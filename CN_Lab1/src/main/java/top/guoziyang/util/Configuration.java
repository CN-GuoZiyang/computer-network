package top.guoziyang.util;

public class Configuration {

    private static Configuration configuration;

    private Configuration(){super();}

    private int serverPort;

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
