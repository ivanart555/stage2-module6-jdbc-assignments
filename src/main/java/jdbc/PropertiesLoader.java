package jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesLoader {
    private static final Logger LOGGER = Logger.getLogger(PropertiesLoader.class.getName());

    public static Properties loadPropertiesFromFile(String filename) {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(rootPath + filename)) {
            properties.load(input);
        } catch (IOException e) {
            LOGGER.warning("Failed to load application properties from file: " + filename);
        }
        return properties;
    }
}
