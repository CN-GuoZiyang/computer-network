package top.guoziyang;

import top.guoziyang.thread.HttpHandler;
import top.guoziyang.util.Configuration;
import top.guoziyang.util.ConfigUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        // 读取配置文件
        ConfigUtils.readConfig();
        Configuration configuration = Configuration.getInstance();
        try {
            // 开启服务端Socket监听
            ServerSocket serverSocket = new ServerSocket(configuration.getServerPort());
            System.out.println("监听端口：" + configuration.getServerPort());
            while(true) {
                // 客户端连接后创建线程处理
                Socket socket = serverSocket.accept();
                new Thread(new HttpHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
