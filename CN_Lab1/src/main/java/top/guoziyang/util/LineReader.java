package top.guoziyang.util;

import java.io.IOException;
import java.io.InputStream;

public class LineReader {

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
