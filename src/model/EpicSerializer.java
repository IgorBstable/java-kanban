package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

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
