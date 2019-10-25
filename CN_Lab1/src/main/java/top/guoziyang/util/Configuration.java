package top.guoziyang.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件类
 *
 * @author Ziyang Guo
 */
public class Configuration {

    private static Configuration configuration;

    private Configuration(){super();}

    private int serverPort;
    private Set<String> blackHostSet = new HashSet<>();
    private Set<String> blockedUsers = new HashSet<>();
    private Map<String, String> guideMap = new HashMap<>();

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public Set<String> getBlackHostSet() {
        return blackHostSet;
    }

    public void setBlackHostSet(Set<String> blackHostSet) {
        this.blackHostSet = blackHostSet;
    }

    public Set<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Map<String, String> getGuideMap() {
        return guideMap;
    }

    public void setGuideMap(Map<String, String> guideMap) {
        this.guideMap = guideMap;
    }

    public static Configuration getInstance() {
        if(configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

}
