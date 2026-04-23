Feature: Task Sorting
  As a busy professional
  I want my tasks sorted by priority score
  So that I can focus on the most critical items first

  Scenario: Tasks are returned sorted by priority score descending
    Given the following tasks exist:
      | name          | urgency | importance |
      | Low Priority  | 1       | 2          |
      | High Priority | 5       | 5          |
      | Mid Priority  | 3       | 3          |
    When I retrieve all tasks
    Then the tasks should be returned in the following order:
      | name          | priorityScore |
      | High Priority | 25            |
      | Mid Priority  | 9             |
      | Low Priority  | 2             |

  Scenario: An empty task list is returned when no tasks exist
    Given no tasks have been created
    When I retrieve all tasks
    Then the result should be an empty list

  Scenario: A single task is returned as a one-item sorted list
    Given I have a task named "Only Task" with urgency 3 and importance 4
    When I submit the task
    And I retrieve all tasks
    Then the result should contain 1 task
    And the first task should be "Only Task"

