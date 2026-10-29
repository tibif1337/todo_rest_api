Feature: Todo items

  Background:
    Given I am logged in as "john" with password "password123"

  Scenario: Create a todo item
    When I create a todo item named "Learn Java" with deadline "2026-10-10T18:00:00Z"
    Then the todo item should be created successfully
    And the todo item should have the name "Learn Java"

  Scenario: Toggle a todo item
    Given I have a todo item named "Learn Java" with deadline "2026-10-10T18:00:00Z"
    And the todo item is not completed
    When I toggle the todo item
    Then the todo item should be completed

  Scenario: Toggle a todo item twice
    Given I have a todo item named "Learn Java" with deadline "2026-10-10T18:00:00Z"
    When I toggle the todo item
    And I toggle the todo item again
    Then the todo item should not be completed

  Scenario: An item is late after its deadline
    Given I have a todo item named "Late task" with deadline "2025-01-01T12:00:00Z"
    Then the todo item should be marked as late

  Scenario: A completed item can still be late
    Given I have a todo item named "Late task" with deadline "2025-01-01T12:00:00Z"
    When I toggle the todo item
    Then the todo item should be completed
    And the todo item should be marked as late