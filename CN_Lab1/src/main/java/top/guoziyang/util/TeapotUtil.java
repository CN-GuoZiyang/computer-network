package top.guoziyang.util;

import java.io.*;

public class TeapotUtil {

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

    public static void analyticsJs(OutputStream toClientWriter) {
        try {
            toClientWriter.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            toClientWriter.flush();
            toClientWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
