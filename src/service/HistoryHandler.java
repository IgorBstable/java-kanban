package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY:
                System.out.println("Началась обработка /history (GET)");
                handleGetHistory(exchange);
                break;
            case UNKNOWN:
                System.out.println("Началась обработка UNKNOWN");
                HttpTaskServer.message.sendNotFound(exchange, "Неизвестный запрос.");
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            List<Task> history = HttpTaskServer.manager.getHistory();

            ArrayList<String> history1 = new ArrayList<>();
            for (Task task : history) {
                if (task.getType() == TaskTypes.TASK) {
                    String taskJson = taskToJson(task);
                    history1.add(taskJson);
                }
                if (task.getType() == TaskTypes.SUBTASK) {
                    String subtaskJson = subtaskToJson(task);
                    history1.add(subtaskJson);
                }
                if (task.getType() == TaskTypes.EPIC) {
                    String epicJson = epicToJson(task);
                    history1.add(epicJson);
                }
            }
            String resultJson = history1.toString();
            HttpTaskServer.message.sendTextAndData(exchange, resultJson);
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private String taskToJson(Task task) {
        Gson gson = Serializers.taskToGson;
        return gson.toJson(task);
    }

    private String subtaskToJson(Task task) {
        Gson gson = Serializers.subtaskToGson;
        return gson.toJson(task);
    }

    private String epicToJson(Task task) {
        Gson gson = Serializers.epicToGson;
        return gson.toJson(task);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_HISTORY,
        UNKNOWN
    }
}


