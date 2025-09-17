package common;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HeaderHelper {

    // Returns a RequestSpecification with all common headers applied
    public static RequestSpecification getRequestWithHeaders() {
        return given()
                .header("x-app-id", "ULTRA")
                .header("x-client-type", "Web Investor App")
                .header("x-device-id", "bd47c5d5-e498-46e6-8cab-4b1c7daa47f7")
                .header("x-device-model", "Macintosh")
                .header("x-device-type", "Desktop")
                .header("x-os-type", "Mac OS")
                .header("x-os-version", "10.15.7")
                .header("x-platform-request", "WEB_READ")
                .header("x-tap-auth", "c_Jj4pwhROWepUbBnidwK")
                .header("x-tap-session", "cbdaa2f7-487d-4d4e-ba82-4e2683a20b6a");
    }

}