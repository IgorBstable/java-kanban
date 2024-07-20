package model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

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

        if (!o.has("duration") || o.get("duration").getAsString().isEmpty()) {
            epic.setDuration(null);
        } else {
            String duration = o.get("duration").getAsString();
            Duration d = Duration.parse(duration);
            epic.setDuration(d);
        }
        if (!o.has("endTime") || o.get("endTime").getAsString().isEmpty()) {
            epic.setEndTime(null);
        } else {
            String endTime = o.get("endTime").getAsString();
            LocalDateTime localDateTime = LocalDateTime.parse(endTime);
            epic.setEndTime(localDateTime);
        }
        return epic;
    }
}
