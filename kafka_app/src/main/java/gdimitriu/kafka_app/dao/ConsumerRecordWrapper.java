package gdimitriu.kafka_app.dao;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
