#!/usr/bin/env bash

n=1
if [ -n "$1" ]; then
  n=$1
fi

repo="todos"
image="todo-service"
tag="v0.1"

: ${HOST:=$(ipconfig getifaddr en0)}
: ${HOST:=$(ipconfig getifaddr en1)}
: ${HOST:=$(ipconfig getifaddr en2)}
: ${HOST:=$(ipconfig getifaddr en3)}
: ${HOST:=$(ipconfig getifaddr en4)}

echo "Stopping old instances with name: ${image}..."
docker ps -aq --filter name=${image} | xargs docker stop

echo "Removing old instances with name: ${image}..."
docker ps -aq --filter name=${image} | xargs docker rm -f

for i in `seq 1 ${n}`; do
    api_port=801${i}
    cluster_port=255${i}
    echo "Running docker image ${image} with tag ${tag}, instance ${i}, reachable at 127.0.0.1:${api_port}..."

    docker run \
      --detach \
      --name ${image}-${i} \
      --publish ${api_port}:8080 \
      --publish ${cluster_port}:2552 \
      ${repo}/${image}:${tag} \
      -Dtradecloud.kafka.bootstrapServers=${HOST}:9092 \
      -Dconstructr.coordination.nodes.0=${HOST}:2181 \
      -Dcassandra-journal.contact-points.0=${HOST}:9042 \
      -Dcassandra-snapshot-store.contact-points.0=${HOST}:9042
done

