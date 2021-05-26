/*
 Copyright (c) 2021 Gabriel Dimitriu All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Kafka_cxf project.

 Kafka_cxf is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Kafka_cxf is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Kafka_cxf.  If not, see <http://www.gnu.org/licenses/>.
 */
package gdimitriu.kafka_cxf.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix="kafka")
public class KafkaProperties {
    private static final Logger log = LoggerFactory.getLogger(KafkaProperties.class);

    @Value("${kafka.bootstrap.servers}")
    List<String> bootstrapServers;
    @Value("${kafka.key.serializer}")
    String keySerializer;
    @Value("${kafka.value.serializer}")
    String valueSerializer;
    @Value("${kafka.key.deserializer}")
    String keyDeSerializer;
    @Value("${kafka.value.deserializer}")
    String valueDeSerializer;
    @Value("${kafka.enable.auto.commit}")
    Boolean enableAutoCommit;
    @Value("${kafka.auto.commit.interval.ms}")
    int autoCommitInterval;
    @Value("${kafka.session.timeout.ms}")
    int sessionTimeoutInterval;

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public int getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(int autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    public int getSessionTimeoutInterval() {
        return sessionTimeoutInterval;
    }

    public void setSessionTimeoutInterval(int sessionTimeoutInterval) {
        this.sessionTimeoutInterval = sessionTimeoutInterval;
    }

    public String getKeyDeSerializer() {
        return keyDeSerializer;
    }

    public void setKeyDeSerializer(String keyDeSerializer) {
        this.keyDeSerializer = keyDeSerializer;
    }

    public String getValueDeSerializer() {
        return valueDeSerializer;
    }

    public void setValueDeSerializer(String valueDeSerializer) {
        this.valueDeSerializer = valueDeSerializer;
    }

    @PostConstruct
    public void printMyself() {
        bootstrapServers.forEach(t -> log.info(t));
        log.info("keySerializer:" + keySerializer + " valueSerializer:" + valueSerializer);
    }

    public String getServersList() {
        return bootstrapServers.stream().collect(Collectors.joining(","));
    }

}
