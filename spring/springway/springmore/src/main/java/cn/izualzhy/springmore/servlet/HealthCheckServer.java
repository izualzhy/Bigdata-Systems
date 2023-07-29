package cn.izualzhy.springmore.servlet;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

//@Component
public class HealthCheckServer {
    private HttpServer httpServer;
    @Value("${DOCKER_HEALTH_CHECK_PORT:-1}")
    private int port;

    @PostConstruct
    public void init() {
        start();
    }
    public void start() {
        try {
            System.out.println("port:" + port);
            httpServer = HttpServer.create(new InetSocketAddress(8016), 0);

            httpServer.createContext("/ready", new ReadyCallHandler());
            httpServer.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (httpServer != null) {
            httpServer.stop(10);
        }
    }

    public class ReadyCallHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write("success".getBytes());
            outputStream.close();
        }
    }

    public static void main(String[] args) {
        HealthCheckServer healthCheckServer = new HealthCheckServer();
        healthCheckServer.start();
    }
}
