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
    private String accessToken;
    private String agencyId;
    private String createName;
    private String createAddress;
    private String createPhone;
    private String createEmail;

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

    @Then("I extract the access token from the login response and store it")
    public void i_extract_the_access_token_from_the_login_response_and_store_it() {
        accessToken = response.body().path("tokens.access.token");
    }

    @When("I send a POST request to create agency with Bearer token with name {string} address {string} phone {string} email {string}")
    public void i_send_a_post_request_to_create_agency_with_bearer_token(String name, String address, String phone, String email) {
        createName = name;
        createAddress = address;
        createPhone = phone;
        createEmail = "test+" + System.currentTimeMillis() + "@example.com";
        String body = String.format(
                "{\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\"}",
                createName, createAddress, createPhone, createEmail
        );
        request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken);
        response = request.body(body).when().post(loginEndpoint);
    }

    @Then("I extract the agency id from the create response and store it")
    public void i_extract_the_agency_id_from_the_create_response_and_store_it() {
        agencyId = response.body().path("agency.id").toString();
    }

    @Given("the get agency by id API endpoint is {string}")
    public void the_get_agency_by_id_api_endpoint_is(String baseUrl) {
        this.loginEndpoint = baseUrl + "/" + agencyId;
    }

    @When("I send a GET request to get agency by id with Bearer token")
    public void i_send_a_get_request_to_get_agency_by_id_with_bearer_token() {
        request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken);
        response = request.when().get(loginEndpoint);
    }

    @Then("the get agency response matches the create payload name {string} address {string} phone {string} email {string}")
    public void the_get_agency_response_matches_the_create_payload(String name, String address, String phone, String email) {
        String expectedName = createName != null ? createName : name;
        String expectedAddress = createAddress != null ? createAddress : address;
        String expectedPhone = createPhone != null ? createPhone : phone;
        String expectedEmail = createEmail != null ? createEmail : email;
        Object agency = response.body().path("agency");
        String namePath = agency != null ? "agency.name" : "name";
        String addressPath = agency != null ? "agency.address" : "address";
        String phonePath = agency != null ? "agency.phone" : "phone";
        String emailPath = agency != null ? "agency.email" : "email";
        response.then()
                .body(namePath, equalTo(expectedName))
                .body(addressPath, equalTo(expectedAddress))
                .body(phonePath, equalTo(expectedPhone))
                .body(emailPath, equalTo(expectedEmail));
    }
}
