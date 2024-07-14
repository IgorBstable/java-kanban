package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static model.TaskStatus.*;
import service.*;
import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    // проверяем, что экземпляры класса Epic равны друг другу, если равен их id
    @Test
    void epicEqualsIfIdEquals() {
        Epic epic = new Epic(0, "Test epicEqualsIfIdEquals",
                "Test epicEqualsIfIdEquals description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        TaskManager taskManager = Managers.getDefault();
        taskManager.makeNewEpic(epic);
        int epicId = epic.getId();

        assertEquals(taskManager.getEpicById(epicId), taskManager.getEpicById(epicId),
                "Эпики с одинаковым id не совпадают");
    }

    // Проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи.
    // При этом операцию "добавление объекта Epic в самого себя в виде подзадачи" понимаю так:
    // создание объекта класса Subtask (подзадачи) с указанием имени, описания и id эпика
    // при этом id подзадачи ДОЛЖНО БЫТЬ РАВНЫМ id эпика
    @Test
    void shouldNotAddEpicInItselfAsSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(15)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);

        assertNotEquals(epic.getId(), subtask.getId());
        // Таким образом, id у подзадачи и эпика РАЗНЫЕ
        // т.е. работает модель, в которой можно добавить подзадачу эпику с содержанием,
        // идентичным содержанию эпика.
        // Если мы хотим изменить эту модель, необходимо менять логику создания подзадачи,
        // которая будет включать проверку равенства соответствующих полей подзадачи с полями эпика.
    }

    @Test
    void allSubtasksHaveStatusNEW() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(15)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);
        Subtask subtask1 = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", NEW, epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(30)));
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask1);

        assertEquals(epic.getStatus(), NEW);
    }

    @Test
    void allSubtasksHaveStatusDONE() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", DONE, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(15)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);
        Subtask subtask1 = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", DONE, epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(30)));
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask1);

        assertEquals(epic.getStatus(), DONE);
    }

    @Test
    void SubtasksHaveStatusNEWandDONE() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(15)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);
        Subtask subtask1 = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", DONE, epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(30)));
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask1);

        assertEquals(epic.getStatus(), IN_PROGRESS);
    }

    @Test
    void allSubtasksHaveStatusIN_PROGRESS() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", IN_PROGRESS, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(15)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);
        Subtask subtask1 = new Subtask(0, "Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", IN_PROGRESS, epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(30)));
        subtask1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask1);

        assertEquals(epic.getStatus(), IN_PROGRESS);
    }
}