package top.guoziyang.thread;

import top.guoziyang.util.LineReader;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpHandler implements Runnable {

    private Socket socketToClient;
    private Socket socketToServer;

    public HttpHandler(Socket socket) {
        this.socketToClient = socket;
    }

    @Override
    public void run() {
        try {
            InputStream toClientReader = socketToClient.getInputStream();
            OutputStream toClientWriter = socketToClient.getOutputStream();
            StringBuilder requestBuilder = new StringBuilder();

            // 获取客户端的请求头
            String line;
            String hostString = null;
            while((line = LineReader.readLine(toClientReader)) != null) {
                if(line.startsWith("Host")) {
                    hostString = line.split(" ")[1];
                }
                requestBuilder.append(line).append("\r\n");
            }
            requestBuilder.append("\r\n");
            System.out.println(requestBuilder.toString());

            // 判断是否https
            boolean https = requestBuilder.toString().startsWith("CONNECT");
            // 从host中解析出主机与端口
            String[] hostSplits = hostString.split(":");
            String host = hostSplits[0];
            int port = hostSplits.length>1?Integer.valueOf(hostSplits[1].trim()):80;

            // 根据主机与端口构建Socket
            System.out.println("Socket to " + host + " port " + port);
            socketToServer = new Socket(host, port);
            System.out.println("Socket established!");
            OutputStream toServerWriter = socketToServer.getOutputStream();
            InputStream toServerReader = socketToServer.getInputStream();
            if(https) {
                toClientWriter.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                toClientWriter.flush();
            } else {
                toServerWriter.write(requestBuilder.toString().getBytes());
                System.out.println("Headers sent to Server!");
            }

            //
            ThreadPool.execute(new ProxyHandler(toClientReader, toServerWriter));
            int ch;
            while(true) {
                toClientWriter.write(toServerReader.read());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
