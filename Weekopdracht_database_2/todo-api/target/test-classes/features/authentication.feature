Feature: Authentication

  Scenario: A user can register and log in
    Given I am logged in as "john" with password "password123"

  Scenario: Login fails with an incorrect password
    Given I am logged in as "john" with password "password123"
    When I try to log in as "john" with password "wrong-password"
    Then the login should fail