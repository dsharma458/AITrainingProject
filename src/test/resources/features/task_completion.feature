Feature: Task Completion
  As a busy professional
  I want to mark tasks as completed
  So that they no longer appear in my active priority list

  Scenario: A newly created task defaults to not completed
    Given I have a task named "New Task" with urgency 3 and importance 3
    When I submit the task
    Then the task should have completed set to false

  Scenario: Successfully mark a task as completed
    Given an existing task named "Finish Report" with urgency 4 and importance 5
    When I mark the task as complete
    Then the task should have completed set to true

  Scenario: Completed task is excluded from the sorted task list
    Given the following tasks exist:
      | name      | urgency | importance |
      | Active    | 3       | 3          |
      | Completed | 5       | 5          |
    When I mark "Completed" as complete
    And I retrieve all tasks
    Then the result should contain 1 task
    And the first task should be "Active"

  Scenario: All tasks are excluded when all are completed
    Given the following tasks exist:
      | name   | urgency | importance |
      | Task 1 | 3       | 3          |
      | Task 2 | 4       | 4          |
    When I mark "Task 1" as complete
    And I mark "Task 2" as complete
    And I retrieve all tasks
    Then the result should be an empty list

  Scenario: Completing a task does not affect other active tasks
    Given the following tasks exist:
      | name      | urgency | importance |
      | Active 1  | 5       | 5          |
      | Active 2  | 3       | 3          |
      | Completed | 4       | 4          |
    When I mark "Completed" as complete
    And I retrieve all tasks
    Then the result should contain 2 tasks
    And the tasks should not include "Completed"

  Scenario: Completion fails when the task does not exist
    Given no tasks have been created
    When I mark a non-existent task as complete
    Then the completion should fail with an error containing "Task not found"

