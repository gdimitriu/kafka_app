#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:8180/kafka/client/topics/create -d @createTopic.json
curl -X POST -H "Content-Type:application/json" http://localhost:8180/kafka/client/topics/test -d @first.json
curl -X GET http://localhost:8180/kafka/client/topics/test/allrecords/myGroupId/myClientId
curl -X GET http://localhost:8180/kafka/client/topic/test/myconsumer/myid
curl -X DELETE http://localhost:8180/kafka/client/topics/test