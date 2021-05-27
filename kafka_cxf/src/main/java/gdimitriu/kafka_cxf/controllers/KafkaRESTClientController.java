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
package gdimitriu.kafka_cxf.controllers;

import gdimitriu.kafka_cxf.dao.RequestCreateTopic;
import gdimitriu.kafka_cxf.dao.RequestPostTopic;
import gdimitriu.kafka_cxf.dao.ResponseGetTopic;
import gdimitriu.kafka_cxf.properties.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Path("/kafka/client")
@Component
public class KafkaRESTClientController {
    private static final Logger log = LoggerFactory.getLogger(KafkaRESTClientController.class);
    @Autowired
    private KafkaProperties properties;

    @GET
    @Path("/info")
    public String getInfo() {
        return properties.getServersList();
    }

    @GET
    @Path("/topics/{topic}/records/{groupId}/{clientId}/{offsetId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTopicRecords(@Valid @PathParam("topic") String topicName,
                                                            @Valid @PathParam("groupId") String groupId,
                                                            @Valid @PathParam("clientId") String clientId,
                                                            @Valid @PathParam("offsetId") long offsetId) {
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
        Response response = Response.ok(new ResponseGetTopic(records)).build();
        consumer.unsubscribe();
        return response;
    }

    @GET
    @Path("/topics/{topic}/allrecords/{groupId}/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopicAllRecords(@Valid @PathParam("topic") String topicName,
                                                               @Valid @PathParam("groupId") String groupId,
                                                               @Valid @PathParam("clientId") String clientId) {
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
        Response response = Response.ok(new ResponseGetTopic(records)).build();
        consumer.unsubscribe();
        return response;
    }

    @POST
    @Path("/topics/{topic}/{numPartition}/{replicationFactor}")
    @Produces(MediaType.WILDCARD)
    public Response createTopic(@Valid @PathParam("topic") String topicName,
                                         @Valid @PathParam("numPartition") Integer numPartitions,
                                         @Valid @PathParam("replicationFactor") Short replicationFactor) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (topics.names().get().contains(topicName)) {
                return Response.accepted("Topic already exists\n").build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);
        adminClient.createTopics(newTopics);
        adminClient.close();
        return Response.ok("topic created\n").build();
    }

    @POST
    @Path("/createtopic")
    @Produces(MediaType.WILDCARD)
    @Consumes("application/json")
    public Response createOneTopic(@Valid RequestCreateTopic dataTopic) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (topics.names().get().contains(dataTopic.getTopicName())) {
                return Response.accepted("Topic already exists\n").build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        NewTopic newTopic = new NewTopic(dataTopic.getTopicName(), dataTopic.getNumPartitions(), dataTopic.getReplicationFactor());
        List<NewTopic> newTopics = new ArrayList<NewTopic>();
        newTopics.add(newTopic);
        adminClient.createTopics(newTopics);
        adminClient.close();
        return Response.ok("topic created\n").build();
    }

    @DELETE
    @Path("/topics/{topic}")
    @Produces(MediaType.WILDCARD)
    public Response deleteTopic(@Valid @PathParam("topic") String topicName) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServersList());
        AdminClient adminClient = AdminClient.create(kafkaProps);
        ListTopicsResult topics = adminClient.listTopics();
        try {
            if (!topics.names().get().contains(topicName)) {
                return Response.notModified("Topic does not exists\n").build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        adminClient.deleteTopics(Arrays.asList(topicName));
        adminClient.close();
        return Response.ok("topic deleted\n").build();
    }

    @GET
    @Path("/infotopic/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response infoOneTopic(@Valid @PathParam("topic") String topicName) {
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
                return Response.ok(result).build();
            } else {
                return Response.notModified("Topic does not exist\n").build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
    @POST
    @Path("/topics/{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTopic(@PathParam("topic") String topicName, @Valid RequestPostTopic dataTopic) {
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
            return Response.serverError().build();
        }
        return Response.ok("success\n").build();
    }
}