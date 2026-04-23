package com.example.taskpriority.dto;

public class TaskRequest {
    private String name;
    private int urgency;
    private int importance;

    public String getName() { return name; }
    public int getUrgency() { return urgency; }
    public int getImportance() { return importance; }

    public void setName(String name) { this.name = name; }
    public void setUrgency(int urgency) { this.urgency = urgency; }
    public void setImportance(int importance) { this.importance = importance; }
}

