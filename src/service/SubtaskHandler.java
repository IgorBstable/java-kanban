package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Serializers;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                System.out.println("Началась обработка /subtasks (GET)");
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK_BY_ID:
                System.out.println("Началась обработка /subtasks/{id} (GET)");
                handleGetSubtaskById(exchange);
                break;
            case CREATE_SUBTASK:
                System.out.println("Началась обработка /subtasks/ (POST)");
                handleCreateSubtask(exchange);
                break;
            case UPDATE_SUBTASK:
                System.out.println("Началась обработка /subtasks/{id} (POST)");
                handleUpdateSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                System.out.println("Началась обработка /subtasks/{id} (DELETE)");
                handleDeleteSubtask(exchange);
                break;
            case UNKNOWN:
                System.out.println("Началась обработка UNKNOWN");
                HttpTaskServer.message.sendNotFound(exchange, "Неизвестный запрос.");
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Task> subtasks = HttpTaskServer.manager.getAllSubtasks();
            Gson gson = Serializers.subtaskToGson;

            String tasksArrayJson = gson.toJson(subtasks);
            HttpTaskServer.message.sendTextAndData(exchange, tasksArrayJson);
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getId(exchange);
            if (idOpt.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Подзадачи с таким id не обнаружено");
                return;
            }
            int id = idOpt.get();
            Task subtask = HttpTaskServer.manager.getSubtaskById(id);
            if (subtask == null) {
                throw new NotFoundException("Подзадача не найдена!");
            }
            Gson gson = Serializers.subtaskToGson;

            String taskJson = gson.toJson(subtask);
            HttpTaskServer.message.sendTextAndData(exchange, taskJson);
        } catch (NotFoundException e) {
            HttpTaskServer.message.sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try {
            Task subtask = getSubtaskFromRequest(exchange);

            if (HttpTaskServer.manager.isTaskNotIntersected(subtask) &&
                    !HttpTaskServer.manager.subTasks.containsKey(subtask.getId())) {
                HttpTaskServer.manager.makeNewSubtask(subtask);
                HttpTaskServer.message.sendText(exchange, "Подзадача успешно добавлена");
            } else {
                HttpTaskServer.message.sendHasInteractions(exchange, "Подзадача не добавлена, " +
                        "поскольку пересекается с существующими");
            }
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);
            if (optId.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Неверный формат id. " +
                        "Введите id подзадачи.");
                return;
            }

            Subtask subtask = getSubtaskFromRequest(exchange);
            if (!HttpTaskServer.manager.isTaskNotIntersected(subtask)) {
                HttpTaskServer.message.sendHasInteractions(exchange, "Подзадача не обновлена, " +
                        "т.к. пересекается с другими.");
                return;
            }

            int id = optId.get();
            Task subtask1 = HttpTaskServer.manager.getSubtaskById(id);
            if (subtask1 != null) {
                HttpTaskServer.manager.updateSubTask(subtask);
                HttpTaskServer.message.sendText(exchange, "Подзадача успешно обновлена.");
            } else {
                HttpTaskServer.message.sendNotFound(exchange, "Подзадача не обновлена, " +
                        "т.к. не найдено задачи с указанным id.");
            }
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);

            if (optId.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Неверный формат id. " +
                        "Введите id подзадачи.");
                return;
            }
            int id = optId.get();
            ArrayList<Task> subtasks = HttpTaskServer.manager.getAllSubtasks();
            boolean deleted = false;
            for (Task t : subtasks) {
                if (t.getId() == id) {
                    HttpTaskServer.manager.delSubtaskById(id);
                    HttpTaskServer.message.sendText(exchange, "Подзадача успешно удалена.");
                    deleted = true;
                }
            }
            if (!deleted) {
                HttpTaskServer.message.sendNotFound(exchange, "Подзадача не удалена, " +
                        "т.к. не найдено подзадачи с указанным id.");
            }
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Subtask getSubtaskFromRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String input = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(input);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Gson gson = Serializers.subtaskFromGson;
            return gson.fromJson(jsonObject, Subtask.class);
        } catch (Exception e) {
            HttpTaskServer.message.sendNotFound(exchange, "Произошла ошибка!");
        }
        return null;
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_SUBTASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK_BY_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.UPDATE_SUBTASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_SUBTASKS,
        GET_SUBTASK_BY_ID,
        CREATE_SUBTASK,
        UPDATE_SUBTASK,
        DELETE_SUBTASK,
        UNKNOWN
    }
}
