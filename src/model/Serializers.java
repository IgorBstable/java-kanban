package model;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class Serializers {
    public static Gson taskToGson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .create();
    public static Gson taskFromJson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .create();
    public static Gson subtaskToGson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .create();
    public static Gson subtaskFromGson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .create();
    public static Gson epicToGson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .create();
    public static Gson epicFromGson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
            .create();
}

class TaskSerializer implements JsonSerializer<Task> {
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

class TaskDeserializer implements JsonDeserializer<Task> {
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

class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type type,
                                 JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("id", subtask.getId());
        result.addProperty("type", subtask.getType().toString());
        result.addProperty("name", subtask.getName());
        result.addProperty("status", subtask.getStatus().toString());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("startTime", subtask.getStartTime().toString());
        result.addProperty("duration", subtask.getDuration().toString());
        result.addProperty("endTime", subtask.getEndTime().toString());
        result.addProperty("epicId", subtask.getEpicId().toString());
        return result;
    }
}

class SubtaskDeserializer implements JsonDeserializer<Subtask> {
    @Override
    public Subtask deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext context) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        Subtask subtask = new Subtask(
                o.get("id").getAsInt(),
                o.get("name").getAsString(),
                o.get("description").getAsString(),
                TaskStatus.valueOf(o.get("status").getAsString()),
                o.get("epicId").getAsInt()
        );
        String startTime = o.get("startTime").getAsString();
        LocalDateTime localDateTime = LocalDateTime.parse(startTime);
        subtask.setStartTime(localDateTime);
        String duration = o.get("duration").getAsString();
        Duration d = Duration.parse(duration);
        subtask.setDuration(d);

        return subtask;
    }
}

class EpicSerializer implements JsonSerializer<Epic> {
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

class EpicDeserializer implements JsonDeserializer<Epic> {
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
