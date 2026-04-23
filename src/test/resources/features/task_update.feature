Feature: Task Update
  As a busy professional
  I want to update my tasks' urgency and importance ratings
  So that the priority score stays accurate as priorities change

  Scenario: Successfully update a task with valid inputs
    Given an existing task named "Original" with urgency 2 and importance 2
    When I update the task with name "Updated Task", urgency 5 and importance 4
    Then the task should be updated successfully
    And the priority score should be 20

  Scenario: Task name is updated correctly
    Given an existing task named "Old Name" with urgency 2 and importance 2
    When I update the task with name "New Name", urgency 2 and importance 2
    Then the task name should be "New Name"

  Scenario: Task ID is preserved after update
    Given an existing task named "Original" with urgency 2 and importance 2
    When I update the task with name "Renamed", urgency 4 and importance 4
    Then the task ID should remain unchanged

  Scenario: Priority score is recalculated after update
    Given an existing task named "Task" with urgency 2 and importance 2
    When I update the task with name "Task", urgency 5 and importance 5
    Then the priority score should be 25

  Scenario: Update fails when the task does not exist
    Given no tasks have been created
    When I update a task with a non-existent ID
    Then the update should fail with an error containing "Task not found"

  Scenario: Update is rejected when the new name is blank
    Given an existing task named "Task" with urgency 3 and importance 3
    When I update the task with name "", urgency 3 and importance 3
    Then the update should fail with error "Task name must not be empty."

  Scenario Outline: Update is rejected when urgency is out of range
    Given an existing task named "Task" with urgency 3 and importance 3
    When I update the task with name "Task", urgency <urgency> and importance 3
    Then the update should fail with error "Urgency must be between 1 and 5, got: <urgency>"

    Examples:
      | urgency |
      | 0       |
      | 6       |

  Scenario Outline: Update is rejected when importance is out of range
    Given an existing task named "Task" with urgency 3 and importance 3
    When I update the task with name "Task", urgency 3 and importance <importance>
    Then the update should fail with error "Importance must be between 1 and 5, got: <importance>"

    Examples:
      | importance |
      | 0          |
      | 6          |

