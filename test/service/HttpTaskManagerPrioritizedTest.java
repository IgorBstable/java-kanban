package service;

import com.google.gson.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest {

    @BeforeEach
    public void setUp() throws IOException {
        HttpTaskServer.manager.deleteTasks();
        HttpTaskServer.manager.delSubtasks();
        HttpTaskServer.manager.deleteEpics();
        HttpTaskServer.manager.deletePrioritized();
        HttpTaskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stopServer();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        URI urlTasks = URI.create("http://localhost:8080/tasks");
        URI urlSubtasks = URI.create("http://localhost:8080/subtasks");
        URI urlEpics = URI.create("http://localhost:8080/epics");

        Gson gsonTasks = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskHandler.TaskSerializer())
                .create();
        Gson gsonSubtasks = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Subtask.class, new SubtaskHandler.SubtaskSerializer())
                .create();
        Gson gsonEpics = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, new EpicHandler.EpicSerializer())
                .create();

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            Task task = new Task(0, "Test 1 task", "Testing task 1",
                    TaskStatus.NEW, TaskTypes.TASK);
            task.setDuration(Duration.ofMinutes(5));
            task.setStartTime(LocalDateTime.of(2024, 7, 10, 10, 0));
            String jsonTask = gsonTasks.toJson(task);

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(urlTasks)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            Epic epic = new Epic(0, "Test 1 epic", "Testing epic1");

            String jsonEpic = gsonEpics.toJson(epic);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(urlEpics)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();

            Subtask subtask = new Subtask(0, "Test 1 subtask", "Testing subtask 1",
                    TaskStatus.NEW, epicId);
            subtask.setDuration(Duration.ofMinutes(5));
            subtask.setStartTime(LocalDateTime.of(2024, 7, 11, 10, 0));

            String jsonSubtask = gsonSubtasks.toJson(subtask);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(urlSubtasks)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                    .build();
            client.send(request3, HttpResponse.BodyHandlers.ofString());

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/prioritized"))
                    .GET()
                    .build();
            response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        }

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject taskJO = jsonArray.get(0).getAsJsonObject();
        String taskName = taskJO.get("name").getAsString();
        JsonObject subtaskJO = jsonArray.get(1).getAsJsonObject();
        String subtaskName = subtaskJO.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> prioritized = HttpTaskServer.manager.getPrioritizedTasks();
        assertNotNull(prioritized, "Приоритезированный список не формируется");
        assertEquals(2, prioritized.size(), "Некорректное количество позиций " +
                "в приоритезированном списке");
        assertEquals("Test 1 task", taskName);
        assertEquals("Test 1 subtask", subtaskName);
    }
}
