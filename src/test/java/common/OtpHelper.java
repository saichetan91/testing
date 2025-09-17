// src/test/java/common/OtpHelper.java

package common;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class OtpHelper {

    public static Response requestPhoneOtp(String mobileNumber, String countryCode, String deviceId, Map<String, String> headers) {
        String requestBody = String.format("{\"phone\": \"%s\", \"countryCode\": \"%s\"}", mobileNumber, countryCode);
        return RestAssured.given()
                .headers(headers)
                .header("X-Device-Id", deviceId)
                .body(requestBody)
                .post("/v2/auth/phone/otp/request");
    }

    public static Response verifyPhoneOtp(String mobileNumber, String countryCode, String fixedOtp, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"phone\": \"%s\", \"otp\": \"%s\", \"countryCode\": \"%s\"}", mobileNumber, fixedOtp, countryCode);
        return RestAssured.given()
                .headers(headers)
                .header("X-Tap-Session", tapSessionId)
                .header("X-Device-Id", deviceId)
                .header("X-Tap-Dshan", tapDshan)
                .header("_session", sessionCookie)
                .body(requestBody)
                .post("/v2/auth/phone/otp/verify");
    }

    public static Response requestEmailOtp(String email, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"email\": \"%s\"}", email);
        return RestAssured.given()
                .headers(headers)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", authToken)
                .header("X-Tap-Session", tapSessionId)
                .header("X-Device-Id", deviceId)
                .header("X-Tap-Dshan", tapDshan)
                .cookie("_session", sessionCookie)
                .body(requestBody)
                .post("/v2/auth/email/otp/request");
    }

    public static Response verifyEmailOtp(String email, String fixedOtp, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"email\": \"%s\", \"otp\": \"%s\"}", email, fixedOtp);
        return RestAssured.given()
                .headers(headers)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", authToken)
                .header("X-Tap-Session", tapSessionId)
                .header("X-Device-Id", deviceId)
                .header("X-Tap-Dshan", tapDshan)
                .cookie("_session", sessionCookie)
                .body(requestBody)
                .post("/v2/auth/email/otp/verify");
    }

    public static Response verifyAadhaarOtp(String aadhaarNumber, String fixedAadhaarOtp, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"aadhaarNumber\": \"%s\", \"otp\": \"%s\"}", aadhaarNumber, fixedAadhaarOtp);
        return RestAssured.given()
                .headers(headers)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", authToken)
                .header("X-Tap-Session", tapSessionId)
                .header("X-Device-Id", deviceId)
                .header("X-Tap-Dshan", tapDshan)
                .cookie("_session", sessionCookie)
                .body(requestBody)
                .post("/v2/user/aadhaar");
    }
}