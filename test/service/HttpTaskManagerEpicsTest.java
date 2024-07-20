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

public class HttpTaskManagerEpicsTest {

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
    public void testGetEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic(0, "Test 1", "Testing epic1");
        Epic epic2 = new Epic(0, "Test 2", "Testing epic2");
        Gson gson = Serializers.epicToGson;
        String json1 = gson.toJson(epic1);
        String json2 = gson.toJson(epic2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json2))
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
        List<Task> epicsFromManager = HttpTaskServer.manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", name1);
        assertEquals("Test 2", name2);
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic(0, "Test 1", "Testing epic1");
        Gson gson = Serializers.epicToGson;
        String Json1 = gson.toJson(epic1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(Json1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            int id = HttpTaskServer.manager.getAllEpics().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/epics/" + id);
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
        List<Task> epicsFromManager = HttpTaskServer.manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", name,
                "Некорректное имя эпика");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic(0, "Test 1", "Testing epic1");
        epic1.setType(TaskTypes.EPIC);
        epic1.setStatus(TaskStatus.NEW);
        Gson gson = Serializers.epicToGson;
        String taskJson1 = gson.toJson(epic1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> epicsFromManager = HttpTaskServer.manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", epicsFromManager.getFirst().getName(),
                "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic(0, "Test 1", "Testing epic1");
        epic1.setType(TaskTypes.EPIC);
        epic1.setStatus(TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Test 2", "Testing epic2");
        epic2.setType(TaskTypes.EPIC);
        epic2.setStatus(TaskStatus.NEW);
        Gson gson = Serializers.epicToGson;
        String Json1 = gson.toJson(epic1);
        String Json2 = gson.toJson(epic2);
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

            int id = HttpTaskServer.manager.getAllEpics().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/epics/" + id);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url1)
                    .DELETE()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> epicsFromManager = HttpTaskServer.manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", epicsFromManager.getFirst().getName());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic(0, "Test 1", "Testing epic1");
        Gson gsonEpic = Serializers.epicToGson;
        String JsonEpic = gsonEpic.toJson(epic1);

        int epicId;
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(JsonEpic))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            epicId = HttpTaskServer.manager.getAllEpics().getFirst().getId();

            Subtask subtask1 = new Subtask(0, "Test 1 subtask", "Testing subtask 1",
                    TaskStatus.NEW, epicId);
            subtask1.setDuration(Duration.ofMinutes(5));
            subtask1.setStartTime(LocalDateTime.of(2024, 7, 12, 10, 0));
            Gson gsonSubtask = Serializers.subtaskToGson;
            String JsonSubtask = gsonSubtask.toJson(subtask1);
            URI url0 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url0)
                    .POST(HttpRequest.BodyPublishers.ofString(JsonSubtask))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            URI url1 = URI.create("http://localhost:8080/epics/" + epicId + "/" + "subtasks");
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url1)
                    .GET()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> subtasksOfEpicFromManager = HttpTaskServer.manager.getListOfSubTasksOfEpic(epicId);
        assertNotNull(subtasksOfEpicFromManager, "Подзадачи эпика не возвращаются");
        assertEquals(1, subtasksOfEpicFromManager.size(),
                "Некорректное количество подзадач эпика");
        assertEquals("Test 1 subtask", name,
                "Некорректное имя подзадачи эпика");
    }
}
