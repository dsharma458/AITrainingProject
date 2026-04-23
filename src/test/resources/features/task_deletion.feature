Feature: Task Deletion
  As a busy professional
  I want to delete tasks that are no longer relevant
  So that my task list stays clean and accurate

  Scenario: Successfully delete an existing task
    Given an existing task named "To Delete" with urgency 3 and importance 3
    When I delete the task
    Then the task list should be empty

  Scenario: Task count decreases after deletion
    Given the following tasks exist:
      | name   | urgency | importance |
      | Task A | 2       | 2          |
      | Task B | 3       | 3          |
    When I delete "Task B"
    Then the task list should contain 1 task

  Scenario: Remaining tasks are not affected after deletion
    Given the following tasks exist:
      | name   | urgency | importance |
      | Task A | 4       | 4          |
      | Task B | 2       | 2          |
    When I delete "Task B"
    Then the task list should contain 1 task
    And the remaining task should be "Task A"

  Scenario: Deletion fails when the task does not exist
    Given no tasks have been created
    When I delete a task with a non-existent ID
    Then the deletion should fail with an error containing "Task not found"

