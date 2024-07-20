package service;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static InMemoryTaskManager manager = new InMemoryTaskManager();
    static BaseHttpHandler message = new BaseHttpHandler();
    static HttpServer httpServer;

    public HttpTaskServer() {
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stopServer() {
        httpServer.stop(0);
    }
}






