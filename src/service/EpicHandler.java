package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                System.out.println("Началась обработка /epics (GET)");
                handleGetEpics(exchange);
                break;
            case GET_EPIC_BY_ID:
                System.out.println("Началась обработка /epics/{id} (GET)");
                handleGetEpicById(exchange);
                break;
            case CREATE_EPIC:
                System.out.println("Началась обработка /epics/ (POST)");
                handleCreateEpic(exchange);
                break;
            case DELETE_EPIC:
                System.out.println("Началась обработка /epics/{id} (DELETE)");
                handleDeleteEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                System.out.println("Началась обработка /epics/{id}/subtasks");
                handleGetEpicSubtasks(exchange);
                break;
            case UNKNOWN:
                System.out.println("Началась обработка UNKNOWN");
                HttpTaskServer.message.sendNotFound(exchange, "Неизвестный запрос.");
                break;
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Task> epics = HttpTaskServer.manager.getAllEpics();
            Gson gson = Serializers.epicToGson;
            String epicsArrayJson = gson.toJson(epics);
            HttpTaskServer.message.sendTextAndData(exchange, epicsArrayJson);
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getId(exchange);
            if (idOpt.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Эпика с таким id не обнаружено");
                return;
            }
            int id = idOpt.get();
            Task epic = HttpTaskServer.manager.getEpicById(id);
            if (epic == null) {
                throw new NotFoundException("Эпик не найден!");
            }
            Gson gson = Serializers.epicToGson;

            String epicJson = gson.toJson(epic);
            HttpTaskServer.message.sendTextAndData(exchange, epicJson);
        } catch (NotFoundException e) {
            HttpTaskServer.message.sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        try {
            Task epic = getEpicFromRequest(exchange);

            if (!HttpTaskServer.manager.subTasks.containsKey(epic.getId())) {
                HttpTaskServer.manager.makeNewEpic(epic);
                HttpTaskServer.message.sendText(exchange, "Эпик успешно добавлен");
            } else {
                HttpTaskServer.message.sendHasInteractions(exchange,
                        "Эпик не добавлен, поскольку эпик с таким id уже существует");
            }
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);

            if (optId.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Неверный формат id. " +
                        "Введите id эпика.");
                return;
            }
            int id = optId.get();
            Task epic = HttpTaskServer.manager.getEpicById(id);
            if (epic != null) {
                HttpTaskServer.manager.delEpicById(id);
                HttpTaskServer.message.sendText(exchange, "Эпик успешно удален.");
            } else {
                HttpTaskServer.message.sendNotFound(exchange, "Эпик не удален, " +
                        "т.к. не найдено эпика с указанным id.");
            }
        } catch (Exception e) {
            HttpTaskServer.message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);

            if (optId.isEmpty()) {
                HttpTaskServer.message.sendNotFound(exchange, "Неверный формат id. " +
                        "Введите id эпика.");
                return;
            }
            int id = optId.get();
            Task epic = HttpTaskServer.manager.getEpicById(id);
            if (epic != null) {
                ArrayList<Task> listOfSubtasksOfEpic =
                        HttpTaskServer.manager.getListOfSubTasksOfEpic(id);
                Gson gson = Serializers.subtaskToGson;
                String subtasksOfEpicArrayJson = gson.toJson(listOfSubtasksOfEpic);
                HttpTaskServer.message.sendTextAndData(exchange, subtasksOfEpicArrayJson);
            } else {
                HttpTaskServer.message.sendNotFound(exchange, "Подзадачи не получены, " +
                        "т.к. не найдено эпика с указанным id.");
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

    private Epic getEpicFromRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String input = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(input);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Gson gson = Serializers.epicFromGson;
            return gson.fromJson(jsonObject, Epic.class);
        } catch (Exception e) {
            HttpTaskServer.message.sendNotFound(exchange, "Произошла ошибка!");
        }
        return null;
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_EPIC;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("epics")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_EPICS,
        GET_EPIC_BY_ID,
        CREATE_EPIC,
        DELETE_EPIC,
        GET_EPIC_SUBTASKS,
        UNKNOWN
    }
}
