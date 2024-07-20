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

public class HttpTaskManagerHistoryTest {

    @BeforeEach
    public void setUp() throws IOException {
        HttpTaskServer.manager.deleteTasks();
        HttpTaskServer.manager.delSubtasks();
        HttpTaskServer.manager.deleteEpics();
        HttpTaskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stopServer();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        URI urlTasks = URI.create("http://localhost:8080/tasks");
        URI urlSubtasks = URI.create("http://localhost:8080/subtasks");
        URI urlEpics = URI.create("http://localhost:8080/epics");

        Gson gsonTasks = Serializers.taskToGson;
        Gson gsonSubtasks = Serializers.subtaskToGson;
        Gson gsonEpics = Serializers.epicToGson;

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

            int taskId = HttpTaskServer.manager.getAllTasks().getFirst().getId();

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

            int subtaskId = HttpTaskServer.manager.getAllSubtasks().getFirst().getId();

            URI urlTaskById = URI.create("http://localhost:8080/tasks/" + taskId);
            URI urlSubtaskById = URI.create("http://localhost:8080/subtasks/" + subtaskId);
            URI urlEpicById = URI.create("http://localhost:8080/epics/" + epicId);

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(urlTaskById)
                    .GET()
                    .build();
            client.send(request4, HttpResponse.BodyHandlers.ofString());

            HttpRequest request5 = HttpRequest.newBuilder()
                    .uri(urlSubtaskById)
                    .GET()
                    .build();
            client.send(request5, HttpResponse.BodyHandlers.ofString());

            HttpRequest request6 = HttpRequest.newBuilder()
                    .uri(urlEpicById)
                    .GET()
                    .build();
            client.send(request6, HttpResponse.BodyHandlers.ofString());

            HttpRequest request7 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/history"))
                    .GET()
                    .build();
            response = client.send(request7, HttpResponse.BodyHandlers.ofString());
        }

        System.out.println(response);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject taskJO = jsonArray.get(0).getAsJsonObject();
        String taskName = taskJO.get("name").getAsString();
        JsonObject epicJO = jsonArray.get(2).getAsJsonObject();
        String epicName = epicJO.get("name").getAsString();
        JsonObject subtaskJO = jsonArray.get(1).getAsJsonObject();
        String subtaskName = subtaskJO.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> history = HttpTaskServer.manager.getHistory();
        assertNotNull(history, "История не формируется");
        assertEquals(3, history.size(), "Некорректное количество позиций в истории");
        assertEquals("Test 1 task", taskName);
        assertEquals("Test 1 subtask", subtaskName);
        assertEquals("Test 1 epic", epicName);
    }
}
