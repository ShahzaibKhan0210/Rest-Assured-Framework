# Rest Assured Framework - Cucumber API Tests

A Java-based API testing framework using **Cucumber**, **Rest Assured**, and **TestNG**. It tests authentication and CRUD operations against the automation-backend API.

## APIs Under Test

| API | Method | Endpoint |
|-----|--------|----------|
| Login | POST | `/api/v1/auth/login` |
| Create Agency | POST | `/api/v1/agencies/add` |
| Get Agency By ID | GET | `/api/v1/agencies/{id}` |

Base URL: `https://automation-backend-ec08fe65847a.herokuapp.com`

## Test Cases

### Case 1 - Invalid Login (`login.feature`)
- POST to Login with invalid credentials (`admin@gmail.com` / `123456`)
- Validates response contains `"message": "Invalid credentials"`

### Case 2 - Create Agency Without Auth (`agencies.feature`)
- POST to Create Agency without an auth token
- Validates response contains `"message": "Please authenticate"`

### Case 3 - Full Flow: Login, Create, GetById (`agency_flow.feature`)
- POST to Login with valid credentials (`admin@gmail.com` / `admin@123`)
- Extracts and stores the access token
- POST to Create Agency with Bearer token in header
- Extracts and stores the agency ID
- GET Agency by ID using the stored ID
- Validates the GetById response matches the Create payload (name, address, phone, email)

## Project Structure

```
src/
  test/
    java/
      runners/
        LoginRunner.java          # TestNG Cucumber runner
      stepdefinitions/
        LoginStepDefinitions.java # Step definitions for all scenarios
    resources/
      features/
        login.feature             # Case 1
        agencies.feature          # Case 2
        agency_flow.feature       # Case 3
pom.xml                           # Maven dependencies
testng.xml                        # TestNG suite config (runs LoginRunner)
```

## Tech Stack

- **Java 11**
- **Rest Assured 5.3.0** - API testing
- **Cucumber 7.14.0** - BDD feature files and step definitions
- **TestNG 7.7.1** - Test runner
- **Hamcrest 2.2** - Assertions
- **SLF4J Simple 2.0.9** - Console logging

## How to Run

### From the terminal

```bash
mvn test
```

Or run a specific runner:

```bash
mvn test -Dtest=LoginRunner
```

### From the IDE

Right-click `LoginRunner.java` and select **Run**.

## Reports

After running, reports are generated at:

| Format | Path |
|--------|------|
| HTML | `target/cucumber-reports/cucumber.html` |
| JSON | `target/cucumber-reports/cucumber.json` |
| JUnit XML | `target/cucumber-reports/cucumber-junit.xml` |

## Logging

SLF4J logging is enabled in step definitions. Each step prints request details, response status, and response body to the console during test execution.
