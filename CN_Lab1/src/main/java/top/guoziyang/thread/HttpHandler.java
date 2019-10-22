package top.guoziyang.thread;

import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.ArrayUtils;
import top.guoziyang.util.CachePool;
import top.guoziyang.util.Configuration;
import top.guoziyang.util.LineReader;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HttpHandler implements Runnable {

    private Socket socketToClient;
    private Socket socketToServer;
    private int BUFSIZE = 1024;

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
            String url = null;
            while((line = LineReader.readLine(toClientReader)) != null) {
                if(line.startsWith("GET")) {
                    url = line.split(" ")[1];
                }
                if(line.startsWith("CONNECT")) {
                    return;
                }
                if(line.startsWith("Host")) {
                    hostString = line.split(" ")[1];
                }
                requestBuilder.append(line).append("\r\n");
            }
            requestBuilder.append("\r\n");
            // 从host中解析出主机与端口
            String[] hostSplits = new String[2];
            try {
                assert hostString != null;
                hostSplits = hostString.split(":");
            } catch(Exception ignore) {
                ;
            }
            String host = hostSplits[0];
            int port = 80;

            Configuration configuration = Configuration.getInstance();

            if(configuration.getBlackHostSet().contains(host)) {
                error304(toClientWriter);
                return;
            }

            if(configuration.getGuideMap().containsKey(host)) {
                String guideHost = configuration.getGuideMap().get(host);
                requestBuilder = new StringBuilder(requestBuilder.toString().replace(host, guideHost));
                host = guideHost;
            }

            CachePool cachePool = CachePool.getInstance();
            String lastTime = cachePool.getTime(url);

            // System.out.println("Socket to " + host + " port " + port);
            socketToServer = new Socket(host, port);
            // System.out.println("Socket established!");
            OutputStream toServerWriter = socketToServer.getOutputStream();
            InputStream toServerReader = socketToServer.getInputStream();

            //缓存存在
            if(lastTime != null) {
                System.out.println("缓存存在！");
                String checkString = "GET " + url + " HTTP/1.1\r\n";
                checkString += "Host: " + host + "\r\n";
                checkString += "If-modified-since: " + lastTime + "\r\n\r\n";
                toServerWriter.write(checkString.getBytes());
                toServerWriter.flush();
                String checkRes = LineReader.readLine(toServerReader);
                System.out.println(checkRes);
                assert checkRes != null;
                if(checkRes.contains("Not Modified")) {
                    System.out.println("命中！");
                    toClientWriter.write(cachePool.getContent(url));
                    toClientWriter.flush();
                } else {
                    checkRes += "\r\n";
                    toClientWriter.write(checkRes.getBytes());
                    byte[] buffer = new byte[BUFSIZE];
                    int length;
                    while (true){
                        if((length = toServerReader.read(buffer)) > 0) {
                            toClientWriter.write(buffer, 0, length);
                        } else if(length < 0) {
                            break;
                        }
                    }
                    toClientWriter.flush();
                }
            } else {
                System.out.println("缓存不存在！");
                // 根据主机与端口构建Socket
                toServerWriter.write(requestBuilder.toString().getBytes());
                toServerWriter.flush();
                // System.out.println("Headers sent to Server!");

                // new Thread(new ProxyHandler(toClientReader, toServerWriter)).start();

                byte[] buffer = new byte[BUFSIZE];
                ArrayList<Byte> bytes = new ArrayList<>();
                int length;
                while (true) {
                    if((length = toServerReader.read(buffer)) > 0) {
                        toClientWriter.write(buffer, 0, length);
                        bytes.addAll(Bytes.asList(ArrayUtils.subarray(buffer, 0, length)));
                    } else if(length < 0) {
                        break;
                    }
                }
                toClientWriter.flush();

                byte[] bytesArray = Bytes.toArray(bytes);
                String byteString = new String(bytesArray);
                int lmIndex = byteString.indexOf("Last-Modified");
                System.out.println(lmIndex);
                if(lmIndex != -1) {
                    String lmString = byteString.substring(lmIndex, lmIndex + 44);
                    String lmTime = lmString.substring(15);
                    cachePool.addCache(url, bytesArray, lmTime);
                }
            }

        } catch (IOException ignored) {
            ;
        }
    }

    private void error304(OutputStream toClientWriter) {
        String errorStr = "HTTP/1.1 403 Forbidden\r\n\r\n";
        try {
            toClientWriter.write(errorStr.getBytes());
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
