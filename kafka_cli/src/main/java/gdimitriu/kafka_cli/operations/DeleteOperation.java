package gdimitriu.kafka_cli.operations;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.kafka.KafkaAdminClientFactory;

import java.util.List;

public class DeleteOperation implements Operation {

    private static final Logger log = LogManager.getLogger(DeleteOperation.class);

    @Override
    public void execute(CliArgs args) throws Exception {
        log.info("Deleting topic '{}' on {}", args.topic(), args.bootstrapServer());
        try (AdminClient admin = KafkaAdminClientFactory.create(args)) {
            admin.deleteTopics(List.of(args.topic())).all().get();
            log.info("Topic '{}' deleted", args.topic());
        }
    }
}
