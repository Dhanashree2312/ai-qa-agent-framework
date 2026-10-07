package framework;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties =
            new Properties();

    static {

        try (InputStream input =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     "config/config.properties"
                             )) {

            if (input == null) {

                throw new RuntimeException(
                        "config.properties not found"
                );
            }

            properties.load(input);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load config.properties",
                    e
            );
        }
    }

    public static String get(String key) {

        String value =
                properties.getProperty(key);

        if (value == null ||
                value.isBlank()) {

            throw new RuntimeException(
                    "Missing configuration: "
                            + key
            );
        }

        return value;
    }
}