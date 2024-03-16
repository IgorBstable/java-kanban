package model;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static model.TaskStatus.*;
import service.*;

class EpicTest {

    // проверяем, что экземпляры класса Epic равны друг другу, если равен их id
    @Test
    void epicEqualsIfIdEquals() {
        Epic epic = new Epic("Test epicEqualsIfIdEquals",
                "Test epicEqualsIfIdEquals description");
        TaskManager taskManager = new Managers();
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
        TaskManager taskManager = new Managers();
        Epic epic = new Epic("Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description");
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test shouldNotAddEpicInEpic",
                "Test shouldNotAddEpicInEpic description", NEW, epic.getId());
        taskManager.makeNewSubtask(subtask);

        assertNotEquals(epic.getId(), subtask.getId());
        // Таким образом, id у подзадачи и эпика РАЗНЫЕ
        // т.е. работает модель, в которой можно добавить подзадачу эпику с содержанием,
        // идентичным содержанию эпика.
        // Если мы хотим изменить эту модель, необходимо менять логику создания подзадачи,
        // которая будет включать проверку равенства соответствующих полей подзадачи с полями эпика.



    }
}