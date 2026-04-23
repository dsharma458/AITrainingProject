package com.example.taskpriority.controller;

import com.example.taskpriority.dto.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(String name, int urgency, int importance) throws Exception {
        TaskRequest req = new TaskRequest();
        req.setName(name);
        req.setUrgency(urgency);
        req.setImportance(importance);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void shouldCreateTaskAndReturn201() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("Deploy App", 4, 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priorityScore").value(20))
                .andExpect(jsonPath("$.name").value("Deploy App"));
    }

    @Test
    void shouldReturnTasksSortedByPriorityScoreDescending() throws Exception {
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(toJson("Low", 1, 1)));
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(toJson("High", 5, 5)));
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(toJson("Mid", 3, 3)));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("High"))
                .andExpect(jsonPath("$[1].name").value("Mid"))
                .andExpect(jsonPath("$[2].name").value("Low"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("  ", 3, 3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Task name must not be empty."));
    }

    @Test
    void shouldReturn400WhenUrgencyOutOfRange() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("Task", 7, 3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Urgency must be between 1 and 5, got: 7"));
    }

    @Test
    void shouldReturn400WhenImportanceOutOfRange() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("Task", 3, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Importance must be between 1 and 5, got: 0"));
    }

    @Test
    void shouldUpdateTaskAndRecalculateScore() throws Exception {
        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("Original", 2, 2)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(put("/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("Updated", 5, 5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityScore").value(25))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteTaskAndReturn204() throws Exception {
        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("To Delete", 3, 3)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks"))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

