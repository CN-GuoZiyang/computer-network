import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        File file1 = new File("2.png");
        File file2 = new File("3.png");
        if(!file1.exists()) {
            if(!file1.createNewFile()) {
                System.out.println("创建文件失败！");
                return;
            }
        }
        GBN server = new GBN("127.0.0.1", 8080, 7070);
        System.out.println("开始从 127.0.0.1:8080 处接收1.png");
        ByteArrayOutputStream byteArrayOutputStream;
        if((byteArrayOutputStream = server.receive()).size() != 0) {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
            fileOutputStream.close();
            System.out.println("获取文件1.png完成\n存为2.png");
        }
        byteArrayOutputStream = new ByteArrayOutputStream();
        Client.cloneStream(byteArrayOutputStream, new FileInputStream(file2));
        System.out.println("开始向 127.0.0.1:7070 发送3.png");
        server.send(byteArrayOutputStream.toByteArray());
    }
}