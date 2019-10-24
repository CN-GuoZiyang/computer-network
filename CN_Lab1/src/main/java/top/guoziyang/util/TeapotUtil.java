package top.guoziyang.util;

import java.io.*;

/**
 * 处理Teapot错误页面请求
 *
 * @author Ziyang Guo
 */
public class TeapotUtil {

    /**
     * 处理418错误页面
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void error418(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("teapot.txt");
            byte[] bytes = new byte[1024];
            int length;
            toClientWriter.write("HTTP/1.1 418 I'm a teapot\r\nContent-Type: text/html;charset=utf-8\r\n\r\n".getBytes());
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
     * 处理418错误页面的CSS
     *
     * @param toClientWriter 对客户端的输出流
     */
    public static void teapotCss(OutputStream toClientWriter) {
        try {
            InputStream fis = new FileInputStream("teapot.min.css");
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
            InputStream fis = new FileInputStream("teapot.min.js");
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
            InputStream fis = new FileInputStream("teapot.png");
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
            InputStream fis = new FileInputStream("logo.png");
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
            InputStream fis = new FileInputStream("favicon.ico");
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
