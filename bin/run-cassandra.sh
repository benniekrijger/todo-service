#!/usr/bin/env bash

echo "Removing old cassandra instances..."

docker ps -aq --filter name=cassandra | xargs docker rm -f

echo "Running new cassandra instance..."

docker run \
  --detach \
  --name cassandra \
  --publish 9042:9042 \
  cassandra:3.9

echo "Waiting for cassandra to boot..."
sleep 10