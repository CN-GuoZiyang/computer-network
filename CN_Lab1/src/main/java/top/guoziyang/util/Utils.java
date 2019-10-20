package top.guoziyang.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utils {

    private static Configuration configuration = Configuration.getInstance();

    public static void readConfig() {
        readProperties();
        readWebFilter();
        readGuide();
    }

    private static void readProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            configuration.setServerPort(Integer.parseInt(properties.getProperty("server_port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readWebFilter() {
        Set<String> hostBlackHostSet = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("host_filter.txt"));) {
            String line;
            while((line = reader.readLine()) != null) {
                hostBlackHostSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setBlackHostSet(hostBlackHostSet);
    }

    private static void readGuide() {
        Map<String, String> guideMap = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("guide.txt"));) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                guideMap.put(splits[0], splits[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setGuideMap(guideMap);
    }

}
