## KAFKA_CXF

This is a frontend for kafka using REST and SOAP.

It is based on springboot and apache CXF.

The maven install will create a docker container which will be use 
in the docker compose from higher level kafka_app/dockers/kafka_cxf_spring

The environment variable are set in docker compose: 
exposeRestPort=9180
exposeWSPort=9182
kafkaPort=kafka:9092 this is the internal port, otherwise if is used standalone should be localhost or the host of the kafka

In the test/resources are the tests to be run (REST and SOAP).

All requests except the openAPI and WSDL are protected by basic auth using user:user .

The REST provide openAPI and SOAP provide WDSL.