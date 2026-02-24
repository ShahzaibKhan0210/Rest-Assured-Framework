package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.equalTo;

public class LoginStepDefinitions {

    private static final Logger log = LoggerFactory.getLogger(LoginStepDefinitions.class);

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
        log.info("Login API endpoint set: {}", endpoint);
    }

    @When("I send a POST request to login with email {string} and password {string}")
    public void i_send_a_post_request_to_login_with_email_and_password(String email, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );
        log.info("POST Login -> {} | body: {}", loginEndpoint, body);
        response = request.body(body).when().post(loginEndpoint);
        log.info("Login response status: {} | body: {}", response.getStatusCode(), response.body().asString());
    }

    @Given("the create agency API endpoint is {string}")
    public void the_create_agency_api_endpoint_is(String endpoint) {
        this.loginEndpoint = endpoint;
        this.request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        log.info("Create agency API endpoint set: {}", endpoint);
    }

    @When("I send a POST request to create agency with name {string} address {string} phone {string} email {string}")
    public void i_send_a_post_request_to_create_agency_with_name_address_phone_email(String name, String address, String phone, String email) {
        String body = String.format(
                "{\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\"}",
                name, address, phone, email
        );
        log.info("POST Create agency (no auth) -> {} | body: {}", loginEndpoint, body);
        response = request.body(body).when().post(loginEndpoint);
        log.info("Create agency response status: {} | body: {}", response.getStatusCode(), response.body().asString());
    }

    @Then("the response status code is {int}")
    public void the_response_status_code_is(int expectedStatus) {
        int actual = response.getStatusCode();
        log.info("Assert response status: expected={}, actual={}", expectedStatus, actual);
        response.then().statusCode(expectedStatus);
    }

    @Then("the response body contains message {string}")
    public void the_response_body_contains_message(String expectedMessage) {
        log.info("Assert response message: expected='{}'", expectedMessage);
        response.then().body("message", equalTo(expectedMessage));
    }

    @Then("I extract the access token from the login response and store it")
    public void i_extract_the_access_token_from_the_login_response_and_store_it() {
        accessToken = response.body().path("tokens.access.token");
        log.info("Extracted and stored access token (length={})", accessToken != null ? accessToken.length() : 0);
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
        log.info("POST Create agency (with Bearer) -> {} | body: {}", loginEndpoint, body);
        request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken);
        response = request.body(body).when().post(loginEndpoint);
        log.info("Create agency response status: {} | body: {}", response.getStatusCode(), response.body().asString());
    }

    @Then("I extract the agency id from the create response and store it")
    public void i_extract_the_agency_id_from_the_create_response_and_store_it() {
        agencyId = response.body().path("agency.id").toString();
        log.info("Extracted and stored agency id: {}", agencyId);
    }

    @Given("the get agency by id API endpoint is {string}")
    public void the_get_agency_by_id_api_endpoint_is(String baseUrl) {
        this.loginEndpoint = baseUrl + "/" + agencyId;
        log.info("Get agency by id endpoint set: {}", loginEndpoint);
    }

    @When("I send a GET request to get agency by id with Bearer token")
    public void i_send_a_get_request_to_get_agency_by_id_with_bearer_token() {
        log.info("GET agency by id -> {}", loginEndpoint);
        request = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken);
        response = request.when().get(loginEndpoint);
        log.info("Get agency response status: {} | body: {}", response.getStatusCode(), response.body().asString());
    }

    @Then("the get agency response matches the create payload name {string} address {string} phone {string} email {string}")
    public void the_get_agency_response_matches_the_create_payload(String name, String address, String phone, String email) {
        String expectedName = createName != null ? createName : name;
        String expectedAddress = createAddress != null ? createAddress : address;
        String expectedPhone = createPhone != null ? createPhone : phone;
        String expectedEmail = createEmail != null ? createEmail : email;
        String[] namePaths = {"agency.name", "data.agency.name", "data.name", "name"};
        String[] addressPaths = {"agency.address", "data.agency.address", "data.address", "address"};
        String[] phonePaths = {"agency.phone", "data.agency.phone", "data.phone", "phone"};
        String[] emailPaths = {"agency.email", "data.agency.email", "data.email", "email"};
        String actualName = firstNonNullPath(namePaths);
        String actualAddress = firstNonNullPath(addressPaths);
        String actualPhone = firstNonNullPath(phonePaths);
        String actualEmail = firstNonNullPath(emailPaths);
        assert actualName != null : "Could not find name in response. Body: " + response.body().asString();
        assert actualAddress != null : "Could not find address in response.";
        assert actualPhone != null : "Could not find phone in response.";
        assert actualEmail != null : "Could not find email in response.";
        assert expectedName.equals(actualName) : "name: expected " + expectedName + ", actual " + actualName;
        assert expectedAddress.equals(actualAddress) : "address: expected " + expectedAddress + ", actual " + actualAddress;
        assert expectedPhone.equals(actualPhone) : "phone: expected " + expectedPhone + ", actual " + actualPhone;
        assert expectedEmail.equals(actualEmail) : "email: expected " + expectedEmail + ", actual " + actualEmail;
        log.info("GetById response matches create payload: name={}, address={}, phone={}, email={}", actualName, actualAddress, actualPhone, actualEmail);
    }

    private String firstNonNullPath(String[] paths) {
        for (String path : paths) {
            Object val = response.body().path(path);
            if (val != null) return val.toString();
        }
        return null;
    }
}
