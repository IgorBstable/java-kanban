package model;

import com.google.gson.*;

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
