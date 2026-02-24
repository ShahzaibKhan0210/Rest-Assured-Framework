package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.Matchers.equalTo;

public class LoginStepDefinitions {

    private String loginEndpoint;
    private RequestSpecification request;
    private Response response;

    @Given("the login API endpoint is {string}")
    public void the_login_api_endpoint_is(String endpoint) {
        this.loginEndpoint = endpoint;
        this.request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    @When("I send a POST request to login with email {string} and password {string}")
    public void i_send_a_post_request_to_login_with_email_and_password(String email, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );
        response = request.body(body).when().post(loginEndpoint);
    }

    @Given("the create agency API endpoint is {string}")
    public void the_create_agency_api_endpoint_is(String endpoint) {
        this.loginEndpoint = endpoint;
        this.request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    @When("I send a POST request to create agency with name {string} address {string} phone {string} email {string}")
    public void i_send_a_post_request_to_create_agency_with_name_address_phone_email(String name, String address, String phone, String email) {
        String body = String.format(
                "{\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\"}",
                name, address, phone, email
        );
        response = request.body(body).when().post(loginEndpoint);
    }

    @Then("the response status code is {int}")
    public void the_response_status_code_is(int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Then("the response body contains message {string}")
    public void the_response_body_contains_message(String expectedMessage) {
        response.then().body("message", equalTo(expectedMessage));
    }
}
