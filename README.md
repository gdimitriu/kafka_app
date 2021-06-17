# kafka_app
kafka application with springboot in docker with bitnami kafka server

There are two docker container created from projects kafka_app and kafka_cxf, those will be used into a docker-compose application.

## Kafka_app

[kafka_app](kafka_app/Readme.md)

This is a container using springboot and spring MVC to expose a REST interface to KAFKA.

This container expose the REST API documentation using OpenAPI V3.


## Kafka_cxf

[kafka_cxf](kafka_cxf/Readme.md)


This is a container using springboot and latest CXF to expose REST and SOAP interface to KAFKA.

This container expose the REST API documentation using OpenAPI V3.

This container expose the SOAP API documentation using wsdl.

## BASIC FUNCTIONALITY

 - implementation on java 13 using modules
 - using fabric8.io to generate docker container from maven
 - Create/delete topic
 - POST message to topic
 - GET messages from topic
 - Authentication using basic http using password identical to the user (this is only for testing, for real operation proper user management should be used).
 - plain http requests
 
 
