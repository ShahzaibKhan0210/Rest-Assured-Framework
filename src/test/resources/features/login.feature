Feature: Login API

  Scenario: Invalid credentials return expected error message
    Given the login API endpoint is "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1/auth/login"
    When I send a POST request to login with email "admin@gmail.com" and password "123456"
    Then the response status code is 500
    And the response body contains message "Invalid credentials"
