# CSP > Tech - Code Challenge QA

Projeto de referência para automação de testes (API, Web e Performance) usando Java + Maven.
## Visão geral

Este repositório contém testes automatizados de API (RestAssured), UI (Selenium + JUnit) e scripts de performance (k6 e/ou JMeter conforme disponível). O objetivo é servir como entrega de um desafio técnico com casos, scripts e instruções para execução local e em CI.

## Estrutura principal

```
.
├─ pom.xml                         # Projeto Maven (dependências e plugins)
├─ src/test/java/                  # Testes automatizados (API, Web, etc.)
│  ├─ api/products/CreateProductTest.java
│  └─ web/users/UserRegistrationTest.java
├─ src/test/resources/             # Recursos de teste (ex.: imagens)
└─ k6/                             # Script(s) k6 para testes de performance (opcional)
  └─ test-performance.js

```

## Pré-requisitos

- Java 17+
- Maven 3.6+
- Chrome + ChromeDriver (se for executar testes Web)
- k6 (opcional, para executar scripts em `k6/`)

Observação: este projeto não utiliza JMeter; removi referências a JMeter do README.

## Como executar

1) Instalar dependências e compilar:

```bash
mvn clean install
```

2) Executar todos os testes JUnit (API + Web):

```bash
mvn test
```

3) Executar testes de API ou Web individualmente (exemplos):

```bash
mvn -Dtest=api.products.CreateProductTest test
mvn -Dtest=web.users.UserRegistrationTest test
```

4) Executar testes de performance com k6 (exemplo):

```bash
# Executa o script k6 e salva saída JSON em results/
k6 run --out json=results/performance-results.json k6/test-performance.js
```

## Resultados e relatórios

- Relatórios JUnit: `target/surefire-reports/`
- Artefatos de performance k6: `results/performance-results.json` (quando usado)

## Contrato rápido (inputs / outputs / critérios)

- Inputs: variáveis de ambiente/configurações (URL alvo, credenciais, timeouts).
- Outputs: relatórios JUnit (`target/surefire-reports/`), JSON de performance (`results/`), screenshots em `target/test-classes/` quando aplicável.
- Critérios de sucesso: execução dos testes sem falhas; para performance, latência/throughput dentro dos SLAs definidos nos planos de teste.

## Casos cobertos (resumo)

- API: criação de recurso (produto), validações de status e payload.
- Web: fluxo de cadastro de usuário, upload, alertas, frames e operações de tabela.
- Performance: scripts k6 para análise de carga básica.

## Edge cases a considerar

- Testes dependentes de rede/API indisponível — execute contra um ambiente estável ou utilize mocks.
- Arquivos/outros recursos faltando em `src/test/resources/` — verifique paths relativos.
- Versões de browser incompatíveis com ChromeDriver — mantenha versões compatíveis.

## Troubleshooting rápido

- Erro "chromedriver: command not found": instale o ChromeDriver e adicione ao PATH.
- Erro de versão Java: confirme `java -version` aponta para Java 17+.
- Falhas Maven/Dependências: `mvn -U clean install` para forçar atualização.

## Execução em CI

- Configure Java 17 e Maven no agente.
- Para testes Web, use um executor com Chrome disponível ou um service container (e.g., Selenium Grid / Docker).
- Armazene os artefatos (surefire, JSON k6) como partes do build pipeline para análise.

## Próximos passos sugeridos

1. Centralizar configurações em `application-test.properties` ou variáveis de ambiente.
2. Adicionar um job CI (GitHub Actions / GitLab CI) que rode `mvn test` e salve artefatos.
3. Expandir cobertura de performance e criar dashboards (Grafana / InfluxDB) se necessário.

