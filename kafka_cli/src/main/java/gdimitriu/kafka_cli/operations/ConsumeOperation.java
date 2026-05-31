package gdimitriu.kafka_cli.operations;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.kafka.KafkaConsumerFactory;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ConsumeOperation implements Operation {

    private static final Logger log = LogManager.getLogger(ConsumeOperation.class);
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(5);
    // Stop after this many consecutive empty polls
    private static final int MAX_EMPTY_POLLS = 3;

    @Override
    public void execute(CliArgs args) throws Exception {
        log.info("Consuming from topic '{}' starting at offset {} on {}",
                args.topic(), args.offset(), args.bootstrapServer());

        KafkaConsumer<String, String> consumer = KafkaConsumerFactory.create(args);

        // Shutdown hook: wakeup() causes the poll() to throw WakeupException cleanly
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup, "consumer-shutdown"));

        PrintWriter writer = args.file() != null
                ? new PrintWriter(new FileOutputStream(args.file()), true, StandardCharsets.UTF_8)
                : new PrintWriter(System.out, true, StandardCharsets.UTF_8);

        long lastOffset = args.offset() - 1;

        try {
            // Use assign+seek instead of subscribe: gives direct offset control without
            // involving a group coordinator or committing anything
            List<TopicPartition> partitions = consumer.partitionsFor(args.topic()).stream()
                    .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                    .collect(Collectors.toList());

            if (partitions.isEmpty()) {
                log.warn("Topic '{}' has no partitions or does not exist", args.topic());
                return;
            }

            consumer.assign(partitions);
            for (TopicPartition tp : partitions) {
                consumer.seek(tp, args.offset());
            }

            int emptyPolls = 0;
            while (emptyPolls < MAX_EMPTY_POLLS) {
                ConsumerRecords<String, String> records = consumer.poll(POLL_TIMEOUT);
                if (records.isEmpty()) {
                    emptyPolls++;
                    continue;
                }
                emptyPolls = 0;
                for (ConsumerRecord<String, String> record : records) {
                    writer.println(record.value());
                    lastOffset = record.offset();
                }
            }
        } catch (WakeupException e) {
            // Expected on shutdown hook or Ctrl+C
            log.debug("Consumer woken up, stopping");
        } finally {
            consumer.close();
            if (args.file() != null) {
                writer.close();
            }
        }

        // Print last consumed offset to stdout (always, even when writing records to a file)
        System.out.println("Last offset: " + lastOffset);
        log.info("Consume finished. Last offset: {}", lastOffset);
    }
}
