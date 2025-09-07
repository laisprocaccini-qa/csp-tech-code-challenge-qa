# Plano de Testes de Performance (resumo)

Objetivo: validar latência, throughput e taxa de erro de endpoints usando k6.

Arquivos
- `k6/test-performance.js` — script de teste
- `results/performance-results.json` — saída gerada pelo k6

Pré-requisitos
- k6 instalado (macOS: `brew install k6`)

Execução rápida

```bash
mkdir -p results
k6 run --out json=results/performance-results.json k6/test-performance.js
```

Métricas e thresholds (exemplo)
- Latência p95: p(95) < 5000 ms
- Taxa de erro: errors.rate < 0.10
- http_req_failed < 0.01

Troubleshooting rápido
- "connection reset by peer": diminuir VUs ou aumentar ramp-up; rodar de outro local; verificar rate limit/WAF.
- Falha frequente: aumentar retries/backoff no script ou reduzir agressividade do stage.

CI (resumo)
- No CI: instalar k6, rodar o script e subir `results/performance-results.json` como artifact.
