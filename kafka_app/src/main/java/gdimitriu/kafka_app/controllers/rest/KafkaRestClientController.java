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

import gdimitriu.kafka_app.dao.RequestCreateTopic;
import gdimitriu.kafka_app.dao.RequestPostTopic;
import gdimitriu.kafka_app.dao.ResponseGetTopic;
import gdimitriu.kafka_app.properties.KafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/kafka/client")
public class KafkaRestClientController {
    private static final Logger log = LoggerFactory.getLogger(KafkaRestClientController.class);
    @Autowired
    private KafkaProperties properties;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String getInfo() {
        return properties.getServersList();
    }

    @RequestMapping(value = "/topics/{topic}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> postTopic(@PathVariable("topic") String topicName, @Valid @RequestBody  RequestPostTopic dataTopic) {
        log.info("topic:" + topicName + " data = " + dataTopic.getKey() + ":" + dataTopic.getValue());
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        kafkaProps.put("key.serializer", properties.getKeySerializer());
        kafkaProps.put("value.serializer", properties.getValueSerializer());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProps);
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, dataTopic.getKey(), dataTopic.getValue());
        try {
            RecordMetadata result = kafkaProducer.send(record).get();
            log.info("SendSynchronous topic : " + result.topic() + " : " + result.timestamp());
        } catch (Throwable e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{topic}/records/{groupId}/{clientId}/{offsetId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseGetTopic> getTopicRecords(@Valid @PathVariable("topic") String topicName,
                                                            @Valid @PathVariable("groupId") String groupId,
                                                            @Valid @PathVariable("clientId") String clientId,
                                                            @Valid @PathVariable("offsetId") long offsetId) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        kafkaProps.put("key.deserializer", properties.getKeyDeSerializer());
        kafkaProps.put("value.deserializer", properties.getValueDeSerializer());
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit());
        kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.getAutoCommitInterval());
        kafkaProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeoutInterval());
        kafkaProps.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Arrays.asList(topicName), new HandleRebalance(consumer, offsetId));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        ResponseEntity<ResponseGetTopic> response = new ResponseEntity<>(new ResponseGetTopic(records), HttpStatus.OK);
        consumer.unsubscribe();
        return response;
    }
    @RequestMapping(value = "/topics/{topic}/allrecords/{groupId}/{clientId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseGetTopic> getTopicAllRecords(@Valid @PathVariable("topic") String topicName,
                                                            @Valid @PathVariable("groupId") String groupId,
                                                            @Valid @PathVariable("clientId") String clientId) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        kafkaProps.put("key.deserializer", properties.getKeyDeSerializer());
        kafkaProps.put("value.deserializer", properties.getValueDeSerializer());
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit());
        kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.getAutoCommitInterval());
        kafkaProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeoutInterval());
        kafkaProps.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        ResponseEntity<ResponseGetTopic> response = new ResponseEntity<>(new ResponseGetTopic(records), HttpStatus.OK);
        consumer.unsubscribe();
        return response;
    }

    @RequestMapping(value = "/topics/{topic}/{numPartition}/{replicationFactor}", method = RequestMethod.POST, produces = {MediaType.ALL_VALUE})
    public ResponseEntity<?> createTopic(@Valid @PathVariable("topic") String topicName,
                                                               @Valid @PathVariable("numPartition") Integer numPartitions,
                                                               @Valid @PathVariable("replicationFactor") Short replicationFactor) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (topics.names().get().contains(topicName)) {
                return new ResponseEntity<>("Topic already exists", HttpStatus.CREATED);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);
        adminClient.createTopics(newTopics);
        adminClient.close();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/createtopic", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.ALL_VALUE})
    public ResponseEntity<?> createOneTopic(@Valid @RequestBody RequestCreateTopic dataTopic) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (topics.names().get().contains(dataTopic.getTopicName())) {
                return new ResponseEntity<>("Topic already exists", HttpStatus.CREATED);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        NewTopic newTopic = new NewTopic(dataTopic.getTopicName(), dataTopic.getNumPartitions(), dataTopic.getReplicationFactor());
        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);
        adminClient.createTopics(newTopics);
        adminClient.close();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{topic}", method = RequestMethod.DELETE, produces = {MediaType.ALL_VALUE})
    public ResponseEntity<?> deleteTopic(@Valid @PathVariable("topic") String topicName) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (!topics.names().get().contains(topicName)) {
                return new ResponseEntity<>("Topic does not exists", HttpStatus.NOT_FOUND);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        adminClient.deleteTopics(Arrays.asList(topicName));
        adminClient.close();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/infotopic/{topic}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createOneTopic(@Valid @PathVariable("topic") String topicName) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (topics.names().get().contains(topicName)) {
                RequestCreateTopic result = new RequestCreateTopic();
                TopicDescription described = adminClient.describeTopics(Arrays.asList(topicName))
                        .all().get().get(topicName);
                result.setTopicName(topicName);
                result.setNumPartitions(described.partitions().size());
                result.setReplicationFactor((short) 0);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Topic does not exist\n", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
