package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static model.TaskStatus.*;
import service.*;

class SubtaskTest {

    // проверяем, что экземпляры класса Subtask равны друг другу, если равен их id
    @Test
    void subtaskEqualsIfIdEquals() {
        Epic epic = new Epic("Test addNewSubtask",
                "Test addNewSubtask description");
        TaskManager taskManager = Managers.getDefault();
        taskManager.makeNewEpic(epic);
        Subtask subTask = new Subtask("Test addNewTask",
                "Test addNewTask description", NEW, epic.getId());
        taskManager.makeNewSubtask(subTask);

        int subTaskId = subTask.getId();

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
        Epic epic = new Epic("Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description");
        taskManager.makeNewEpic(epic);
        Subtask subTask = new Subtask("Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description", NEW, epic.getId());
        taskManager.makeNewSubtask(subTask);
        Epic epic1 = new Epic("Test shouldNotMakeSubtaskAsEpic",
                "Test shouldNotMakeSubtaskAsEpic description");
        taskManager.makeNewEpic(epic1);

        assertNotEquals(subTask.getId(), epic1.getId());
        // Таким образом, id у эпика и подзадачи РАЗНЫЕ
        // т.е. работает модель, в которой можно сделать эпик с содержанием,
        // идентичным содержанию подзадачи.
        // Если мы хотим изменить эту модель, необходимо менять логику создания эпика,
        // которая будет включать проверку равенства соответствующих полей всех подзадач эпика.

    }
}