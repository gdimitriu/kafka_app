#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:9080/kafka/client/createtopic -d @createTopic.json -u user:userPass
curl -X POST -H "Content-Type:application/json" http://localhost:9080/kafka/client/topics/test -d @first.json -u user:userPass
curl -X GET http://localhost:9080/kafka/client/topics/test/allrecords/myGroupId/myClientId -u user:userPass
curl -X GET http://localhost:9080/kafka/client/topics/test/records/myconsumer/myid/0 -u user:userPass
curl -X DELETE http://localhost:9080/kafka/client/topics/test -u user:userPass
