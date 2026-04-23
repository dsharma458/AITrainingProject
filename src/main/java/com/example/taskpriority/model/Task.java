package com.example.taskpriority.model;

import java.util.UUID;

public class Task {

    private String id;
    private String name;
    private int urgency;
    private int importance;
    private int priorityScore;
    private boolean completed;

    public Task(String name, int urgency, int importance) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.urgency = urgency;
        this.importance = importance;
        this.priorityScore = urgency * importance;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getUrgency() { return urgency; }
    public int getImportance() { return importance; }
    public int getPriorityScore() { return priorityScore; }
    public boolean isCompleted() { return completed; }

    public void setName(String name) { this.name = name; }
    public void setUrgency(int urgency) { this.urgency = urgency; }
    public void setImportance(int importance) { this.importance = importance; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void recalculateScore() { this.priorityScore = this.urgency * this.importance; }
}
