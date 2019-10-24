package top.guoziyang.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流按行读取
 *
 * @author Ziyang Guo
 */
public class LineReader {

    /**
     * 按行读取输入流
     *
     * @param inputStream 输入流
     * @return 读取的一行文本
     * @throws IOException 可能出现的异常
     */
    public static String readLine(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        int ch;
        while((ch = inputStream.read()) != -1) {
            if(ch == '\r') {
                inputStream.read();
                break;
            }
            builder.append((char)ch);
        }
        if(builder.length() <= 0) {
            return null;
        } else {
            return builder.toString();
        }
    }

}
