package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import model.TaskStatus;
import model.TaskTypes;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static service.HttpTaskServer.manager;
import static service.HttpTaskServer.message;

class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                System.out.println("Началась обработка /tasks (GET)");
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                System.out.println("Началась обработка /tasks/{id} (GET)");
                handleGetTaskById(exchange);
                break;
            case CREATE_TASK:
                System.out.println("Началась обработка /tasks/ (POST)");
                handleCreateTask(exchange);
                break;
            case UPDATE_TASK:
                System.out.println("Началась обработка /tasks/{id} (POST)");
                handleUpdateTask(exchange);
                break;
            case DELETE_TASK:
                System.out.println("Началась обработка /tasks/{id} (DELETE)");
                handleDeleteTask(exchange);
                break;
            case UNKNOWN:
                System.out.println("Началась обработка UNKNOWN");
                message.sendNotFound(exchange, "Неизвестный запрос.");
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Task> tasks = manager.getAllTasks();
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Task.class, new TaskSerializer())
                    .create();

            String tasksArrayJson = gson.toJson(tasks);
            message.sendTextAndData(exchange, tasksArrayJson);
        } catch (Exception e) {
            message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getId(exchange);
            if (idOpt.isEmpty()) {
                message.sendNotFound(exchange, "Задачи с таким id не обнаружено");
                return;
            }
            int id = idOpt.get();
            Task task = manager.getTaskById(id);
            if (task == null) {
                throw new NotFoundException("Задача не найдена!");
            }
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Task.class, new TaskSerializer())
                    .create();

            String taskJson = gson.toJson(task);
            message.sendTextAndData(exchange, taskJson);
        } catch (NotFoundException e) {
            message.sendNotFound(exchange, e.getMessage());
        }
        catch (Exception e) {
            message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = getTaskFromRequest(exchange);

            if (manager.isTaskNotIntersected(task) &&
                    !manager.tasks.containsKey(task.getId())) {
                manager.makeNewTask(task);
                message.sendText(exchange, "Задача успешно добавлена");
            } else {
                message.sendHasInteractions(exchange, "Задача не добавлена, поскольку " +
                        "пересекается с существующими");
            }
        } catch (Exception e) {
            message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);
            if (optId.isEmpty()) {
                message.sendNotFound(exchange, "Неверный формат id. Введите id задачи.");
                return;
            }

            Task task = getTaskFromRequest(exchange);
            if (!manager.isTaskNotIntersected(task)) {
                message.sendHasInteractions(exchange, "Задача не обновлена, т.к. " +
                        "пересекается с другими.");
                return;
            }

            int id = optId.get();
            ArrayList<Task> tasks = manager.getAllTasks();
            boolean updated = false;
            for (Task t : tasks) {
                if (t.getId() == id) {
                    manager.updateTask(task);
                    message.sendText(exchange, "Задача успешно обновлена.");
                    updated = true;
                }
            }
            if (!updated) {
                message.sendNotFound(exchange, "Задача не обновлена, т.к. не найдено задачи " +
                        "с указанным id.");
            }
        } catch (Exception e) {
            message.sendInternalServerError(exchange,
                    "Произошла ошибка при обработке запроса");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> optId = getId(exchange);

            if (optId.isEmpty()) {
                message.sendNotFound(exchange, "Неверный формат id. Введите id задачи.");
                return;
            }
            int id = optId.get();
            ArrayList<Task> tasks = manager.getAllTasks();
            boolean deleted = false;
            for (Task t : tasks) {
                if (t.getId() == id) {
                    manager.deleteTaskById(id);
                    message.sendText(exchange, "Задача успешно удалена.");
                    deleted = true;
                }
            }
            if (!deleted) {
                message.sendNotFound(exchange, "Задача не удалена, т.к. не найдено задачи " +
                        "с указанным id.");
            }
        } catch (Exception e) {
            message.sendInternalServerError(exchange,
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

    private Task getTaskFromRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String input = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(input);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Task.class, new TaskDeserializer())
                    .create();
            return gson.fromJson(jsonObject, Task.class);
        } catch (Exception e) {
            message.sendNotFound(exchange, "Произошла ошибка!");
        }
        return null;
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.UPDATE_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }


    enum Endpoint {
        GET_TASKS,
        GET_TASK_BY_ID,
        CREATE_TASK,
        UPDATE_TASK,
        DELETE_TASK,
        UNKNOWN
    }

    static class TaskSerializer implements JsonSerializer<Task> {
        @Override
        public JsonElement serialize(Task task, Type type,
                                     JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("id", task.getId());
            result.addProperty("type", task.getType().toString());
            result.addProperty("name", task.getName());
            result.addProperty("status", task.getStatus().toString());
            result.addProperty("description", task.getDescription());
            result.addProperty("startTime", task.getStartTime().toString());
            result.addProperty("duration", task.getDuration().toString());
            if (task.getEndTime() == null) {
                result.addProperty("endTime", "");
            } else {
                result.addProperty("endTime", task.getEndTime().toString());
            }

            return result;
        }
    }

    static class TaskDeserializer implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = jsonElement.getAsJsonObject();
            Task task = new Task(
                    o.get("id").getAsInt(),
                    o.get("name").getAsString(),
                    o.get("description").getAsString(),
                    TaskStatus.valueOf(o.get("status").getAsString()),
                    TaskTypes.valueOf(o.get("type").getAsString())
            );
            String startTime = o.get("startTime").getAsString();
            LocalDateTime localDateTime = LocalDateTime.parse(startTime);
            task.setStartTime(localDateTime);
            String duration = o.get("duration").getAsString();
            Duration d = Duration.parse(duration);
            task.setDuration(d);

            return task;
        }
    }
}
