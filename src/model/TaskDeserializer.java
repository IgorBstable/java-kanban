package model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

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
