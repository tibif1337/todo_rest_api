Feature: Todo lists

  Background:
    Given I am logged in as "john" with password "password123"

  Scenario: Create a todo list
    When I create a todo list named "School"
    Then the todo list should be created successfully
    And the todo list should have the name "School"

  Scenario: A list is explicitly completed
    Given I have a todo list named "School"
    And the list contains a todo item named "Java assignment"
    When I explicitly complete the list
    Then the list should be completed

  Scenario: A list is completed when all items are completed
    Given I have a todo list named "School"
    And the list contains a todo item named "Java assignment"
    And the list contains a todo item named "Database assignment"
    When I complete all items in the list
    Then the list should be completed