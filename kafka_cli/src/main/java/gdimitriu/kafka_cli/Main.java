package gdimitriu.kafka_cli;

import gdimitriu.kafka_cli.operations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gdimitriu.kafka_cli.cli.CliArgs;
import gdimitriu.kafka_cli.operations.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        // Must happen before any Log4j class is touched.
        initLogging();

        Logger log = LogManager.getLogger(Main.class);

        if (args.length == 0 || (args.length == 1 && args[0].equals("-h"))) {
            CliArgs.printHelp();
            System.exit(0);
        }

        CliArgs cliArgs;
        try {
            cliArgs = CliArgs.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Run with -h for usage.");
            System.exit(1);
            return;
        }

        Operation op = switch (cliArgs.operation()) {
            case list    -> new ListOperation();
            case create  -> new CreateOperation();
            case delete  -> new DeleteOperation();
            case produce -> new ProduceOperation();
            case consume -> new ConsumeOperation();
        };

        try {
            op.execute(cliArgs);
        } catch (Exception e) {
            log.error("Operation '{}' failed: {}", cliArgs.operation(), e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void initLogging() {
        Path configFile = resolveExecDir().resolve("log4j2.xml");
        if (Files.exists(configFile)) {
            System.setProperty("log4j2.configurationFile", configFile.toAbsolutePath().toString());
        }
        // Required for GraalVM native-image: JMX MBean registration fails in native mode
        System.setProperty("log4j2.disableJmx", "true");
    }

    private static Path resolveExecDir() {
        // Strategy 1: path of the running process (works in GraalVM native-image via ProcessHandle)
        try {
            String cmd = ProcessHandle.current().info().command().orElse(null);
            if (cmd != null) {
                Path p = Paths.get(cmd).toAbsolutePath().getParent();
                if (p != null) return p;
            }
        } catch (Exception ignored) {}

        // Strategy 2: location of the JAR on the classpath (JVM fat-jar mode)
        try {
            URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                Path p = Paths.get(location.toURI()).getParent();
                if (p != null) return p;
            }
        } catch (Exception ignored) {}

        // Strategy 3: working directory fallback
        return Paths.get(System.getProperty("user.dir"));
    }
}
