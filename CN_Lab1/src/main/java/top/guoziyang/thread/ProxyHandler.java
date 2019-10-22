package top.guoziyang.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProxyHandler implements Runnable {

    private InputStream inputStream;
    private OutputStream outputStream;

    public ProxyHandler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            int ch;
            while((ch = inputStream.read()) != -1) {
                outputStream.write(ch);
            }
            outputStream.flush();
            //System.out.println("flush success!");
        } catch (IOException ignore) {
            ;
        }
    }

}
