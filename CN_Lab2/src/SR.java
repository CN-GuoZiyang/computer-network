import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class SR {
    
    private InetAddress host;
    private int targetPort, myPort;
    private int windowSize = 16;
    private int sendMaxTime = 2;
    private int receiveMaxTime = 4;
    private long base = 0;
    private int loss = 10;

    public SR(String host, int targetPort, int myPort) throws UnknownHostException {
        this.myPort = myPort;
        this.targetPort = targetPort;
        this.host = InetAddress.getByName(host);
    }

    public void send(byte[] content) throws IOException {
        int sendIndex = 0;
        int length;
        int maxLength = 1024;
        DatagramSocket datagramSocket = new DatagramSocket(myPort);
        List<ByteArrayOutputStream> datagramBuffer = new LinkedList<>();
        List<Integer> timers = new LinkedList<>();
        long sendSeq = base;
        do {

            while(timers.size() < windowSize && sendIndex < content.length && sendSeq < 256) {
                timers.add(0);
                datagramBuffer.add(new ByteArrayOutputStream());
                length = content.length - sendIndex < maxLength ? content.length - sendIndex : maxLength;
                ByteArrayOutputStream one = new ByteArrayOutputStream();
                byte[] temp = new byte[1];
                temp[0] = new Long(base).byteValue();
                one.write(temp, 0, 1);
                temp = new byte[1];
                temp[0] = new Long(sendSeq).byteValue();
                one.write(temp, 0, 1);
                one.write(content, sendIndex, length);
                DatagramPacket packet = new DatagramPacket(one.toByteArray(), one.size(), host, targetPort);
                datagramSocket.send(packet);
                datagramBuffer.get((int)(sendSeq - base)).write(content, sendIndex, length);
                sendIndex += length;
                System.out.println("发送数据包：base " + base + " seq " + sendSeq);
                sendSeq ++;
            }
            datagramSocket.setSoTimeout(1000);
            DatagramPacket receivePacket;
            try {
                while(!checkWindow(timers)) {
                    byte[] recv = new byte[1500];
                    receivePacket = new DatagramPacket(recv, recv.length);
                    datagramSocket.receive(receivePacket);
                    int ack = (int)((recv[0] & 0x0FF) - base);
                    timers.set(ack, -1);
                }
            } catch (SocketTimeoutException e) {
                for(int i = 0; i < timers.size(); i ++) {
                    int tempTime = timers.get(i);
                    if(tempTime != -1) {
                        timers.set(i, tempTime + 1);
                    }
                }
            }
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
            if(base >= 256) {
                base -= 256;
                sendSeq -= 256;
            }

        } while (sendIndex < content.length || timers.size() != 0);
        datagramSocket.close();
    }

    public ByteArrayOutputStream receive() throws IOException {
        int count = 0;
        int time = 0;
        long max = 0;
        long receiveBase = -1;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DatagramSocket datagramSocket = new DatagramSocket(myPort);
        datagramSocket.setSoTimeout(1000);
        List<ByteArrayOutputStream> datagramBuffer = new LinkedList<>();
        DatagramPacket receivePacket = null;
        for(int i = 0; i < windowSize; i ++) {
            datagramBuffer.add(new ByteArrayOutputStream());
        }
        while (true) {
            try {
                byte[] recv = new byte[1500];
                receivePacket = new DatagramPacket(recv, recv.length, host, targetPort);
                datagramSocket.receive(receivePacket);
                if(count % loss != 0) {
                    long base = recv[0] & 0x0FF;
                    long seq = recv[1] & 0x0FF;
                    if(receiveBase == -1) {
                        receiveBase = base;
                    }
                    if(base != receiveBase) {
                        ByteArrayOutputStream temp = getBytes(datagramBuffer, (base - receiveBase) > 0 ? (base - receiveBase) : max + 1);
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
                    ByteArrayOutputStream recvBytes = new ByteArrayOutputStream();
                    recvBytes.write(recv, 2, receivePacket.getLength() - 2);
                    datagramBuffer.set((int) (seq - base), recvBytes);
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