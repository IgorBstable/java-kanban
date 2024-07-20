package model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

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
