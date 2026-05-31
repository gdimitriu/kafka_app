package gdimitriu.kafka_cli.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import gdimitriu.kafka_cli.cli.CliArgs;

import java.util.Properties;
import java.util.UUID;

public class KafkaConsumerFactory {

    public static KafkaConsumer<String, String> create(CliArgs args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, args.bootstrapServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Random group.id: we use assign()+seek() and never commit, so the group coordinator
        // is not involved and commit state is irrelevant
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-cli-" + UUID.randomUUID());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // none: fail fast if no offset is committed (we always seek explicitly)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "none");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        return new KafkaConsumer<>(props);
    }
}
