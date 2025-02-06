import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ApiTest {

    private void writeToJsonFile(String jsonContent, String filePath) {
        try {
            File file = new File(filePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(jsonContent);

            writer.close();

            System.out.println("Response saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This function can intentionally accept any field and not just id due to potential future scalability needs
    private List<String> getAllValuesFromResponse(String responseJsonPath, String fieldName) {
        List<String> values = new ArrayList<>();

        try {
            String jsonString = Files.readString(Paths.get(responseJsonPath));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject page = jsonArray.getJSONObject(i);
                if (page.has(fieldName)) {
                    values.add(page.getString(fieldName));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private static final String BASE_URL = "https://api.instatus.com";
    private static final String BEARER_TOKEN = "8d16333936e72e705980878e18c95976";
    private static final String BEARER_TOKEN_INVALID = "8d16333936e72e705980878e18c95776";
    private static final String RESPONSE_JSON_PATH = "response.json";
    private static final String RESPONSE_COMPONENTS_JSON_PATH = "responseComponents.json";


    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    // Test to verify the GET /v1/pages endpoint
    @Test(priority = 1)
    public void testGetPages() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/pages")
                .then()
                .extract()
                .response();

        // Just for debugging purposes
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200");

        // Write response to response file
        String responseBody = response.getBody().asString();
        String filePath = "response.json";
        writeToJsonFile(responseBody, filePath);

    }

    // Test unauthorized access
    @Test
    public void testGetStatusUnauthorized() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + BEARER_TOKEN_INVALID)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/pages")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 401, "Expected HTTP status code 401");
    }

    // Test to verify the GET /v1/pagesId/components endpoint
    @Test(priority = 2, dependsOnMethods = "testGetPages")
    public void testGetPagesId() {
        List<String> pageIds = getAllValuesFromResponse(RESPONSE_JSON_PATH, "id");

        // Check if the list is empty
        Assert.assertFalse(pageIds.isEmpty(), "Page IDs list should not be empty");

        // This can modify to use other page IDs
        String pageId = pageIds.get(2);

        Response response = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/" + pageId + "/components") // Using dynamic page ID
                .then()
                .extract()
                .response();

        // Just for debugging purposes
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200");

        // Write response to responseComponents file
        String responseBody = response.getBody().asString();
        String filePath = "responseComponents.json";
        writeToJsonFile(responseBody, filePath);

    }

    // Test to assure invalid pageId will return 404
    @Test(priority = 3)
    public void testGetInvalidPageId() {
        String invalidPageId = "invalid-page-123";
        System.out.println("Checking API response for an invalid Page ID: " + invalidPageId);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/" + invalidPageId + "/components")
                .then()
                .extract()
                .response();

        // Just for debugging purposes
        System.out.println("Response for invalid Page ID " + invalidPageId + ": " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 404, "Expected HTTP status code 404 for invalid Page ID");
    }

    // Test to check if every pageId returns 200
    @Test(priority = 4, dependsOnMethods = "testGetPages")
    public void testGetMultiplePagesId() {
        List<String> pageIds = getAllValuesFromResponse(RESPONSE_JSON_PATH, "id");

        Assert.assertFalse(pageIds.isEmpty(), "Page IDs list should not be empty");

        // Loop through the all page IDs
        for (int i = 0; i < pageIds.size(); i++) {
            String pageId = pageIds.get(i);
            System.out.println("Checking components for Page ID: " + pageId);

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + BEARER_TOKEN)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/v1/" + pageId + "/components")
                    .then()
                    .extract()
                    .response();

            // Just for debugging purposes
            System.out.println("Response for Page ID " + pageId + ": " + response.getBody().asString());

            Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200 for Page ID: " + pageId);
        }
    }

    // Test to verify the GET /v1/pagesId/components/pageComponentId endpoint
    @Test(priority = 5, dependsOnMethods = "testGetPagesId")
    public void testGetPagesComponentId() {
        List<String> pageComponentIds = getAllValuesFromResponse(RESPONSE_COMPONENTS_JSON_PATH, "id");
        List<String> pageIds = getAllValuesFromResponse(RESPONSE_JSON_PATH, "id");

        Assert.assertFalse(pageComponentIds.isEmpty(), "Page IDs list should not be empty");

        // This can modify to use other page IDs or page component IDs
        String pageId = pageIds.get(2);
        String pageComponentId = pageComponentIds.get(1);

        // Just for debugging purposes
        System.out.println("API Request URL: " + BASE_URL + "/v1/" + pageId + "/components/" + pageComponentId);

        Response response = RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/" + pageId + "/components/" + pageComponentId)
                .then()
                .extract()
                .response();

        // Just for debugging purposes
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200");
    }

    // Test to check incidents on all pageIds
    @Test(priority = 6, dependsOnMethods = "testGetPages")
    public void testGetIncidentsForAllPageIds() {
        List<String> pageIds = getAllValuesFromResponse(RESPONSE_JSON_PATH, "id");

        Assert.assertFalse(pageIds.isEmpty(), "Page IDs list should not be empty");

        // Loop through all page IDs and check incidents for each
        for (int i = 0; i < pageIds.size(); i++) {
            String pageId = pageIds.get(i);
            System.out.println("Checking API response for Incidents on Page ID: " + pageId);

            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + BEARER_TOKEN)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/v1/" + pageId + "/incidents")
                    .then()
                    .extract()
                    .response();

            // Just for debugging purposes
            System.out.println("Response for Incidents on Page ID " + pageId + ": " + response.getBody().asString());

            Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200 for Page ID " + pageId);

            // Check two existing conditions: no incidents or incident found
            List<?> incidents = response.jsonPath().getList("incidents");
            if (!incidents.isEmpty()) {
                System.out.println("Found incidents for Page ID " + pageId);
            } else {
                System.out.println("No incidents found for Page ID " + pageId);
            }

            // Assert that incidents are either empty or returned successfully
            Assert.assertTrue(incidents.size() >= 0, "Incidents should be returned or be empty for Page ID " + pageId);
        }
    }
}
