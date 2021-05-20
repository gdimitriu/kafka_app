/*
 Copyright (c) 2021 Gabriel Dimitriu All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Kafka_app project.

 Kafka_app is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Kafka_app is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Kafka_app.  If not, see <http://www.gnu.org/licenses/>.
 */
package gdimitriu.kafka_app.dao;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.List;

public class ResponseGetTopic {
    private List<ConsumerRecordWrapper> records;

    public ResponseGetTopic(ConsumerRecords<String,String> recordsKafka) {
        records = new ArrayList<>();
        recordsKafka.forEach(a -> records.add(new ConsumerRecordWrapper(a)));
    }

    public List<ConsumerRecordWrapper> getRecords() {
        return records;
    }

    public void setRecords(List<ConsumerRecordWrapper> records) {
        this.records = records;
    }
}
