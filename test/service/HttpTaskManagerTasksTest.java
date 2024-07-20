package service;

import com.google.gson.*;
import model.Serializers;
import model.Task;
import model.TaskStatus;
import model.TaskTypes;
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

public class HttpTaskManagerTasksTest {

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
    public void testGetTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task(0, "Test 1", "Testing task 1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.of(2024, 7, 10, 10, 0));
        Task task2 = new Task(0, "Test 2", "Testing task 2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.of(2024, 7, 10, 10, 30));
        Gson gson = Serializers.taskToGson;
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
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
        JsonObject task1JO = jsonArray.get(0).getAsJsonObject();
        JsonObject task2JO = jsonArray.get(1).getAsJsonObject();
        String name1 = task1JO.get("name").getAsString();
        String name2 = task2JO.get("name").getAsString();

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = HttpTaskServer.manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", name1);
        assertEquals("Test 2", name2);
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task(0, "Test 1", "Testing task 1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.of(2024, 7, 10, 11, 0));
        Gson gson = Serializers.taskToGson;
        String taskJson1 = gson.toJson(task1);
        HttpResponse<String> response2;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());

            int id = HttpTaskServer.manager.getAllTasks().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/tasks/" + id);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url1)
                    .GET()
                    .build();
            response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }

        JsonElement jsonElement = JsonParser.parseString(response2.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name1 = jsonObject.get("name").getAsString();

        assertEquals(200, response2.statusCode());
        List<Task> tasksFromManager = HttpTaskServer.manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", name1,
                "Некорректное имя задачи");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task(0, "Test 1", "Testing task 1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.of(2024, 7, 10, 12, 0));
        Task task2 = new Task(0, "Test 2", "Testing task 2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.of(2024, 7, 10, 12, 30));
        Gson gson = Serializers.taskToGson;
        String taskJson1 = gson.toJson(task1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = HttpTaskServer.manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getName(),
                "Некорректное имя задачи");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task(0, "Test 1", "Testing task 1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.of(2024, 7, 10, 13, 0));
        Task task2 = new Task(0, "Test 2", "Testing task 2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.of(2024, 7, 10, 13, 30));
        Gson gson = Serializers.taskToGson;
        String taskJson1 = gson.toJson(task1);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            int id = HttpTaskServer.manager.getAllTasks().getFirst().getId();
            URI url1 = URI.create("http://localhost:8080/tasks/" + id);
            task2.setId(id);
            String taskJson2 = gson.toJson(task2);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url1)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = HttpTaskServer.manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(),
                "Некорректное имя задачи");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task(0, "Test 1", "Testing task 1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.of(2024, 7, 10, 14, 0));
        Task task2 = new Task(0, "Test 2", "Testing task 2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.of(2024, 7, 12, 14, 30));
        Gson gson = Serializers.taskToGson;
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                    .build();
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            int id = HttpTaskServer.manager.getAllTasks().getFirst().getId();

            URI url1 = URI.create("http://localhost:8080/tasks/" + id);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(url1)
                    .DELETE()
                    .build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = HttpTaskServer.manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName());
    }
}
