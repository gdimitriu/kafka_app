#!/bin/bash
/home/gaby/teste/kafka_2.12-2.8.0/bin/kafka-topics.sh --create --topic test --partitions 2 --replication-factor 1 --bootstrap-server localhost:9093