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
