#!/usr/bin/env bash

PORT=$1
CONTAINER=$2

if [[ -z ${PORT} ]]
then
    (>&2 echo "First argument should be a port number")

    exit 1
fi

until echo | nc -z 127.0.0.1 ${PORT}; do
    if [[ -z ${CONTAINER} ]]
    then
        echo "Waiting for 127.0.0.1:$PORT to become reachable..."
    else
        echo "Waiting for $CONTAINER to become reachable on port $PORT..."
    fi
  sleep 1
done