Feature: Items can appear in multiple todo lists

  Background:
    Given I am logged in as "john" with password "password123"

  Scenario: An item can be added to multiple lists
    Given I have a todo list named "School"
    And I have a todo list named "Personal"
    And I have a todo item named "Finish Java assignment" with deadline "2026-10-10T23:59:00Z"
    When I add the todo item to the "School" list
    And I add the todo item to the "Personal" list
    Then the todo item should appear in the "School" list
    And the todo item should appear in the "Personal" list