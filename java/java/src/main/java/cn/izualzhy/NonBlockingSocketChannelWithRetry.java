package cn.izualzhy;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NonBlockingSocketChannelWithRetry {
    public static void main(String[] args) {
        String host = "192.0.2.1";  // 使用一个无法访问的IP地址来模拟连接超时
        int port = 9092;            // Kafka通常使用的端口
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Selector selector = Selector.open();
             SocketChannel socketChannel = SocketChannel.open()) {

            socketChannel.configureBlocking(false); // 设置为非阻塞模式
            System.out.println(LocalDateTime.now().format(dtf) + " - Attempting to connect to " + host + ":" + port);

            if (!socketChannel.connect(new InetSocketAddress(host, port))) {
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
                while (selector.select() > 0) {  // 无超时，直到有事件发生
//              while (selector.select(10000) > 0) {  // 超时10s
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();
                        if (key.isConnectable()) {
                            if (socketChannel.finishConnect()) {
                                System.out.println(LocalDateTime.now().format(dtf) + " - Connected successfully.");
                                return;
                            } else {
                                System.out.println(LocalDateTime.now().format(dtf) + " - finishConnect() return false.");
                            }
                        } else {
                            System.out.println(LocalDateTime.now().format(dtf) + " - key.isConnectable() return false.");
                        }
                    }
                    System.out.println(LocalDateTime.now().format(dtf) + " - finish keyIterator");
                }
                System.out.println(LocalDateTime.now().format(dtf) + " - finish selector.select");
            } else {
                System.out.println(LocalDateTime.now().format(dtf) + " - Connected immediately!");
            }
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(dtf) + " - Connection failed: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
