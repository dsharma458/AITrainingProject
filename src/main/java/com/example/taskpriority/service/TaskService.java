package com.example.taskpriority.service;

import com.example.taskpriority.dto.TaskRequest;
import com.example.taskpriority.exception.InvalidTaskInputException;
import com.example.taskpriority.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private final Map<String, Task> taskStore = new LinkedHashMap<>();

    private void validateRequest(TaskRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidTaskInputException("Task name must not be empty.");
        }
        if (request.getUrgency() < 1 || request.getUrgency() > 5) {
            throw new InvalidTaskInputException("Urgency must be between 1 and 5, got: " + request.getUrgency());
        }
        if (request.getImportance() < 1 || request.getImportance() > 5) {
            throw new InvalidTaskInputException("Importance must be between 1 and 5, got: " + request.getImportance());
        }
    }

    public Task createTask(TaskRequest request) {
        validateRequest(request);
        Task task = new Task(request.getName(), request.getUrgency(), request.getImportance());
        taskStore.put(task.getId(), task);
        return task;
    }

    public List<Task> getAllTasksSorted() {
        return taskStore.values().stream()
                .filter(task -> !task.isCompleted())
                .sorted(Comparator.comparingInt(Task::getPriorityScore).reversed())
                .toList();
    }

    public Task updateTask(String id, TaskRequest request) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new InvalidTaskInputException("Task not found with id: " + id);
        }
        validateRequest(request);
        task.setName(request.getName());
        task.setUrgency(request.getUrgency());
        task.setImportance(request.getImportance());
        task.recalculateScore();
        return task;
    }

    public void deleteTask(String id) {
        if (!taskStore.containsKey(id)) {
            throw new InvalidTaskInputException("Task not found with id: " + id);
        }
        taskStore.remove(id);
    }

    public Task completeTask(String id) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new InvalidTaskInputException("Task not found with id: " + id);
        }
        task.setCompleted(true);
        return task;
    }
}
