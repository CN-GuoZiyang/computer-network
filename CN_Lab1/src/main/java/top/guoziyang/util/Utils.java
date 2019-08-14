package top.guoziyang.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    private static Configuration configuration = Configuration.getInstance();

    public static void readConfig() {
        readProperties();
    }

    private static void readProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            configuration.setServerPort(Integer.valueOf(properties.getProperty("server_port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
