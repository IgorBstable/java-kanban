package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
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
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Epic.class, new EpicSerializer())
                    .create();

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
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Epic.class, new EpicSerializer())
                    .create();

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
            ArrayList<Task> epics = HttpTaskServer.manager.getAllEpics();
            boolean deleted = false;
            for (Task t : epics) {
                if (t.getId() == id) {
                    HttpTaskServer.manager.delEpicById(id);
                    HttpTaskServer.message.sendText(exchange, "Эпик успешно удален.");
                    deleted = true;
                }
            }
            if (!deleted) {
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
            ArrayList<Task> epics = HttpTaskServer.manager.getAllEpics();
            boolean ifExists = false;
            for (Task t : epics) {
                if (t.getId() == id) {
                    ArrayList<Task> listOfSubtasksOfEpic =
                            HttpTaskServer.manager.getListOfSubTasksOfEpic(id);
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .registerTypeAdapter(Subtask.class, new SubtaskHandler.SubtaskSerializer())
                            .create();

                    String subtasksOfEpicArrayJson = gson.toJson(listOfSubtasksOfEpic);
                    HttpTaskServer.message.sendTextAndData(exchange, subtasksOfEpicArrayJson);
                    ifExists = true;
                }
            }
            if (!ifExists) {
                HttpTaskServer.message.sendNotFound(exchange, "Эпик не удален, " +
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
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Epic.class, new EpicDeserializer())
                    .create();
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

    static class EpicSerializer implements JsonSerializer<Epic> {
        @Override
        public JsonElement serialize(Epic epic, Type type,
                                     JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("id", epic.getId());
            result.addProperty("type", epic.getType().toString());
            result.addProperty("name", epic.getName());
            if (epic.getStatus() == null) {
                result.addProperty("status", "");
            } else {
                result.addProperty("status", epic.getStatus().toString());
            }
            result.addProperty("description", epic.getDescription());
            if (epic.getStartTime() == null) {
                result.addProperty("startTime", "");
            } else {
                result.addProperty("startTime", epic.getStartTime().toString());
            }
            if (epic.getDuration() == null) {
                result.addProperty("duration", "");
            } else {
                result.addProperty("duration", epic.getDuration().toString());
            }
            if (epic.getEndTime() == null) {
                result.addProperty("endTime", "");
            } else {
                result.addProperty("endTime", epic.getEndTime().toString());
            }
            return result;
        }
    }

    static class EpicDeserializer implements JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = jsonElement.getAsJsonObject();
            Epic epic = new Epic(
                    o.get("id").getAsInt(),
                    o.get("name").getAsString(),
                    o.get("description").getAsString()
            );

            if (!o.has("type")) {
                epic.setType(null);
            } else {
                TaskTypes epicType = TaskTypes.valueOf(o.get("type").getAsString());
                epic.setType(epicType);
            }

            if (!o.has("status")) {
                epic.setStatus(null);
            } else {
                TaskStatus epicStatus = TaskStatus.valueOf(o.get("status").getAsString());
                epic.setStatus(epicStatus);
            }

            if (!o.has("startTime") || o.get("startTime").getAsString().isEmpty()) {
                epic.setStartTime(null);
            } else {
                String startTime = o.get("startTime").getAsString();
                LocalDateTime localDateTime = LocalDateTime.parse(startTime);
                epic.setStartTime(localDateTime);
            }

            if (!o.has("duration")  || o.get("duration").getAsString().isEmpty()) {
                epic.setDuration(null);
            } else {
                String duration = o.get("duration").getAsString();
                Duration d = Duration.parse(duration);
                epic.setDuration(d);
            }
            if (!o.has("endTime")  || o.get("endTime").getAsString().isEmpty()) {
                epic.setEndTime(null);
            } else {
                String endTime = o.get("endTime").getAsString();
                LocalDateTime localDateTime = LocalDateTime.parse(endTime);
                epic.setEndTime(localDateTime);
            }
            return epic;
        }
    }
}
