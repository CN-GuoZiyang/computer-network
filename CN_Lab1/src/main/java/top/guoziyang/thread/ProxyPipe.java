package top.guoziyang.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 直接将输入流的内容转发到输出流
 *
 * @author Ziyang Guo
 */
public class ProxyPipe implements Runnable {

    private InputStream inputStream;
    private OutputStream outputStream;

    public ProxyPipe(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while((length = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException ignore) {
            ;
        }
    }

}
