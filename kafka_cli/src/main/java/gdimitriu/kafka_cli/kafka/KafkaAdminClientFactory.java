package gdimitriu.kafka_cli.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import gdimitriu.kafka_cli.cli.CliArgs;

import java.util.Properties;

public class KafkaAdminClientFactory {

    public static AdminClient create(CliArgs args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, args.bootstrapServer());
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "30000");
        return AdminClient.create(props);
    }
}
