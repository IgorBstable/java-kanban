package service;

import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager> {

    public InMemoryTaskManagerTest setup() {
        InMemoryTaskManagerTest imtmt = new InMemoryTaskManagerTest();
        imtmt.taskManager = new InMemoryTaskManager();
        return imtmt;
    }

    @Test
    void standardManagerTests() {
        setup().addTasks();
        setup().updateTask();
        setup().deleteTasks();
        setup().updateSubTask();
        setup().delSubtasks();
        setup().deleteEpics();
        setup().delEpicById();
        setup().subtaskShouldNotSaveOldId();
        setup().epicDoNotContainNonActualSubtaskId();
        setup().settersTest();
        setup().newManager();
        setup().taskEqualsAfterAdd();
    }
}