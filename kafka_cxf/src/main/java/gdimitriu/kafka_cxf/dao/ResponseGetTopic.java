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
package gdimitriu.kafka_cxf.dao;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JacksonXmlRootElement(localName = "response")
public class ResponseGetTopic {
    @JacksonXmlElementWrapper(localName = "record")
    @JacksonXmlProperty(localName = "record")
    private List<gdimitriu.kafka_cxf.dao.ConsumerRecordWrapper> records;

    public ResponseGetTopic(ConsumerRecords<String,String> recordsKafka) {
        records = new ArrayList<>();
        recordsKafka.forEach(a -> records.add(new gdimitriu.kafka_cxf.dao.ConsumerRecordWrapper(a)));
    }

    public List<gdimitriu.kafka_cxf.dao.ConsumerRecordWrapper> getRecords() {
        return records;
    }

    public void setRecords(List<gdimitriu.kafka_cxf.dao.ConsumerRecordWrapper> records) {
        this.records = records;
    }
}
