package common;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HeaderHelper {

    /**
     * Returns a RequestSpecification with all common headers applied using dynamic session data
     */
    public static RequestSpecification getRequestWithHeaders() {
        RequestSpecification spec = given()
                .headers(ApiBaseTest.commonHeaders);

        // Add authentication headers if available
        if (ApiBaseTest.authToken != null) {
            spec = spec.header("X-Tap-Auth", ApiBaseTest.authToken);
        }
        if (ApiBaseTest.tapSessionId != null) {
            spec = spec.header("X-Tap-Session", ApiBaseTest.tapSessionId);
        }
        if (ApiBaseTest.deviceId != null) {
            spec = spec.header("X-Device-Id", ApiBaseTest.deviceId);
        }
        if (ApiBaseTest.tapDshan != null) {
            spec = spec.header("X-Tap-Dshan", ApiBaseTest.tapDshan);
        }
        if (ApiBaseTest.sessionCookie != null) {
            spec = spec.cookie("_session", ApiBaseTest.sessionCookie);
        }

        return spec;
    }

    /**
     * Returns a RequestSpecification with common headers and specific platform request type
     */
    public static RequestSpecification getRequestWithHeaders(String platformRequest) {
        return getRequestWithHeaders()
                .header("X-Platform-Request", platformRequest);
    }

    /**
     * Returns a RequestSpecification for authenticated requests (WEB_WRITE)
     */
    public static RequestSpecification getAuthenticatedRequest() {
        return getRequestWithHeaders("WEB_WRITE");
    }

    /**
     * Returns a RequestSpecification for read-only requests (WEB_READ)
     */
    public static RequestSpecification getReadOnlyRequest() {
        return getRequestWithHeaders("WEB_READ");
    }
}