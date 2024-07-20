package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

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
