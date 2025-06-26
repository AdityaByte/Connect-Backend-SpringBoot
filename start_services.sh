#!/usr/bin/bash

KAFKA_LOCATION=/f/kafka/bin
LOG_DIR=$(pwd)
PID_DIR="$LOG_DIR/pids"

echo "Starting services..."

echo "Starting mongodb.."
net start mongodb

mkdir -p "$LOG_DIR/log"
mkdir -p "$PID_DIR"

echo "Starting Kafka Zookeeper..."
cd "$KAFKA_LOCATION"
./zookeeper-server-start.sh ../config/zookeeper.properties > "$LOG_DIR/log/zookeeper.log" 2>&1 &
echo $! > "$PID_DIR/zookeeper.pid"

sleep 10 # Waiting till the zookeeper server is fully started.

echo "Starting Kafka Server..."
./kafka-server-start.sh ../config/server.properties > "$LOG_DIR/log/kafka.log" 2>&1 &
echo $! > "$PID_DIR/kafka.pid"

echo "All services started logs are in $LOG_DIR/log"