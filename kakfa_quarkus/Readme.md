## KAFKA_QUARKUS

This is a frontend for kafka using REST.

It is based on quarkus and mvc.

The maven install will create a docker container which will be use
in the docker compose from higher level kafka_quarkus/dockers/kafka_cxf_all

The environment variable are set in docker compose:
exposeRestPort=9084
kafkaPort=kafka:9094 this is the internal port, otherwise if is used standalone should be localhost or the host of the kafka

In the test/resources are the tests to be run for REST.

The openAPI is provided as swagger.

All requests except the openAPI is protected by basic auth using user:user .