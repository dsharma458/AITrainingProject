package com.example.taskpriority.service;

import com.example.taskpriority.dto.TaskRequest;
import com.example.taskpriority.exception.InvalidTaskInputException;
import com.example.taskpriority.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    private TaskRequest buildRequest(String name, int urgency, int importance) {
        TaskRequest req = new TaskRequest();
        req.setName(name);
        req.setUrgency(urgency);
        req.setImportance(importance);
        return req;
    }

    // --- Priority Score Calculation ---

    @Test
    void shouldCalculatePriorityScore() {
        Task task = taskService.createTask(buildRequest("Test Task", 4, 3));
        assertEquals(12, task.getPriorityScore());
    }

    @Test
    void shouldCalculateMaxPriorityScore() {
        Task task = taskService.createTask(buildRequest("Max Task", 5, 5));
        assertEquals(25, task.getPriorityScore());
    }

    @Test
    void shouldCalculateMinPriorityScore() {
        Task task = taskService.createTask(buildRequest("Min Task", 1, 1));
        assertEquals(1, task.getPriorityScore());
    }

    // --- Sorting ---

    @Test
    void shouldReturnTasksSortedByPriorityScoreDescending() {
        taskService.createTask(buildRequest("Low Priority", 1, 2));
        taskService.createTask(buildRequest("High Priority", 5, 5));
        taskService.createTask(buildRequest("Mid Priority", 3, 3));

        List<Task> sorted = taskService.getAllTasksSorted();

        assertEquals("High Priority", sorted.get(0).getName());
        assertEquals("Mid Priority", sorted.get(1).getName());
        assertEquals("Low Priority", sorted.get(2).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksExist() {
        assertTrue(taskService.getAllTasksSorted().isEmpty());
    }

    @Test
    void shouldReturnSingleTaskAsSortedList() {
        taskService.createTask(buildRequest("Only Task", 3, 4));
        List<Task> sorted = taskService.getAllTasksSorted();
        assertEquals(1, sorted.size());
        assertEquals("Only Task", sorted.get(0).getName());
    }

    // --- Validation: Name ---

    @Test
    void shouldThrowWhenNameIsNull() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest(null, 3, 3)));
        assertEquals("Task name must not be empty.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest("   ", 3, 3)));
        assertEquals("Task name must not be empty.", ex.getMessage());
    }

    // --- Validation: Urgency Boundaries ---

    @Test
    void shouldThrowWhenUrgencyIsTooLow() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest("Task", 0, 3)));
        assertEquals("Urgency must be between 1 and 5, got: 0", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUrgencyIsTooHigh() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest("Task", 6, 3)));
        assertEquals("Urgency must be between 1 and 5, got: 6", ex.getMessage());
    }

    // --- Validation: Importance Boundaries ---

    @Test
    void shouldThrowWhenImportanceIsTooLow() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest("Task", 3, 0)));
        assertEquals("Importance must be between 1 and 5, got: 0", ex.getMessage());
    }

    @Test
    void shouldThrowWhenImportanceIsTooHigh() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.createTask(buildRequest("Task", 3, 6)));
        assertEquals("Importance must be between 1 and 5, got: 6", ex.getMessage());
    }

    // --- Update ---

    @Test
    void shouldRecalculateScoreOnUpdate() {
        Task task = taskService.createTask(buildRequest("Task", 2, 2));
        Task updated = taskService.updateTask(task.getId(), buildRequest("Updated Task", 5, 4));
        assertEquals(20, updated.getPriorityScore());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentTask() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.updateTask("non-existent-id", buildRequest("Task", 3, 3)));
        assertTrue(ex.getMessage().contains("Task not found"));
    }

    @Test
    void shouldPreserveTaskIdAfterUpdate() {
        Task original = taskService.createTask(buildRequest("Original", 2, 2));
        Task updated = taskService.updateTask(original.getId(), buildRequest("Renamed", 4, 4));
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    void shouldUpdateTaskNameOnUpdate() {
        Task task = taskService.createTask(buildRequest("Old Name", 2, 2));
        Task updated = taskService.updateTask(task.getId(), buildRequest("New Name", 2, 2));
        assertEquals("New Name", updated.getName());
    }

    @Test
    void shouldThrowWhenUpdatingWithInvalidUrgency() {
        Task task = taskService.createTask(buildRequest("Task", 3, 3));
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.updateTask(task.getId(), buildRequest("Task", 0, 3)));
        assertEquals("Urgency must be between 1 and 5, got: 0", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUpdatingWithInvalidImportance() {
        Task task = taskService.createTask(buildRequest("Task", 3, 3));
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.updateTask(task.getId(), buildRequest("Task", 3, 6)));
        assertEquals("Importance must be between 1 and 5, got: 6", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUpdatingWithBlankName() {
        Task task = taskService.createTask(buildRequest("Task", 3, 3));
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.updateTask(task.getId(), buildRequest("", 3, 3)));
        assertEquals("Task name must not be empty.", ex.getMessage());
    }

    // --- Delete ---

    @Test
    void shouldDeleteTask() {
        Task task = taskService.createTask(buildRequest("To Delete", 3, 3));
        taskService.deleteTask(task.getId());
        assertTrue(taskService.getAllTasksSorted().isEmpty());
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTask() {
        InvalidTaskInputException ex = assertThrows(InvalidTaskInputException.class,
                () -> taskService.deleteTask("non-existent-id"));
        assertTrue(ex.getMessage().contains("Task not found"));
    }

    @Test
    void shouldReduceTaskCountAfterDeletion() {
        taskService.createTask(buildRequest("Task A", 2, 2));
        Task taskB = taskService.createTask(buildRequest("Task B", 3, 3));
        taskService.deleteTask(taskB.getId());
        assertEquals(1, taskService.getAllTasksSorted().size());
    }

    @Test
    void shouldNotAffectRemainingTasksAfterDeletion() {
        Task taskA = taskService.createTask(buildRequest("Task A", 4, 4));
        Task taskB = taskService.createTask(buildRequest("Task B", 2, 2));
        taskService.deleteTask(taskB.getId());
        List<Task> remaining = taskService.getAllTasksSorted();
        assertEquals(1, remaining.size());
        assertEquals(taskA.getId(), remaining.get(0).getId());
    }

    @Test
    void shouldAssignUniqueIdsToEachTask() {
        Task task1 = taskService.createTask(buildRequest("Task 1", 3, 3));
        Task task2 = taskService.createTask(buildRequest("Task 2", 3, 3));
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void shouldAcceptUrgencyAtLowerBoundary() {
        Task task = taskService.createTask(buildRequest("Task", 1, 3));
        assertEquals(3, task.getPriorityScore());
    }

    @Test
    void shouldAcceptUrgencyAtUpperBoundary() {
        Task task = taskService.createTask(buildRequest("Task", 5, 3));
        assertEquals(15, task.getPriorityScore());
    }

    @Test
    void shouldAcceptImportanceAtLowerBoundary() {
        Task task = taskService.createTask(buildRequest("Task", 3, 1));
        assertEquals(3, task.getPriorityScore());
    }

    @Test
    void shouldAcceptImportanceAtUpperBoundary() {
        Task task = taskService.createTask(buildRequest("Task", 3, 5));
        assertEquals(15, task.getPriorityScore());
    }

    @Test
    void shouldProduceAsymmetricScoreWhenUrgencyAndImportanceDiffer() {
        Task highUrgency = taskService.createTask(buildRequest("High Urgency", 5, 1));
        Task highImportance = taskService.createTask(buildRequest("High Importance", 1, 5));
        assertEquals(highUrgency.getPriorityScore(), highImportance.getPriorityScore());
    }
}
