import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 100 }, // Ramp-up to 100 virtual users over 30 seconds
        { duration: '1m', target: 100 },  // Stay at 100 virtual users for 1 minute
        { duration: '10s', target: 0 },   // Ramp-down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
    },
};

export default function () {
    const res = http.get('http://localhost:8080/api/products');

    // Check if the request was successful (status 200) or rate-limited (status 429).
    check(res, {
        'is status 200 or 429': (r) => r.status === 200 || r.status === 429,
    });

    // Wait for 1 second before the next request.
    sleep(1);
}