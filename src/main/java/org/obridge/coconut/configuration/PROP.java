package org.obridge.coconut.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PROP {

    private static String propertyFileName = "application.properties";

    public static void setPropertyFileName(String propertyFileName) {
        PROP.propertyFileName = propertyFileName;
    }

    public static String get(String propertyName) {
        try (final InputStream prop = new File(propertyFileName).exists() ? Files.newInputStream(Paths.get(propertyFileName)) : PROP.class.getResourceAsStream("/" + propertyFileName);) {
            Properties p = new Properties();
            p.load(prop);
            return p.getProperty(propertyName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
