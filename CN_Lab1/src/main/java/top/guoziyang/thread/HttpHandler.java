package top.guoziyang.thread;

import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.ArrayUtils;
import top.guoziyang.util.CachePool;
import top.guoziyang.util.Configuration;
import top.guoziyang.util.LineReader;
import top.guoziyang.util.TeapotUtil;

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
            while ((line = LineReader.readLine(toClientReader)) != null) {
                if (line.startsWith("GET")) {
                    url = line.split(" ")[1];
                }
                if (line.startsWith("CONNECT")) {
                    return;
                }
                if (line.startsWith("Host")) {
                    hostString = line.split(" ")[1];
                }
                requestBuilder.append(line).append("\r\n");
            }
            requestBuilder.append("\r\n");
            if(url == null) {
                toClientReader.close();
                toClientWriter.close();
                return;
            }
            // 从host中解析出主机与端口
            String[] hostSplits = new String[2];
            try {
                assert hostString != null;
                hostSplits = hostString.split(":");
            } catch (Exception ignore) {
                ;
            }
            String host = hostSplits[0];
            int port = 80;

            Configuration configuration = Configuration.getInstance();

            switch (url) {
                case "http://teapot.min.css/":
                    TeapotUtil.teapotCss(toClientWriter);
                    return;
                case "http://teapot.min.js/":
                    TeapotUtil.teapotJs(toClientWriter);
                    return;
                case "http://teapot.png/":
                    TeapotUtil.teapotPng(toClientWriter);
                    return;
                case "http://logo.png/":
                    TeapotUtil.teapotLogo(toClientWriter);
                    return;
                case "http://analytics.js/":
                    TeapotUtil.analyticsJs(toClientWriter);
                    return;
            }

            if (configuration.getBlackHostSet().contains(host)) {
                if (url.contains("favicon.ico")) {
                    TeapotUtil.faviconIco(toClientWriter);
                    return;
                }
                TeapotUtil.error418(toClientWriter);
                return;
            }

            if (configuration.getGuideMap().containsKey(host)) {
                String guideHost = configuration.getGuideMap().get(host);
                requestBuilder = new StringBuilder(requestBuilder.toString().replace(host, guideHost));
                host = guideHost;
            }

            // System.out.println("Socket to " + host + " port " + port);
            socketToServer = new Socket(host, port);
            // System.out.println("Socket established!");
            OutputStream toServerWriter = socketToServer.getOutputStream();
            InputStream toServerReader = socketToServer.getInputStream();

            CachePool cachePool = CachePool.getInstance();
            byte[] content;
            //缓存存在
            if ((content = cachePool.getContent(url)) != null) {
                System.out.println("缓存存在：" + url);
                String contentStr = new String(ArrayUtils.subarray(content, 0, 1024));
                String lastTime = contentStr.substring(contentStr.indexOf("Last-Modified") + 15, contentStr.indexOf("Last-Modified") + 44);
                String checkString = "GET " + url + " HTTP/1.1\r\n";
                checkString += "Host: " + host + "\r\n";
                checkString += "If-modified-since: " + lastTime + "\r\n\r\n";
                toServerWriter.write(checkString.getBytes());
                toServerWriter.flush();
                String checkRes = LineReader.readLine(toServerReader);
                assert checkRes != null;
                if (checkRes.contains("Not Modified")) {
                    System.out.println("缓存命中：" + url);
                    toClientWriter.write(content);
                    toClientWriter.flush();
                    toClientWriter.close();
                } else {
                    checkRes += "\r\n";
                    toClientWriter.write(checkRes.getBytes());
                    byte[] buffer = new byte[BUFSIZE];
                    int length;
                    while (true) {
                        if ((length = toServerReader.read(buffer)) > 0) {
                            toClientWriter.write(buffer, 0, length);
                        } else if (length < 0) {
                            break;
                        }
                    }
                    toClientWriter.flush();
                    toClientWriter.close();
                }
            } else {
                // System.out.println("缓存不存在！");
                // 根据主机与端口构建Socket
                toServerWriter.write(requestBuilder.toString().getBytes());
                toServerWriter.flush();
                // System.out.println("Headers sent to Server!");

                // new Thread(new ProxyHandler(toClientReader, toServerWriter)).start();

                byte[] buffer = new byte[BUFSIZE];
                ArrayList<Byte> bytes = new ArrayList<>();
                ArrayList<byte[]> byteList = new ArrayList<>();
                ArrayList<Integer> lengthList = new ArrayList<>();
                int length;
                while (true) {
                    if ((length = toServerReader.read(buffer)) > 0) {
                        toClientWriter.write(buffer, 0, length);
                        byteList.add(buffer.clone());
                        lengthList.add(length);
                    } else if (length < 0) {
                        break;
                    }
                }

                toClientWriter.flush();
                toClientWriter.close();

                for (int i = 0; i < byteList.size(); i++) {
                    bytes.addAll(Bytes.asList(ArrayUtils.subarray(byteList.get(i), 0, lengthList.get(i))));
                }
                byte[] bytesArray = Bytes.toArray(bytes);
                if (new String(bytesArray).contains("Last-Modified")) {
                    cachePool.addCache(url, bytesArray);
                    System.out.println("缓存保存：" + url);
                }
            }

        } catch (IOException ignored) {
            ;
        }
    }

}
