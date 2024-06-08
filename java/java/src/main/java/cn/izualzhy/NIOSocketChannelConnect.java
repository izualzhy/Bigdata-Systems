package cn.izualzhy;

import java.net.Socket;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NIOSocketChannelConnect {
    public static void main(String[] args) {
        String host = "192.0.2.1";  // 使用一个无法访问的IP地址来模拟连接超时
        int port = 9092;            // Kafka通常使用的端口
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            System.out.println(LocalDateTime.now().format(dtf) + " - Attempting to connect to " + host + ":" + port);
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true); // 设置为阻塞模式

            // 尝试连接，不设置超时
            socketChannel.connect(new InetSocketAddress(host, port));

            if (socketChannel.isConnected()) {
                System.out.println(LocalDateTime.now().format(dtf) + " - Connected successfully.");
            } else {
                System.out.println(LocalDateTime.now().format(dtf) + " - Connection could not be established.");
            }
            socketChannel.close();
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(dtf) + " - Connection failed: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
