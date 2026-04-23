Feature: Task Creation
  As a busy professional
  I want to create tasks with urgency and importance ratings
  So that the system can assign a priority score to help me focus

  Scenario: Successfully create a task with valid inputs
    Given I have a task named "Deploy App" with urgency 4 and importance 5
    When I submit the task
    Then the task should be created successfully
    And the priority score should be 20

  Scenario: Priority score is calculated as urgency multiplied by importance
    Given I have a task named "Test Task" with urgency 4 and importance 3
    When I submit the task
    Then the priority score should be 12

  Scenario: Maximum priority score when both values are at upper boundary
    Given I have a task named "Max Task" with urgency 5 and importance 5
    When I submit the task
    Then the priority score should be 25

  Scenario: Minimum priority score when both values are at lower boundary
    Given I have a task named "Min Task" with urgency 1 and importance 1
    When I submit the task
    Then the priority score should be 1

  Scenario: Priority score is the same regardless of which field holds the higher value
    Given I have a task named "High Urgency" with urgency 5 and importance 1
    When I submit the task
    Then the priority score should be 5
    Given I have a task named "High Importance" with urgency 1 and importance 5
    When I submit the task
    Then the priority score should be 5

  Scenario: Each created task receives a unique identifier
    Given I create a task named "Task 1" with urgency 3 and importance 3
    And I create a task named "Task 2" with urgency 3 and importance 3
    Then both tasks should have different IDs

  Scenario Outline: Task creation is rejected when name is invalid
    Given I have a task with name "<name>", urgency 3 and importance 3
    When I submit the task
    Then the creation should fail with error "Task name must not be empty."

    Examples:
      | name   |
      |        |
      |        |

  Scenario Outline: Task creation is rejected when urgency is out of range
    Given I have a task named "Task" with urgency <urgency> and importance 3
    When I submit the task
    Then the creation should fail with error "Urgency must be between 1 and 5, got: <urgency>"

    Examples:
      | urgency |
      | 0       |
      | 6       |

  Scenario Outline: Task creation is rejected when importance is out of range
    Given I have a task named "Task" with urgency 3 and importance <importance>
    When I submit the task
    Then the creation should fail with error "Importance must be between 1 and 5, got: <importance>"

    Examples:
      | importance |
      | 0          |
      | 6          |

  Scenario: Urgency is accepted at its lower boundary value of 1
    Given I have a task named "Task" with urgency 1 and importance 3
    When I submit the task
    Then the priority score should be 3

  Scenario: Urgency is accepted at its upper boundary value of 5
    Given I have a task named "Task" with urgency 5 and importance 3
    When I submit the task
    Then the priority score should be 15

  Scenario: Importance is accepted at its lower boundary value of 1
    Given I have a task named "Task" with urgency 3 and importance 1
    When I submit the task
    Then the priority score should be 3

  Scenario: Importance is accepted at its upper boundary value of 5
    Given I have a task named "Task" with urgency 3 and importance 5
    When I submit the task
    Then the priority score should be 15

