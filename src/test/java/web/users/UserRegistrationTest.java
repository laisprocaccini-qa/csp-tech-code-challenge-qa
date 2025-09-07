package web.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRegistrationTest {
    private WebDriver driver;
    private static final Logger logger = Logger.getLogger(UserRegistrationTest.class.getName());

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String BASE_URL = "https://demo.automationtesting.in/Register.html";
    private static final String TEST_IMAGE = "src/test/resources/test.png";
    private static final By FIRST_NAME = By.cssSelector("input[placeholder='First Name']");
    private static final By LAST_NAME = By.cssSelector("input[placeholder='Last Name']");
    private static final By EMAIL = By.cssSelector("input[type='email']");
    private static final By PHONE = By.cssSelector("input[type='tel']");
    private static final By GENDER_MALE = By.cssSelector("input[value='Male']");
    private static final By COUNTRIES = By.id("countries");
    private static final By COUNTRY_INDIA_OPTION = By.xpath("//option[text()='India']");
    private static final By PASSWORD = By.id("firstpassword");
    private static final By CONFIRM_PASSWORD = By.id("secondpassword");
    private static final By SUBMIT = By.id("submitbtn");

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    void testUserRegistration() {
        driver.get(BASE_URL);

        waitForPresence(FIRST_NAME);

        fill(FIRST_NAME, "Teste");
        fill(LAST_NAME, "Usuario");
        fill(EMAIL, "teste.usuario@example.com");
        fill(PHONE, "1234567890");
        click(GENDER_MALE);
        click(COUNTRIES);
        click(COUNTRY_INDIA_OPTION);

        fill(PASSWORD, "Password1");
        fill(CONFIRM_PASSWORD, "Password1");

        uploadTestImage();

        WebElement submitButton = driver.findElement(SUBMIT);
        scrollIntoView(submitButton);
        safeClick(submitButton);

        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        boolean success = false;
        try {
            success = wait.until(d -> {
                String url = d.getCurrentUrl();
                String src = d.getPageSource().toLowerCase();
                return !url.contains("Register.html") || src.contains("success") || src.contains("thank");
            });
        } catch (Exception e) {
            logger.fine("Timeout aguardando confirmação de registro: " + e.getMessage());
        }

        assertTrue(success, "Usuário deve ser cadastrado com sucesso");
    }

    // --- Helpers ---
    private void fill(By locator, String text) {
        WebElement el = waitForPresence(locator);
        try {
            el.clear();
        } catch (Exception ignored) {
        }
        el.sendKeys(text);
    }

    private void click(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        safeClick(el);
    }

    private WebElement waitForPresence(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }

    private void safeClick(WebElement el) {
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    /** Upload da imagem de teste; falha no upload não quebra o teste. */
    private void uploadTestImage() {
        try {
            String testImagePath = new File(TEST_IMAGE).getAbsolutePath();
            WebElement fileInput = findFileUploadElement();
            if (fileInput != null) {
                fileInput.sendKeys(testImagePath);
                waitForPresence(By.id("imagetrgt"));
                logger.info("Imagem de teste carregada");
            } else {
                logger.warning("Elemento de upload de arquivo não encontrado");
            }
        } catch (Exception e) {
            logger.warning("Erro no upload da imagem (não falha o teste): " + e.getMessage());
        }
    }

    private WebElement findFileUploadElement() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (WebElement) js.executeScript("return document.querySelector(\"input[type='file']\");");
        } catch (Exception e) {
            logger.fine("Não foi possível localizar input de arquivo: " + e.getMessage());
            return null;
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
