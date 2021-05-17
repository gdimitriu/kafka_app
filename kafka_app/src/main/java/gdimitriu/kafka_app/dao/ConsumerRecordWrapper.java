package gdimitriu.kafka_app.dao;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Data
public class ConsumerRecordWrapper {

    private String topic;
    private int partition;
    private long offset;
    private long timestamp;
    private String key;
    private String value;

    public ConsumerRecordWrapper(ConsumerRecord<String, String> consumerRecord) {
        topic = consumerRecord.topic();
        partition = consumerRecord.partition();
        offset = consumerRecord.offset();
        timestamp = consumerRecord.timestamp();
        key = consumerRecord.key();
        value = consumerRecord.value();
    }
}