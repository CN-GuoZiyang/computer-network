package top.guoziyang.util;

import java.io.*;

/**
 * 处理Teapot错误页面请求
 *
 * @author Ziyang Guo
 */
public class TeapotUtil {

    /**
     * 处理418页面的资源请求
     *
     * @param url 待检查的url
     * @param toClientWriter 对客户端socket的输出流
     * @return 是否是418资源请求
     */
    public static boolean teapotResource(String url, OutputStream toClientWriter) {
        switch (url) {
            case "http://teapot.min.css/":
                TeapotUtil.teapotCss(toClientWriter);
                return true;
            case "http://teapot.min.js/":
                TeapotUtil.teapotJs(toClientWriter);
                return true;
            case "http://teapot.png/":
                TeapotUtil.teapotPng(toClientWriter);
                return true;
            case "http://logo.png/":
                TeapotUtil.teapotLogo(toClientWriter);
                return true;
            case "http://analytics.js/":
                TeapotUtil.analyticsJs(toClientWriter);
                return true;
            default:
                return false;
        }
    }

    /**
     * 处理418错误页面
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void error418(OutputStream toClientWriter, String errorMsg) {
        try {
            BufferedReader fbr = new BufferedReader(new FileReader("418/teapot.txt"));
            toClientWriter.write("HTTP/1.1 418 I'm a teapot\r\nContent-Type: text/html;charset=utf-8\r\n\r\n".getBytes());
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = fbr.readLine()) != null) {
                sb.append(line);
            }
            fbr.close();
            toClientWriter.write(sb.toString().replace("<此处为错误信息>", errorMsg).getBytes());
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面的CSS
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void teapotCss(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("418/teapot.min.css");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 200 OK\r\nContent-Type: text/css\r\n\r\n".getBytes());
            while(true) {
                if((length = fis.read(bytes)) > 0) {
                    toClientWriter.write(bytes, 0, length);
                } else if(length < 0) {
                    break;
                }
            }
            fis.close();
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面的JS
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void teapotJs(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("418/teapot.min.js");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 200 OK\r\nContent-Type: text/javascript\r\n\r\n".getBytes());
            while(true) {
                if((length = fis.read(bytes)) > 0) {
                    toClientWriter.write(bytes, 0, length);
                } else if(length < 0) {
                    break;
                }
            }
            fis.close();
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面的PNG
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void teapotPng(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("418/teapot.png");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n".getBytes());
            while(true) {
                if((length = fis.read(bytes)) > 0) {
                    toClientWriter.write(bytes, 0, length);
                } else if(length < 0) {
                    break;
                }
            }
            fis.close();
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面的LOGO
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void teapotLogo(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("418/logo.png");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n".getBytes());
            while(true) {
                if((length = fis.read(bytes)) > 0) {
                    toClientWriter.write(bytes, 0, length);
                } else if(length < 0) {
                    break;
                }
            }
            fis.close();
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面关于谷歌的JS（不处理）
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void analyticsJs(OutputStream toClientWriter) {
        try {
            toClientWriter.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理418错误页面的favicon
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void faviconIco(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("418/favicon.ico");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 200 OK\r\nContent-Type: image/x-icon\r\n\r\n".getBytes());
            while(true) {
                if((length = fis.read(bytes)) > 0) {
                    toClientWriter.write(bytes, 0, length);
                } else if(length < 0) {
                    break;
                }
            }
            fis.close();
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
