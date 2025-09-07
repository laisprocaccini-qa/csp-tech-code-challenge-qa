package api.products;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateProductTest {
  private static final String BASE_URI = "https://dummyjson.com";
  private static final String BASE_PATH = "/products/add";

  @Test
  void testCreateProduct() {
    Map<String, Object> payload = new HashMap<>();
    payload.put("title", "Produto Teste");
    payload.put("description", "Descrição do produto teste");
    payload.put("price", 99.99);
    payload.put("category", "electronics");

    Response response = RestAssured.given()
        .baseUri(BASE_URI)
        .basePath(BASE_PATH)
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post()
        .then()
        .log().ifValidationFails()
        .extract()
        .response();

    assertEquals(201, response.getStatusCode(), "Status deve ser 201");

    int id = response.jsonPath().getInt("id");
    String title = response.jsonPath().getString("title");
    String description = response.jsonPath().getString("description");
    double price = response.jsonPath().getDouble("price");
    String category = response.jsonPath().getString("category");

    assertAll("Validar produto criado",
        () -> assertTrue(id > 0, "ID gerado deve ser maior que zero"),
        () -> assertEquals("Produto Teste", title),
        () -> assertEquals("Descrição do produto teste", description),
        () -> assertEquals(99.99, price, 0.01),
        () -> assertEquals("electronics", category)
    );
  }
}
