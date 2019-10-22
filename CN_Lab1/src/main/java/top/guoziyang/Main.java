package top.guoziyang;

import top.guoziyang.thread.HttpHandler;
import top.guoziyang.util.Configuration;
import top.guoziyang.util.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        Utils.readConfig();
        Configuration configuration = Configuration.getInstance();
        try {
            ServerSocket serverSocket = new ServerSocket(configuration.getServerPort());
            while(true) {
                Socket socket = serverSocket.accept();
                new Thread(new HttpHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
