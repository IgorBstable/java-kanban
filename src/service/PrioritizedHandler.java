package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.*;
import java.io.IOException;
import java.util.ArrayList;

class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED_TASKS:
                System.out.println("Началась обработка /prioritized (GET)");
                handleGetPrioritized(exchange);
                break;
            case UNKNOWN:
                System.out.println("Началась обработка UNKNOWN");
                HttpTaskServer.message.sendNotFound(exchange, "Неизвестный запрос.");
                break;
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Task> prioritized = HttpTaskServer.manager.getPrioritizedTasks();
            ArrayList<String> prioritized1 = new ArrayList<>();
            for (Task task : prioritized) {
                if (task.getType() == TaskTypes.TASK) {
                    String taskJson = taskToJson(task);
                    prioritized1.add(taskJson);
                }
                if (task.getType() == TaskTypes.SUBTASK) {
                    String subtaskJson = subtaskToJson(task);
                    prioritized1.add(subtaskJson);
                }
            }
            String resultJson = prioritized1.toString();
            HttpTaskServer.message.sendTextAndData(exchange, resultJson);
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private String taskToJson(Task task) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskHandler.TaskSerializer())
                .create();

        return gson.toJson(task);
    }

    private String subtaskToJson(Task task) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Subtask.class, new SubtaskHandler.SubtaskSerializer())
                .create();

        return gson.toJson(task);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED_TASKS;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_PRIORITIZED_TASKS,
        UNKNOWN
    }
}


