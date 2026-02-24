Feature: Login, Create Agency, and GetById flow

  Scenario: Login, create agency with token, get by id and validate response matches create
    Given the login API endpoint is "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1/auth/login"
    When I send a POST request to login with email "admin@gmail.com" and password "admin@123"
    Then the response status code is 200
    And I extract the access token from the login response and store it
    Given the create agency API endpoint is "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1/agencies/add"
    When I send a POST request to create agency with Bearer token with name "Test User" address "some value" phone "1234567890" email "test@example.com"
    Then the response status code is 200
    And I extract the agency id from the create response and store it
    Given the get agency by id API endpoint is "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1/agencies"
    When I send a GET request to get agency by id with Bearer token
    Then the response status code is 200
    And the get agency response matches the create payload name "Test User" address "some value" phone "1234567890" email "test@example.com"
