package gdimitriu.kafka_cli.operations;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.kafka.KafkaAdminClientFactory;

import java.util.ArrayList;
import java.util.List;

public class ListOperation implements Operation {

    private static final Logger log = LogManager.getLogger(ListOperation.class);

    @Override
    public void execute(CliArgs args) throws Exception {
        log.info("Listing topics on {}", args.bootstrapServer());
        try (AdminClient admin = KafkaAdminClientFactory.create(args)) {
            List<String> topics = new ArrayList<>(admin.listTopics().names().get());
            topics.sort(String::compareTo);
            topics.forEach(System.out::println);
            log.info("Found {} topic(s)", topics.size());
        }
    }
}
