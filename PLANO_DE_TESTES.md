# Plano de Testes

## 1. Visão Geral

Este documento descreve o plano de testes para o projeto. Cobre objetivos, escopo, ambientes, estratégias, casos de teste (API, Web, Performance), dados de teste, critérios de aceitação e geração de relatórios.

## 2. Objetivos

- Verificar funcionalidade e estabilidade das features implementadas.
- Validar contratos de API (status, esquema e conteúdo).
- Validar fluxos principais de UI (cadastro, upload, alertas, frames, tabelas).
- Medir performance básica do endpoint de posts com k6.
- Gerar evidências (relatórios JUnit e JSON do k6).

## 3. Escopo

Inclui testes automatizados presentes no repositório:

- API: `src/test/java/api/products/CreateProductTest.java`
- Web: `src/test/java/web/users/UserRegistrationTest.java`
- Performance: `k6/test-performance.js`

Fora do escopo: testes manuais extensivos e integração contínua com monitoramento (a menos que configurado no CI).

## 4. Ambientes

- Execução local: máquina do desenvolvedor (Java 17, Maven). Para UI: Chrome + ChromeDriver.
- Ambiente de teste remoto: variável/configurável via propriedades ou variáveis de ambiente.
- Pré-requisito para performance: k6 instalado (opcional se não executar performance).

## 5. Estratégia de Teste

- API: RestAssured + JUnit. Validar status, headers e payload.
- UI: Selenium + JUnit em Chrome (headless em CI, se necessário).
- Performance: k6 com stages e thresholds.
- Evidências: relatórios JUnit (surefire) e JSON do k6.

## 6. Casos de Teste Principais

### 6.1 API — Criação de Produto

- Objetivo: validar criação via POST `/products`.
- Pré-condições: endpoint configurado; conectividade.
- Dados: payload JSON com campos obrigatórios.
- Passos:
  1. POST /products com payload válido.
  2. Verificar status (201/200) e JSON de resposta (id e campos).
  3. (Opcional) Deletar recurso criado.
- Critério: resposta compatível com contrato.

### 6.2 Web — Cadastro de Usuário

- Objetivo: validar fluxo de cadastro.
- Pré-condições: Chrome + ChromeDriver.
- Passos:
  1. Abrir página de registro.
  2. Preencher campos obrigatórios.
  3. (Se aplicável) Fazer upload de arquivo.
  4. Submeter e validar sucesso (mensagem/redirect).
- Critério: fluxo completo sem erros.

### 6.3 Web — Upload / Alertas / Frames / Tabelas

- Upload: anexar `src/test/resources/test.png`, submeter e verificar feedback.
- Alertas: acionar alerta JS e validar texto/ação.
- Frames: trocar para iframe e interagir com elementos.
- Tabelas: validar ordenação/filtragem e conteúdo.

### 6.4 Performance — GET /posts (k6)

- Objetivo: medir latência, throughput e erros do endpoint `/posts`.
- Pré-condições: k6 instalado (se executar localmente).
- Execução:

```bash
mkdir -p results
k6 run --out json=results/performance-results.json k6/test-performance.js
```

- Métricas chave: `http_reqs`, `http_req_duration` (p95), `errors`.
- Thresholds sugeridos (exemplo): p(95) < 5000 ms; errors.rate < 0.10.

## 7. Dados de Teste

- Preferir dados descartáveis ou mocks.
- Arquivos de upload: `src/test/resources/`.
- Não armazenar segredos no repositório; usar variáveis de ambiente em CI.

## 8. Execução e Agendamento

- Local: `mvn test` para suite automatizada.
- Performance: executar k6 manualmente ou via job de CI acionável.
- CI: adicionar job para `mvn test` em PRs; job de performance opcional acionável.

## 9. Relatórios e Evidências

- JUnit: `target/surefire-reports/` (XML/TXT).
- Performance: `results/performance-results.json`.
- Em caso de falha: coletar screenshots e logs.

