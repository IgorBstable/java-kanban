package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static model.TaskStatus.*;
import service.*;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {

    // проверяем, что экземпляры класса Subtask равны друг другу, если равен их id
    @Test
    void subtaskEqualsIfIdEquals() {
        Epic epic = new Epic(0, "Test addNewSubtask",
                "Test addNewSubtask description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        TaskManager taskManager = Managers.getDefault();
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test addNewTask",
                "Test addNewTask description", NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(5)));
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewSubtask(subtask);

        int subTaskId = subtask.getId();

        assertEquals(taskManager.getSubtaskById(subTaskId), taskManager.getSubtaskById(subTaskId),
                "Подзадачи с одинаковым id не совпадают");
    }

    // Проверяем, что объект Subtask нельзя сделать своим же эпиком.
    // При этом под операцией "Объект Subtask сделать своим же эпиком" понимаю:
    // создание объекта класса Epic (эпик) с указанием имени, описания и id подзадачи
    // при этом id эпика ДОЛЖНО БЫТЬ РАВНЫМ id подзадачи
    @Test
    void shouldNotMakeSubtaskAsItselfEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic(0, "Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description", NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(20)));
        subtask.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewSubtask(subtask);
        Epic epic1 = new Epic(0, "Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description");
        epic1.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(40)));
        epic1.setDuration(Duration.ofMinutes(10));
        taskManager.makeNewEpic(epic1);

        assertNotEquals(subtask.getId(), epic1.getId());
        // Таким образом, id у эпика и подзадачи РАЗНЫЕ
        // т.е. работает модель, в которой можно сделать эпик с содержанием,
        // идентичным содержанию подзадачи.
        // Если мы хотим изменить эту модель, необходимо менять логику создания эпика,
        // которая будет включать проверку равенства соответствующих полей всех подзадач эпика.

    }
}