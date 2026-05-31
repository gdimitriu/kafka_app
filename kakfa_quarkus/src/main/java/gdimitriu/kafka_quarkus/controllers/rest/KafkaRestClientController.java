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
package gdimitriu.kafka_quarkus.controllers.rest;

import gdimitriu.kafka_quarkus.dao.RequestCreateTopic;
import gdimitriu.kafka_quarkus.dao.RequestPostTopic;
import gdimitriu.kafka_quarkus.dao.ResponseGetTopic;
import gdimitriu.kafka_quarkus.properties.KafkaProperties;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Path("/kafka/client")
public class KafkaRestClientController {

    private static final Logger log = Logger.getLogger(KafkaRestClientController.class);

    @Inject
    KafkaProperties properties;

    @Inject
    ConsumerRegistry consumerRegistry;

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String getInfo() {
        return properties.bootstrapServers();
    }

    @POST
    @Path("/consumers/subscribe/{topic}/{groupId}/{clientId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response subscribeConsumer(@PathParam("topic") String topicName,
                                      @PathParam("groupId") String groupId,
                                      @PathParam("clientId") String clientId) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.keyDeserializer());
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.valueDeserializer());
        kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.autoCommitIntervalMs());
        kafkaProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.sessionTimeoutMs());
        kafkaProps.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        TopicPartition partition = new TopicPartition(topicName, 0);
        consumer.assign(Arrays.asList(partition));
        if (!consumerRegistry.register(topicName, groupId, clientId, consumer)) {
            consumer.close();
            return Response.status(Response.Status.CONFLICT)
                    .entity("Consumer already subscribed\n").build();
        }
        consumer.poll(Duration.ofMillis(0));
        return Response.ok("subscribed\n").build();
    }

    @DELETE
    @Path("/consumers/subscribe/{topic}/{groupId}/{clientId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response unsubscribeConsumer(@PathParam("topic") String topicName,
                                        @PathParam("groupId") String groupId,
                                        @PathParam("clientId") String clientId) {
        ConsumerRegistry.ConsumerEntry entry = consumerRegistry.get(topicName, groupId, clientId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Consumer not found\n").build();
        }
        entry.lock.lock();
        try {
            consumerRegistry.remove(topicName, groupId, clientId);
            entry.consumer.unsubscribe();
            entry.consumer.close();
        } finally {
            entry.lock.unlock();
        }
        return Response.ok("unsubscribed\n").build();
    }

    @POST
    @Path("/topics/{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postTopic(@PathParam("topic") String topicName, RequestPostTopic dataTopic) {
        log.infof("topic:%s data = %s:%s", topicName, dataTopic.getKey(), dataTopic.getValue());
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.keySerializer());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.valueSerializer());
        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProps)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, dataTopic.getKey(), dataTopic.getValue());
            RecordMetadata result = kafkaProducer.send(record).get();
            log.infof("SendSynchronous topic : %s : %d", result.topic(), result.timestamp());
        } catch (Throwable e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
        return Response.ok("success\n").build();
    }

    @GET
    @Path("/topics/{topic}/records/{groupId}/{clientId}/{offsetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopicRecords(@PathParam("topic") String topicName,
                                    @PathParam("groupId") String groupId,
                                    @PathParam("clientId") String clientId,
                                    @PathParam("offsetId") long offsetId) {
        ConsumerRegistry.ConsumerEntry entry = consumerRegistry.get(topicName, groupId, clientId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ResponseGetTopic("Consumer not subscribed\n")).build();
        }
        entry.lock.lock();
        try {
            KafkaConsumer<String, String> consumer = entry.consumer;
            consumer.poll(Duration.ofMillis(0));
            Collection<TopicPartition> assigned = consumer.assignment();
            if (!assigned.isEmpty()) {
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(assigned);
                for (Map.Entry<TopicPartition, Long> e : endOffsets.entrySet()) {
                    if (e.getValue() > offsetId) {
                        consumer.seek(e.getKey(), offsetId);
                    }
                }
            }
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
            return Response.ok(new ResponseGetTopic(records)).build();
        } finally {
            entry.lock.unlock();
        }
    }

    @GET
    @Path("/topics/{topic}/allrecords/{groupId}/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopicAllRecords(@PathParam("topic") String topicName,
                                       @PathParam("groupId") String groupId,
                                       @PathParam("clientId") String clientId) {
        ConsumerRegistry.ConsumerEntry entry = consumerRegistry.get(topicName, groupId, clientId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ResponseGetTopic("Consumer not subscribed\n")).build();
        }
        Set<TopicPartition> assignment = entry.consumer.assignment();
        if (!assignment.isEmpty()) {
            entry.consumer.seekToBeginning(assignment);
        } else {
            entry.consumer.poll(Duration.ofMillis(0));
            assignment = entry.consumer.assignment();
            if (!assignment.isEmpty()) {
                entry.consumer.seekToBeginning(assignment);
            }
        }
        long timeoutMs = 3000;
        long startTime = System.currentTimeMillis();
        ConsumerRecords<String, String> records = null;
        entry.lock.lock();
        try {
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                records = entry.consumer.poll(Duration.ofMillis(200));
                if (!records.isEmpty()) {
                    break;
                }
            }
            return Response.ok(new ResponseGetTopic(records)).build();
        } finally {
            entry.lock.unlock();
        }
    }

    @POST
    @Path("/topics/{topic}/{numPartition}/{replicationFactor}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createTopic(@PathParam("topic") String topicName,
                                @PathParam("numPartition") Integer numPartitions,
                                @PathParam("replicationFactor") Short replicationFactor) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        try (AdminClient adminClient = AdminClient.create(kafkaProps)) {
            if (adminClient.listTopics().names().get().contains(topicName)) {
                return Response.accepted("Topic already exists\n").build();
            }
            adminClient.createTopics(List.of(new NewTopic(topicName, numPartitions, replicationFactor)));
        } catch (InterruptedException | ExecutionException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
        return Response.ok("topic created\n").build();
    }

    @POST
    @Path("/createtopic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createOneTopic(RequestCreateTopic dataTopic) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        try (AdminClient adminClient = AdminClient.create(kafkaProps)) {
            if (adminClient.listTopics().names().get().contains(dataTopic.getTopicName())) {
                return Response.status(Response.Status.CREATED).entity("Topic already exists\n").build();
            }
            adminClient.createTopics(List.of(new NewTopic(
                    dataTopic.getTopicName(), dataTopic.getNumPartitions(), dataTopic.getReplicationFactor())));
        } catch (InterruptedException | ExecutionException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
        return Response.ok("topic created\n").build();
    }

    @DELETE
    @Path("/topics/{topic}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTopic(@PathParam("topic") String topicName) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        try (AdminClient adminClient = AdminClient.create(kafkaProps)) {
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Topic does not exists\n").build();
            }
            adminClient.deleteTopics(List.of(topicName));
        } catch (InterruptedException | ExecutionException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
        return Response.ok("topic deleted\n").build();
    }

    @GET
    @Path("/infotopic/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response infoOneTopic(@PathParam("topic") String topicName) {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        try (AdminClient adminClient = AdminClient.create(kafkaProps)) {
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                return Response.serverError().entity("Topic does not exist\n").build();
            }
            TopicDescription described = adminClient.describeTopics(List.of(topicName))
                    .allTopicNames().get().get(topicName);
            RequestCreateTopic result = new RequestCreateTopic();
            result.setTopicName(topicName);
            result.setNumPartitions(described.partitions().size());
            result.setReplicationFactor((short) 0);
            return Response.ok(result).build();
        } catch (InterruptedException | ExecutionException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
    }
}
