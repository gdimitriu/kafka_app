package gdimitriu.kafka_cli.operations;

import gdimitriu.kafka_cli.cli.CliArgs;

public interface Operation {
    void execute(CliArgs args) throws Exception;
}
