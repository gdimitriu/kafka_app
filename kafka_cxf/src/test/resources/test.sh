#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/createtopic -d @createTopic.json -u user:user
curl -X POST http://localhost:9180/kafka/client/consumers/subscribe/test/myGroupId/myClientId -u user:user
#sleep 2
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/topics/test -d @first.json -u user:user
curl -X POST -H "Content-Type:application/json" http://localhost:9180/kafka/client/topics/test -d @second.json -u user:user
#sleep 1
curl -X GET http://localhost:9180/kafka/client/topics/test/allrecords/myGroupId/myClientId -u user:user
curl -X GET http://localhost:9180/kafka/client/topics/test/records/myGroupId/myClientId/1 -u user:user
curl -X DELETE http://localhost:9180/kafka/client/consumers/subscribe/test/myGroupId/myClientId -u user:user
curl -X DELETE http://localhost:9180/kafka/client/topics/test -u user:user
