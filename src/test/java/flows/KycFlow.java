// src/test/java/flows/KycFlow.java

package flows;

import common.HeaderHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import common.ApiBaseTest;
import java.util.Random;
import common.OtpHelper;

public class KycFlow extends ApiBaseTest {

    private final String firstName = "chetan";
    private final String gender = "MALE";
    private final String fixedEmailOtp = "1001";
    private final String panNumber = "DGAPC8318G";
    private final String panName = "KOVURU SAI CHETAN";
    private final String aadhaarNumber = "212711195196";
    private final String fixedAadhaarOtp = "123456";

    private final String bankAccountNumber = generateEvenBankAccountNumber(15);
    private final String ifscCode = "SBIN0001234";
    private final String investingAs = "Individual";
    private final String netWorth = "Below 50 Lakhs";
    private final String investmentPlan = "Less than ₹25L";

    private String generateEvenBankAccountNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // Ensure first digit is non-zero
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < length - 1; i++) {
            sb.append(random.nextInt(10));
        }
        // Last digit must be even
        int[] evenDigits = {0, 2, 4, 6, 8};
        sb.append(evenDigits[random.nextInt(evenDigits.length)]);
        return sb.toString();
    }

    @Test(priority = 1, dependsOnGroups = {"signup"})
    public void testUpdateUserProfile() {
        // Use the dynamic userId from ApiBaseTest
        String updateProfileUrl = "/v2/user/update-profile";
        String requestBody = String.format("{\"id\": %d, \"firstName\": \"%s\", \"gender\": \"%s\"}", ApiBaseTest.userId, firstName, gender);

        Response response = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(updateProfileUrl);

        Assert.assertEquals(response.getStatusCode(), 200, "Profile update failed.");
    }

    @Test(priority = 2, dependsOnMethods = {"testUpdateUserProfile"})
    public void testEmailVerification() {
        // Generate a dynamic email from the mobile number to ensure it's unique
        String testEmail = "testuser" + ApiBaseTest.mobileNumber + "@example.com";
        Response requestOtpResponse = OtpHelper.requestEmailOtp(testEmail, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "Email OTP request failed.");

        Response verifyOtpResponse = OtpHelper.verifyEmailOtp(testEmail, fixedEmailOtp, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "Email OTP verification failed.");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        Assert.assertNotNull(ApiBaseTest.authToken, "New auth token not found.");
    }

    @Test(priority = 3, dependsOnMethods = {"testEmailVerification"})
    public void testPanVerification() {
        String verifyPanUrl = "/v2/user/pan/verify";
        String requestBody = String.format("{\"panNumber\": \"%s\"}", panNumber);
        Response response = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(verifyPanUrl);

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "PAN verification failed.");

        String submitPanUrl = "/v2/user/pan";
        String submitBody = String.format("{\"panCardNumber\": \"%s\", \"name\": \"%s\"}", panNumber, panName);

        Response submitResponse = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(submitBody)
                .log().all()
                .post(submitPanUrl);

        Assert.assertEquals(submitResponse.getStatusCode(), 200, "PAN submission failed.");
    }

    @Test(priority = 4, dependsOnMethods = {"testPanVerification"})
    public void testAadhaarVerification() {
        String requestAadhaarOtpUrl = "/v2/user/aadhaar/verify";
        String requestBody = String.format("{\"aadhaarNumber\": \"%s\"}", aadhaarNumber);

        Response response = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(requestBody)
                .log().all()
                .post(requestAadhaarOtpUrl);

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Aadhaar OTP request failed.");

        Response verifyAadhaarResponse = OtpHelper.verifyAadhaarOtp(aadhaarNumber, fixedAadhaarOtp, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyAadhaarResponse.getStatusCode(), 200, "Aadhaar submission failed.");
    }

    @Test(priority = 5, dependsOnMethods = {"testAadhaarVerification"})
    public void testBankAndAccreditation() {
        // Step 1: Submit Bank Details
        String bankDetailsUrl = "/v2/user/bank-details";

        String bankRequestBody = String.format("{\"accountNumber\": \"%s\", \"confirmAccountNumber\": \"%s\", \"ifscCode\": \"%s\"}",
                bankAccountNumber, bankAccountNumber, ifscCode);

        Response bankResponse = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(bankRequestBody)
                .log().all()
                .post(bankDetailsUrl);

        bankResponse.then().log().all();
        Assert.assertEquals(bankResponse.getStatusCode(), 200, "Bank details submission failed.");

        // Step 2: Get Accreditation Info (GET request)
        String getAccreditationUrl = "/v2/user/get-accreditation";

        Response getAccreditationResponse = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_READ")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .log().all()
                .get(getAccreditationUrl);

        getAccreditationResponse.then().log().all();
        if (getAccreditationResponse.getStatusCode() != 200) {
            System.out.println("Get Accreditation non-200 status: " + getAccreditationResponse.getStatusCode());
        }

        // Step 3: Submit Accreditation
        String accreditationUrl = "/v2/user/accreditation";
        String accreditationBody = String.format("{\"investingAs\": \"%s\", \"netWorth\": \"%s\", \"investmentPlan\": \"%s\"}",
                investingAs, netWorth, investmentPlan);

        Response accreditationResponse = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(accreditationBody)
                .log().all()
                .post(accreditationUrl);

        if (accreditationResponse.getStatusCode() != 200) {
            System.out.println("Accreditation non-200 status: " + accreditationResponse.getStatusCode() + "; continuing with KYC flow");
        }

        // Step 4: Compliance
        String complianceUrl = "/v2/user/compliance";
        String complianceBody = "{\"subjectToRegulatoryActions\": false, \"rbiCompliance\": true, \"wilfulDefaulter\": false, \"termsAcknowledged\": true}";

        Response complianceResponse = RestAssured.given()
                .headers(commonHeaders)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .body(complianceBody)
                .log().all()
                .post(complianceUrl);

        if (complianceResponse.getStatusCode() != 200) {
            System.out.println("Compliance non-200 status: " + complianceResponse.getStatusCode());
        }
    }
}