#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:9184/kafka/client/createtopic -d @createTopic.json -u user:userPass
curl -X POST http://localhost:9184/kafka/client/consumers/subscribe/test/myGroupId/myClientId -u user:userPass
curl -X POST -H "Content-Type:application/json" http://localhost:9184/kafka/client/topics/test -d @first.json -u user:userPass
curl -X POST -H "Content-Type:application/json" http://localhost:9184/kafka/client/topics/test -d @second.json -u user:userPass
curl -X GET http://localhost:9184/kafka/client/topics/test/allrecords/myGroupId/myClientId -u user:userPass
curl -X GET http://localhost:9184/kafka/client/topics/test/records/myGroupId/myClientId/1 -u user:userPass
curl -X DELETE http://localhost:9184/kafka/client/consumers/subscribe/test/myGroupId/myClientId -u user:userPass
curl -X DELETE http://localhost:9184/kafka/client/topics/test -u user:userPass
