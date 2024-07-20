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

public class HttpTaskManagerSubtasksTest {

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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "epic1", "test subtasks");
        HttpTaskServer.manager.makeNewEpic(epic);
        int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask(0, "Test 1", "Testing subtask 1",
                TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setStartTime(LocalDateTime.of(2024, 7, 11, 10, 0));
        Subtask subtask2 = new Subtask(0, "Test 2", "Testing subtask 2",
                TaskStatus.NEW, epicId);
        subtask2.setDuration(Duration.ofMinutes(5));
        subtask2.setStartTime(LocalDateTime.of(2024, 7, 11, 10, 30));
        Gson gson = Serializers.subtaskToGson;
        String Json1 = gson.toJson(subtask1);
        String Json2 = gson.toJson(subtask2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json2))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject JO1 = jsonArray.get(0).getAsJsonObject();
        JsonObject JO2 = jsonArray.get(1).getAsJsonObject();
        String name1 = JO1.get("name").getAsString();
        String name2 = JO2.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> subtasksFromManager = HttpTaskServer.manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", name1);
        assertEquals("Test 2", name2);
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "epic1", "test subtasks");
        HttpTaskServer.manager.makeNewEpic(epic);
        int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask(0, "Test 1", "Testing subtask 1",
                TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setStartTime(LocalDateTime.of(2024, 7, 11, 11, 0));
        Gson gson = Serializers.subtaskToGson;
        String Json1 = gson.toJson(subtask1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            int id = HttpTaskServer.manager.getAllSubtasks().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/subtasks/" + id);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url1)
                    .GET()
                    .build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> subtasksFromManager = HttpTaskServer.manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", name,
                "Некорректное имя подзадачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "epic1", "test subtasks");
        HttpTaskServer.manager.makeNewEpic(epic);
        int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask(0, "Test 1", "Testing subtask 1",
                TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setStartTime(LocalDateTime.of(2024, 7, 11, 12, 0));
        Gson gson = Serializers.subtaskToGson;
        String Json1 = gson.toJson(subtask1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> subtasksFromManager = HttpTaskServer.manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", subtasksFromManager.getFirst().getName(),
                "Некорректное имя подзадачи");
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "epic1", "test subtasks");
        HttpTaskServer.manager.makeNewEpic(epic);
        int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask(0, "Test 1", "Testing subtask 1",
                TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setStartTime(LocalDateTime.of(2024, 7, 11, 13, 0));
        Subtask subtask2 = new Subtask(0, "Test 2", "Testing subtask 2",
                TaskStatus.NEW, epicId);
        subtask2.setDuration(Duration.ofMinutes(5));
        subtask2.setStartTime(LocalDateTime.of(2024, 7, 11, 13, 30));
        Gson gson = Serializers.subtaskToGson;
        String Json1 = gson.toJson(subtask1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            int id = HttpTaskServer.manager.getAllSubtasks().getFirst().getId();
            URI url1 = URI.create("http://localhost:8080/subtasks/" + id);
            subtask2.setId(id);
            String Json2 = gson.toJson(subtask2);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url1)
                    .POST(HttpRequest.BodyPublishers.ofString(Json2))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Task> subtasksFromManager = HttpTaskServer.manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(),
                "Некорректное имя подзадачи");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "epic1", "test subtasks");
        HttpTaskServer.manager.makeNewEpic(epic);
        int epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask(0, "Test 1", "Testing subtask 1",
                TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setStartTime(LocalDateTime.of(2024, 7, 11, 14, 0));
        Subtask subtask2 = new Subtask(0, "Test 2", "Testing subtask 2",
                TaskStatus.NEW, epicId);
        subtask2.setDuration(Duration.ofMinutes(5));
        subtask2.setStartTime(LocalDateTime.of(2024, 7, 11, 14, 30));
        Gson gson = Serializers.subtaskToGson;
        String Json1 = gson.toJson(subtask1);
        String Json2 = gson.toJson(subtask2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json2))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            int id = HttpTaskServer.manager.getAllSubtasks().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/subtasks/" + id);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url1)
                    .DELETE()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> subtasksFromManager = HttpTaskServer.manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName());
    }
}
