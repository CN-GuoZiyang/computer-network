package top.guoziyang.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 读取配置文件的工具类
 *
 * @author Ziyang Guo
 */
public class ConfigUtils {

    private static Configuration configuration = Configuration.getInstance();

    /**
     * 读取配置
     */
    public static void readConfig() {
        readProperties();
        readWebFilter();
        readGuide();
        readBlockedUsers();
    }

    /**
     * 读取properties配置文件
     */
    private static void readProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            configuration.setServerPort(Integer.parseInt(properties.getProperty("server_port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取不允许访问的网站列表
     */
    private static void readWebFilter() {
        Set<String> hostBlackHostSet = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("host_filter.txt"));) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("//") || line.length() == 0) continue;
                hostBlackHostSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setBlackHostSet(hostBlackHostSet);
    }

    /**
     * 读取钓鱼网站列表
     */
    private static void readGuide() {
        Map<String, String> guideMap = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("guide.txt"));) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("//") || line.length() == 0) continue;
                String[] splits = line.split(" ");
                guideMap.put(splits[0], splits[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setGuideMap(guideMap);
    }

    /**
     * 读取不允许访问的用户列表
     */
    private static void readBlockedUsers() {
        Set<String> blockedUsers = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("blocked_users.txt"));) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("//") || line.length() == 0) continue;
                blockedUsers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setBlockedUsers(blockedUsers);
    }

}
