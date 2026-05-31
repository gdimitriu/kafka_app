package gdimitriu.kafka_cli.cli;

public class CliArgs {

    public enum Op { list, create, delete, produce, consume }

    private String bootstrapServer;
    private Op operation;
    private String topic;
    private String file;
    private long offset = 0;
    private boolean helpRequested;

    private CliArgs() {}

    public static CliArgs parse(String[] args) {
        CliArgs a = new CliArgs();
        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            switch (arg) {
                case "-h" -> a.helpRequested = true;
                case "-O" -> { i++; a.operation = parseOp(args[i]); }
                case "-t" -> { i++; a.topic = args[i]; }
                case "-f" -> { i++; a.file = args[i]; }
                case "-o" -> { i++; a.offset = Long.parseLong(args[i]); }
                default -> {
                    if (!arg.startsWith("-") && a.bootstrapServer == null) {
                        a.bootstrapServer = arg;
                    } else {
                        throw new IllegalArgumentException("Unknown argument: " + arg);
                    }
                }
            }
            i++;
        }

        if (a.helpRequested) {
            printHelp();
            System.exit(0);
        }

        a.validate();
        return a;
    }

    private static Op parseOp(String s) {
        try {
            return Op.valueOf(s.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unknown operation: '" + s + "'. Must be one of: list, create, delete, produce, consume");
        }
    }

    private void validate() {
        if (bootstrapServer == null) {
            throw new IllegalArgumentException("Kafka bootstrap server <url:port> is required");
        }
        if (operation == null) {
            throw new IllegalArgumentException("-O <operation> is required");
        }
        if ((operation == Op.create || operation == Op.delete ||
             operation == Op.produce || operation == Op.consume) && topic == null) {
            throw new IllegalArgumentException("-t <topic> is required for operation: " + operation);
        }
        if (operation == Op.produce && file == null) {
            throw new IllegalArgumentException("-f <input-file> is required for produce operation");
        }
    }

    public static void printHelp() {
        System.out.println("Usage: kafka-cli <url:port> -O <operation> [options]");
        System.out.println();
        System.out.println("  <url:port>          Kafka bootstrap server (e.g. localhost:9092)");
        System.out.println("  -O <operation>      One of: list, create, delete, produce, consume");
        System.out.println("  -t <topic>          Topic name (required for create/delete/produce/consume)");
        System.out.println("  -f <file>           Input file for produce, output file for consume (consume defaults to stdout)");
        System.out.println("  -o <offset>         Starting offset for consume (default: 0)");
        System.out.println("  -h                  Print this help and exit");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  kafka-cli localhost:9092 -O list");
        System.out.println("  kafka-cli localhost:9092 -O create -t my-topic");
        System.out.println("  kafka-cli localhost:9092 -O produce -t my-topic -f messages.txt");
        System.out.println("  kafka-cli localhost:9092 -O consume -t my-topic -o 0 -f out.txt");
        System.out.println("  kafka-cli localhost:9092 -O delete -t my-topic");
    }

    public String bootstrapServer() { return bootstrapServer; }
    public Op operation() { return operation; }
    public String topic() { return topic; }
    public String file() { return file; }
    public long offset() { return offset; }
}
