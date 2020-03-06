#!/bin/sh

echo "Configuring toxiproxy..."

curl -X POST toxiproxy:8474/proxies -d "{\"name\": \"textproxy\", \"listen\": \"0.0.0.0:$PROXY_PORT\", \"upstream\": \"textserver:$SERVER_PORT\"}"

curl -X POST toxiproxy:8474/proxies/textproxy/toxics -d '{"type": "limit_data", "stream": "upstream", "toxicity": 0.3, "attributes":{"bytes": 1}}'
curl -X POST toxiproxy:8474/proxies/textproxy/toxics -d '{"type": "latency", "stream": "upstream", "toxicity": 0.3, "attributes":{"latency":5000, "jitter":3000}}'
# Only delays confirmation
#curl -X POST toxiproxy:8474/proxies/textproxy/toxics -d '{"type": "slow_close", "stream": "upstream", "toxicity": 0.3, "attributes":{"delay": 5000}}'

echo "done."
