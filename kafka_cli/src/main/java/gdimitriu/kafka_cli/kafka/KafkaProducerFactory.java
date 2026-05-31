package gdimitriu.kafka_cli.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import gdimitriu.kafka_cli.cli.CliArgs;

import java.util.Properties;

public class KafkaProducerFactory {

    public static KafkaProducer<String, String> create(CliArgs args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, args.bootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // compression=none is mandatory for GraalVM native-image: JNI codecs (lz4/snappy/zstd)
        // extract native libraries at runtime, which is incompatible with native-image closed-world
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return new KafkaProducer<>(props);
    }
}
