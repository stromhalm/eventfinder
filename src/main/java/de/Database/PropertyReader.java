package de.Database;

/**
 * Created by Dario on 28.05.15.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private InputStream inputStream;
    private Properties properties;

    private final String config = "config.properties";

    private String getConfigFilePath()
    {
        return getClass().getClassLoader().getResource(config).getPath();
    }

    public String readFromProperties(String input){
        properties = new Properties();
        try {
            inputStream = new FileInputStream(getConfigFilePath());
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(input);

    }
}
