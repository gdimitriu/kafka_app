package gdimitriu.kafka_app.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ConfigurationProperties(prefix="kafka")
public class KafkaProperties {

    @Value("${kafka.bootstrap.servers}")
    List<String> bootstrapServers;

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @PostConstruct
    public void printMyself() {
        bootstrapServers.forEach(t -> log.info(t));
    }

    public String getServersList() {
        return bootstrapServers.stream().collect(Collectors.joining(","));
    }
}
