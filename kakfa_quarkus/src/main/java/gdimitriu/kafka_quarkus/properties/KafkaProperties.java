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
package gdimitriu.kafka_quarkus.properties;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "kafka")
public interface KafkaProperties {

    @WithName("bootstrap.servers")
    String bootstrapServers();

    @WithName("key.serializer")
    String keySerializer();

    @WithName("value.serializer")
    String valueSerializer();

    @WithName("key.deserializer")
    String keyDeserializer();

    @WithName("value.deserializer")
    String valueDeserializer();

    @WithName("enable.auto.commit")
    boolean enableAutoCommit();

    @WithName("auto.commit.interval.ms")
    int autoCommitIntervalMs();

    @WithName("session.timeout.ms")
    int sessionTimeoutMs();
}
