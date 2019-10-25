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

/**
 * 处理单个HTTP的线程
 *
 * @author Ziyang Guo
 */
public class HttpHandler implements Runnable {

    private Socket socketToClient;
    private Socket socketToServer;
    private static final int BUFFER_SIZE = 1024;

    public HttpHandler(Socket socket) {
        this.socketToClient = socket;
    }

    @Override
    public void run() {
        try {
            InputStream toClientReader = socketToClient.getInputStream();
            OutputStream toClientWriter = socketToClient.getOutputStream();
            StringBuilder requestBuilder = new StringBuilder();

            Configuration configuration = Configuration.getInstance();

            // 获取并解析客户端的请求头
            String line;
            String hostString = null;
            String url = null;
            while ((line = LineReader.readLine(toClientReader)) != null) {
                if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("CONNECT")) {
                    url = line.split(" ")[1];
                }
                if (line.startsWith("Host")) {
                    hostString = line.split(" ")[1];
                }
                requestBuilder.append(line).append("\r\n");
            }
            requestBuilder.append("\r\n");

            if(url == null || hostString == null) {
                toClientReader.close();
                toClientWriter.close();
                return;
            }
            // 从host中解析出主机
            String[] hostSplits = hostString.split(":");
            String host = hostSplits[0];

            // 获取服务器端口
            int port = hostSplits.length == 1?80:443;

            // 检查是否是418页面的资源请求
            if(TeapotUtil.teapotResource(url, toClientWriter)) {
                return;
            }

            // 拦截不允许访问的用户
            if(configuration.getBlockedUsers().contains(socketToClient.getInetAddress().getHostAddress())) {
                if (url.contains("favicon.ico")) {
                    TeapotUtil.faviconIco(toClientWriter);
                    return;
                }
                System.out.println("不允许的用户：" + socketToClient.getInetAddress().getHostAddress());
                TeapotUtil.error418(toClientWriter, "未允许的用户访问");
                return;
            }

            // 拦截不允许访问的host
            if (configuration.getBlackHostSet().contains(host)) {
                if (url.contains("favicon.ico")) {
                    TeapotUtil.faviconIco(toClientWriter);
                    return;
                }
                System.out.println("不允许的网站：" + host);
                TeapotUtil.error418(toClientWriter, "未允许的网站访问");
                return;
            }

            // 如果是被钓鱼host，修改头部信息中的地址
            if (configuration.getGuideMap().containsKey(host)) {
                String guideHost = configuration.getGuideMap().get(host);
                System.out.println("钓鱼：" + host + "\t引导至：" + guideHost);
                requestBuilder = new StringBuilder(requestBuilder.toString().replace(host, guideHost));
                url = url.replace(host, guideHost);
                host = guideHost;
            }

            // 开启与远程服务器的会话，并打开输入输出流
            socketToServer = new Socket(host, port);
            System.out.println("请求：" + url);
            OutputStream toServerWriter = socketToServer.getOutputStream();
            InputStream toServerReader = socketToServer.getInputStream();

            // 由于HTTPS通信加密，无法获取请求的其他信息，于是直接转发流量
            if(requestBuilder.toString().startsWith("CONNECT")) {
                toClientWriter.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                toClientWriter.flush();
                new Thread(new ProxyPipe(toClientReader, toServerWriter)).start();
                byte[] buffer = new byte[1024];
                int length;
                while((length = toServerReader.read(buffer)) >= 0) {
                    toClientWriter.write(buffer, 0, length);
                }
                return;
            }

            CachePool cachePool = CachePool.getInstance();
            byte[] content;
            //缓存存在
            if ((content = cachePool.getContent(url)) != null) {
                System.out.println("缓存存在：" + url);
                // 构造请求头向服务器确认缓存是否过期
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
                    // 返回304，直接返回缓存中数据
                    System.out.println("缓存命中：" + url);
                    toClientWriter.write(content);
                    toClientWriter.flush();
                    toClientWriter.close();
                } else {
                    // 否则，继续读取服务器报文并转发
                    System.out.println("缓存过期：" + url);
                    checkRes += "\r\n";
                    toClientWriter.write(checkRes.getBytes());
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = toServerReader.read(buffer)) >= 0) {
                        toClientWriter.write(buffer, 0, length);
                    }
                    toClientWriter.flush();
                    toClientWriter.close();
                }
            } else {
                // 缓存不存在
                toServerWriter.write(requestBuilder.toString().getBytes());
                toServerWriter.flush();

                byte[] buffer = new byte[BUFFER_SIZE];
                ArrayList<Byte> bytes = new ArrayList<>();
                ArrayList<byte[]> byteList = new ArrayList<>();
                ArrayList<Integer> lengthList = new ArrayList<>();
                int length;
                // 从服务器接收数据，并转发给客户端
                while ((length = toServerReader.read(buffer)) >= 0) {
                    toClientWriter.write(buffer, 0, length);
                    byteList.add(buffer.clone());
                    lengthList.add(length);
                }

                // 清空缓冲区（发送）并断开输出流
                toClientWriter.flush();
                toClientWriter.close();

                //将所有的buffer整合起来转换成一个唯一的byte数组
                for (int i = 0; i < byteList.size(); i++) {
                    bytes.addAll(Bytes.asList(ArrayUtils.subarray(byteList.get(i), 0, lengthList.get(i))));
                }
                byte[] bytesArray = Bytes.toArray(bytes);
                // 如果存在Last-Modified字段，则存到cache里
                if (new String(bytesArray).contains("Last-Modified")) {
                    cachePool.addCache(url, bytesArray);
                    System.out.println("缓存保存：" + url);
                }
            }

        } catch (IOException ignored) {} finally {
            // 关闭socket
            try {
                if(socketToClient != null && !socketToClient.isClosed()) {
                    socketToClient.close();
                }
                if(socketToServer != null && !socketToServer.isClosed()) {
                    socketToServer.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

}
