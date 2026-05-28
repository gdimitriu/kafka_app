/*
 Copyright (c) 2021 Gabriel Dimitriu All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Kafka_app project.

 Kafka_app is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Kafka_app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Kafka_app.  If not, see <http://www.gnu.org/licenses/>.
 */
package gdimitriu.kafka_app.controllers.rest;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ConsumerRegistry {

    public static class ConsumerEntry {
        public final KafkaConsumer<String, String> consumer;
        public final ReentrantLock lock;

        ConsumerEntry(KafkaConsumer<String, String> c) {
            this.consumer = c;
            this.lock = new ReentrantLock();
        }
    }

    private final ConcurrentHashMap<String, ConsumerEntry> registry = new ConcurrentHashMap<>();

    private static String key(String topic, String groupId, String clientId) {
        return topic + "::" + groupId + "::" + clientId;
    }

    public boolean register(String topic, String groupId, String clientId,
                            KafkaConsumer<String, String> consumer) {
        return registry.putIfAbsent(key(topic, groupId, clientId),
                                    new ConsumerEntry(consumer)) == null;
    }

    public ConsumerEntry get(String topic, String groupId, String clientId) {
        return registry.get(key(topic, groupId, clientId));
    }

    public boolean remove(String topic, String groupId, String clientId) {
        return registry.remove(key(topic, groupId, clientId)) != null;
    }
}
