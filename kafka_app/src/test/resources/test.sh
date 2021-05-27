#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:8080/kafka/client/topics/create -d @createTopic.json -u ad:ad
curl -X POST -H "Content-Type:application/json" http://localhost:8080/kafka/client/topics/test -d @first.json -u ad:ad
curl -X GET http://localhost:8080/kafka/client/topics/test/allrecords/myGroupId/myClientId -u ad:ad
curl -X DELETE http://localhost:8080/kafka/client/topics/test -u ad:ad