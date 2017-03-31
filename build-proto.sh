#!/bin/bash

protoc --proto_path=src/main/proto --java_out=src/main/java src/main/proto/trading.proto