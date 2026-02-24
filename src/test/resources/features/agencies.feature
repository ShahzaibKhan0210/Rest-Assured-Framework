Feature: Create Agency API

  Scenario: Create agency without auth returns please authenticate message
    Given the create agency API endpoint is "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1/agencies/add"
    When I send a POST request to create agency with name "Test User" address "some value" phone "1234567890" email "test@example.com"
    Then the response status code is 500
    And the response body contains message "Please authenticate"
