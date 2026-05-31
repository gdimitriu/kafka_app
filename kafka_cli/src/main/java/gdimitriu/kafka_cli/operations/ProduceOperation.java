package gdimitriu.kafka_cli.operations;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.kafka.KafkaProducerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class ProduceOperation implements Operation {

    private static final Logger log = LogManager.getLogger(ProduceOperation.class);

    @Override
    public void execute(CliArgs args) throws Exception {
        log.info("Producing messages from '{}' to topic '{}' on {}",
                args.file(), args.topic(), args.bootstrapServer());

        long count = 0;
        try (KafkaProducer<String, String> producer = KafkaProducerFactory.create(args);
             BufferedReader reader = new BufferedReader(
                     new FileReader(args.file(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                ProducerRecord<String, String> record = new ProducerRecord<>(args.topic(), null, line);
                // Synchronous send: ensures ordering and surfaces errors immediately
                producer.send(record).get();
                count++;
            }
            producer.flush();
        }
        log.info("Produced {} message(s) to topic '{}'", count, args.topic());
    }
}
