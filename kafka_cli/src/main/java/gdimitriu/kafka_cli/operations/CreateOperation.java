package gdimitriu.kafka_cli.operations;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.kafka.KafkaAdminClientFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateOperation implements Operation {

    private static final Logger log = LogManager.getLogger(CreateOperation.class);

    @Override
    public void execute(CliArgs args) throws Exception {
        log.info("Creating topic '{}' on {}", args.topic(), args.bootstrapServer());
        try (AdminClient admin = KafkaAdminClientFactory.create(args)) {
            NewTopic newTopic = new NewTopic(args.topic(), 1, (short) 1);
            try {
                admin.createTopics(List.of(newTopic)).all().get();
                log.info("Topic '{}' created", args.topic());
            } catch (ExecutionException e) {
                if (e.getCause() instanceof TopicExistsException) {
                    log.warn("Topic '{}' already exists", args.topic());
                } else {
                    throw e;
                }
            }
        }
    }
}
