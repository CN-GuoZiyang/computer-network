package top.guoziyang.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private static Configuration configuration;

    private Configuration(){super();}

    private int serverPort;
    private Set<String> blackHostSet = new HashSet<>();

    public Map<String, String> getGuideMap() {
        return guideMap;
    }

    public void setGuideMap(Map<String, String> guideMap) {
        this.guideMap = guideMap;
    }

    private Map<String, String> guideMap = new HashMap<>();

    public Set<String> getBlackHostSet() {
        return blackHostSet;
    }

    public void setBlackHostSet(Set<String> blackHostSet) {
        this.blackHostSet = blackHostSet;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public static Configuration getInstance() {
        if(configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

}
