package common;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

/**
 * Common API helper for user profile related operations
 */
public class UserProfileAPI {

    /**
     * Update user profile with first name and gender
     */
    public static Response updateUserProfile(int userId, String firstName, String gender) {
        String updateProfileUrl = "/v2/user/update-profile";
        String requestBody = String.format("{\"id\": %d, \"firstName\": \"%s\", \"gender\": \"%s\"}", 
                userId, firstName, gender);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(updateProfileUrl);
    }

    /**
     * Verify PAN number
     */
    public static Response verifyPan(String panNumber) {
        String verifyPanUrl = "/v2/user/pan/verify";
        String requestBody = String.format("{\"panNumber\": \"%s\"}", panNumber);
        
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(verifyPanUrl);
    }

    /**
     * Submit PAN details
     */
    public static Response submitPan(String panNumber, String panName) {
        String submitPanUrl = "/v2/user/pan";
        String submitBody = String.format("{\"panCardNumber\": \"%s\", \"name\": \"%s\"}", 
                panNumber, panName);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(submitBody)
                .log().all()
                .post(submitPanUrl);
    }

    /**
     * Request Aadhaar OTP
     */
    public static Response requestAadhaarOtp(String aadhaarNumber) {
        String requestAadhaarOtpUrl = "/v2/user/aadhaar/verify";
        String requestBody = String.format("{\"aadhaarNumber\": \"%s\"}", aadhaarNumber);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(requestAadhaarOtpUrl);
    }

    /**
     * Submit bank details
     */
    public static Response submitBankDetails(String accountNumber, String ifscCode) {
        String bankDetailsUrl = "/v2/user/bank-details";
        String bankRequestBody = String.format("{\"accountNumber\": \"%s\", \"confirmAccountNumber\": \"%s\", \"ifscCode\": \"%s\"}",
                accountNumber, accountNumber, ifscCode);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(bankRequestBody)
                .log().all()
                .post(bankDetailsUrl);
    }

    /**
     * Get accreditation questions
     */
    public static Response getAccreditation() {
        String getAccreditationUrl = "/v2/user/get-accreditation";

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_READ")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .log().all()
                .get(getAccreditationUrl);
    }

    /**
     * Mark accreditation answers
     */
    public static Response markAccreditationAnswers(int userId, JSONObject accreditationAnswers) {
        String markAnswerUrl = "/v2/user/accreditation/mark-answer";
        
        JSONObject markAnswerBody = new JSONObject();
        markAnswerBody.put("userId", userId);
        markAnswerBody.put("accreditationAnswers", accreditationAnswers);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(markAnswerBody.toString())
                .log().all()
                .post(markAnswerUrl);
    }

    /**
     * Submit compliance
     */
    public static Response submitCompliance(boolean subjectToRegulatoryActions, boolean rbiCompliance, 
                                          boolean wilfulDefaulter, boolean termsAcknowledged) {
        String complianceUrl = "/v2/user/compliance";
        
        JSONObject complianceBody = new JSONObject();
        complianceBody.put("subjectToRegulatoryActions", subjectToRegulatoryActions);
        complianceBody.put("rbiCompliance", rbiCompliance);
        complianceBody.put("wilfulDefaulter", wilfulDefaulter);
        complianceBody.put("termsAcknowledged", termsAcknowledged);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(complianceBody.toString())
                .log().all()
                .post(complianceUrl);
    }
}
