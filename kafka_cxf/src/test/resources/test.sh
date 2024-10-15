#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/createtopic -d @createTopic.json -u user:user
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/topics/test -d @first.json -u user:user
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/topics/test -d @first.json -u user:user
sleep 30
curl -X GET http://localhost:9180/kafka/client/topics/test/allrecords/myGroupId/myClientId -u user:user
curl -X GET http://localhost:9180/kafka/client/topics/test/records/myconsumer/myid/0 -u user:user
curl -X DELETE http://localhost:9180/kafka/client/topics/test -u user:user
