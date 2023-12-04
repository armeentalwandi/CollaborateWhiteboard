#!/bin/bash

docker build -t appengers-server-local .
docker run -p 8080:8080 appengers-server-local