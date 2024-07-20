package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

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
