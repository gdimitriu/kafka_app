#!/bin/bash
curl -X POST -H "Content-Type:application/json" http://localhost:1080/kafka/client/topics/test -d @first.json -u ad:ad
curl -X GET http://localhost:1080/kafka/client/topics/test/records/myGroupId/myClientId -u ad:ad