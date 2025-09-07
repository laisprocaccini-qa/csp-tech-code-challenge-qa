import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('errors');
const responseTimeThreshold = new Trend('response_time_threshold');
const ttfb = new Trend('time_to_first_byte');

const baseUrl = 'https://jsonplaceholder.typicode.com';

export const options = {
  stages: [
    { duration: '1s', target: 10 },
    { duration: '1s', target: 100 },
    { duration: '1m', target: 500 },
    { duration: '1m', target: 1000 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    'http_req_duration': ['p(95)<5000'],
    'errors': [{ threshold: 'rate<0.10', abortOnFail: true }],
    'http_req_failed': [{ threshold: 'rate<0.01', abortOnFail: true }],
  },
};

export default function () {
  const response = http.get(`${baseUrl}/posts`, {
    tags: { name: 'GET /posts' },
  });

  const success = check(response, {
    'Status é 200': (r) => r.status === 200,
    'Resposta contém dados': (r) => r.body.length > 0,
    'Tempo de resposta < 5s': (r) => r.timings.duration < 5000,
    'Content-Type correto': (r) => r.headers['Content-Type']?.includes('application/json'),
  });

  errorRate.add(!success);

  responseTimeThreshold.add(response.timings.duration);
  ttfb.add(response.timings.waiting);

  if (!success) {
    console.error(`❌ Falha na request [${response.status}] para ${response.url}`);
  }

  sleep(Math.random() * 2);
}
