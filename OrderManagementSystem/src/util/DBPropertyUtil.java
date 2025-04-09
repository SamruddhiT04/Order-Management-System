package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {
    public static Properties loadProperties(String propertyFileName) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertyFileName)) {
            props.load(fis);
        }
        return props;
    }
}
