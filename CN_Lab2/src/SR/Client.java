import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        File file1 = new File("1.png");
        File file2 = new File("4.png");
        if(!file2.exists()) {
            if(!file2.createNewFile()) {
                System.out.println("创建文件失败！");
                return;
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        cloneStream(byteArrayOutputStream, new FileInputStream(file1));
        SR client = new SR("127.0.0.1", 7070, 8080);
        System.out.println("开始向 127.0.0.1:7070 发送1.png");
        client.send(byteArrayOutputStream.toByteArray());

        System.out.println("开始从 127.0.0.1:7070 处接收3.png");
        Thread.sleep(50);
        if((byteArrayOutputStream = client.receive()).size() != 0) {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
            System.out.println("获取文件3.png完成\n存为4.png");
        }
    }

    public static void cloneStream(ByteArrayOutputStream res, InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while((length = in.read(buffer)) >= 0) {
            res.write(buffer, 0, length);
        }
        res.flush();
    }
}