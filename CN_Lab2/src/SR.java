import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * 可收发的SR协议实现
 * 
 * @author Ziyang Guo
 */
public class SR {
    
    private InetAddress host;   // 目的主机地址
    private int targetPort; // 目的端口
    private int myPort;     // 本地端口
    private int windowSize = 16;    // 窗口大小
    private int sendMaxTime = 2;    // 最大发送次数
    private int receiveMaxTime = 4; // 最大接收次数
    private long base = 0;          // 窗口base序号
    private int loss = 10;          // 模拟丢包

    public SR(String host, int targetPort, int myPort) throws UnknownHostException {
        this.myPort = myPort;
        this.targetPort = targetPort;
        this.host = InetAddress.getByName(host);
    }

    /**
     * 向目的主机端口发送内容
     * 
     * @param content 待发送的内容
     * @throws IOException IO异常
     */
    public void send(byte[] content) throws IOException {
        int sendIndex = 0;  // 发送到的字节序号
        int length;
        int maxLength = 1024;   // 最大数据长度
        DatagramSocket datagramSocket = new DatagramSocket(myPort);
        List<ByteArrayOutputStream> datagramBuffer = new LinkedList<>();    // 对当前窗口的内容进行缓存，方便重发
        List<Integer> timers = new LinkedList<>();  // 当前窗口的数据帧已发送次数
        long sendSeq = base;    // 发送的数据帧的序列号
        do {
            // 循环将窗口发满
            while(timers.size() < windowSize && sendIndex < content.length && sendSeq < 256) {
                timers.add(0);
                datagramBuffer.add(new ByteArrayOutputStream());
                length = Math.min(content.length - sendIndex, maxLength);

                // 拼接数据帧，按照 base + seq + data 的顺序拼接
                ByteArrayOutputStream one = new ByteArrayOutputStream();
                byte[] temp = new byte[1];
                temp[0] = new Long(base).byteValue();
                one.write(temp, 0, 1);
                temp = new byte[1];
                temp[0] = new Long(sendSeq).byteValue();
                one.write(temp, 0, 1);
                one.write(content, sendIndex, length);

                // 向目的主机发送
                DatagramPacket packet = new DatagramPacket(one.toByteArray(), one.size(), host, targetPort);
                datagramSocket.send(packet);

                // 将发送的内容暂存在缓存中
                datagramBuffer.get((int)(sendSeq - base)).write(content, sendIndex, length);
                sendIndex += length;
                System.out.println("发送数据包：base " + base + " seq " + sendSeq);
                sendSeq ++;
            }
            
            // 设置超时时间1000ms
            datagramSocket.setSoTimeout(1000);
            DatagramPacket receivePacket;

            // 循环从目的主机接收ack
            try {
                while(!checkWindow(timers)) {
                    byte[] recv = new byte[1500];
                    receivePacket = new DatagramPacket(recv, recv.length);
                    datagramSocket.receive(receivePacket);
                    // 取出ack的序列号
                    int ack = (int)((recv[0] & 0x0FF) - base);
                    timers.set(ack, -1);
                }
            } catch (SocketTimeoutException e) {
                // socket超时，设置确认次数+1
                for(int i = 0; i < timers.size(); i ++) {
                    int tempTime = timers.get(i);
                    if(tempTime != -1) {
                        timers.set(i, tempTime + 1);
                    }
                }
            }
            // 重发所有超过最大确认次数的数据帧
            for(int i = 0; i < timers.size(); i ++) {
                if(timers.get(i) > sendMaxTime) {
                    ByteArrayOutputStream resender = new ByteArrayOutputStream();
                    byte[] temp = new byte[1];
                    temp[0] = new Long(base).byteValue();
                    resender.write(temp, 0, 1);
                    temp = new byte[1];
                    temp[0] = new Long(i + base).byteValue();
                    resender.write(temp, 0, 1);
                    resender.write(datagramBuffer.get(i).toByteArray(), 0, datagramBuffer.get(i).size());
                    DatagramPacket datagramPacket = new DatagramPacket(resender.toByteArray(), resender.size(), host, targetPort);
                    datagramSocket.send(datagramPacket);
                    System.err.println("重新发送数据包：base " + base + " seq " + (i + base));
                    timers.set(i, 0);
                }
            }
            int i = 0;
            int s = timers.size();
            // 确认并删除所有已经确认过的缓存
            while(i < s) {
                if(timers.get(i) == -1) {
                    timers.remove(i);
                    datagramBuffer.remove(i);
                    base ++;
                    s --;
                } else {
                    break;
                }
            }

            // 更新发送序号
            if(base >= 256) {
                base -= 256;
                sendSeq -= 256;
            }

        } while (sendIndex < content.length || timers.size() != 0);
        datagramSocket.close();
    }

    /**
     * 从目的主机接收
     * 
     * @return 接收到的有序的字节
     * @throws IOException IO异常
     */
    public ByteArrayOutputStream receive() throws IOException {
        int count = 0;  // 接收到的数据帧个数
        int time = 0;   // 同一个数据帧的接收次数
        long max = 0;   // 当前最大接收到的序列号 - base，乱序
        long receiveBase = -1;  // 接收窗口的base
        ByteArrayOutputStream result = new ByteArrayOutputStream(); // 按序输出流
        DatagramSocket datagramSocket = new DatagramSocket(myPort); // server监听socket
        datagramSocket.setSoTimeout(1000);
        List<ByteArrayOutputStream> datagramBuffer = new LinkedList<>();    // 缓存窗口内接收到的数据帧
        DatagramPacket receivePacket;
        
        for(int i = 0; i < windowSize; i ++) {
            datagramBuffer.add(new ByteArrayOutputStream());
        }

        while (true) {
            try {
                byte[] recv = new byte[1500];
                receivePacket = new DatagramPacket(recv, recv.length, host, targetPort);
                datagramSocket.receive(receivePacket);
                // 模拟丢包，即接收之后不处理，当成没接收到
                if(count % loss != 0) {
                    // 提取出接收到的base和序列号
                    long base = recv[0] & 0x0FF;
                    long seq = recv[1] & 0x0FF;
                    if(receiveBase == -1) {
                        receiveBase = base;
                    }
                    // 若发送端base更新（即已经确认了几个数据帧）
                    if(base != receiveBase) {
                        // 从缓存中取出已经确认完成的数据帧拼接
                        ByteArrayOutputStream temp = getBytes(datagramBuffer, (base - receiveBase) > 0 ? (base - receiveBase) : max + 1);
                        // 空出缓存
                        for(int i = 0; i < base - receiveBase; i ++) {
                            datagramBuffer.remove(0);
                            datagramBuffer.add(new ByteArrayOutputStream());
                        }
                        result.write(temp.toByteArray(), 0, temp.size());
                        receiveBase = base;
                        max -= (base - receiveBase);
                    }
                    if(seq - base > max) {
                        max = seq - base;
                    }
                    // 将接收到的数据帧写入缓存
                    ByteArrayOutputStream recvBytes = new ByteArrayOutputStream();
                    recvBytes.write(recv, 2, receivePacket.getLength() - 2);
                    datagramBuffer.set((int) (seq - base), recvBytes);
                    // 返回ACK
                    recv = new byte[1];
                    recv[0] = new Long(seq).byteValue();
                    receivePacket = new DatagramPacket(recv, recv.length, host, targetPort);
                    datagramSocket.send(receivePacket);
                    System.out.println("接收到数据包：base " + base + " seq " + seq);
                }
                count ++;
                time = 0;
            } catch (SocketTimeoutException e) {
                time ++;
            }
            // 超出最大接收时间，则接收结束，写出数据
            if(time > receiveMaxTime) {
                ByteArrayOutputStream temp = getBytes(datagramBuffer, max + 1);
                result.write(temp.toByteArray(), 0, temp.size());
                break;
            }
        }
        datagramSocket.close();
        return result;
    }

    private ByteArrayOutputStream getBytes(List<ByteArrayOutputStream> buffer, long max) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = 0; i < max; i++) {
            if (buffer.get(i) != null)
                result.write(buffer.get(i).toByteArray(), 0, buffer.get(i).size());
        }
        return result;
    }

    private boolean checkWindow(List<Integer> timers) {
        for (Integer timer : timers) {
            if (timer != -1)
                return false;
        }
        return true;
    }

}