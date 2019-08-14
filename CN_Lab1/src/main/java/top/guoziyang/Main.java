package top.guoziyang;

import top.guoziyang.thread.HttpHandler;
import top.guoziyang.thread.ThreadPool;
import top.guoziyang.util.Configuration;
import top.guoziyang.util.Utils;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static Configuration configuration;

    public static void main(String[] args) {
        Utils.readConfig();
        configuration = Configuration.getInstance();
        try {
            ServerSocket serverSocket = new ServerSocket(configuration.getServerPort());
            while(true) {
                Socket socket = serverSocket.accept();
                ThreadPool.execute(new HttpHandler(socket));
            }
        } catch (BindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
