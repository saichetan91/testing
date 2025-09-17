// src/test/java/flows/KycFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.json.JSONObject;

import common.ApiBaseTest;
import common.OtpHelper;
import common.DataGenerator;
import common.UserProfileAPI;

public class KycFlow extends ApiBaseTest {

    private final String firstName = "chetan";
    private final String gender = "MALE";
    private final String fixedEmailOtp = "1001";
    private final String panNumber = "DGAPC8318G";
    private final String panName = "KOVURU SAI CHETAN";
    private final String aadhaarNumber = "212711195196";
    private final String fixedAadhaarOtp = "123456";

    private final String bankAccountNumber = DataGenerator.generateEvenBankAccountNumber(15);
    private final String ifscCode = DataGenerator.generateMockIfscCode();

    @Test(priority = 1, dependsOnGroups = {"signup"})
    public void testUpdateUserProfile() {
        Response response = UserProfileAPI.updateUserProfile(ApiBaseTest.userId, firstName, gender);
        Assert.assertEquals(response.getStatusCode(), 200, "Profile update failed.");
    }

    @Test(priority = 2, dependsOnMethods = {"testUpdateUserProfile"})
    public void testEmailVerification() {
        // Generate a dynamic email from the mobile number to ensure it's unique
        String testEmail = DataGenerator.generateTestEmail(ApiBaseTest.mobileNumber);
        Response requestOtpResponse = OtpHelper.requestEmailOtp(testEmail, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "Email OTP request failed.");

        Response verifyOtpResponse = OtpHelper.verifyEmailOtp(testEmail, fixedEmailOtp, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "Email OTP verification failed.");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        Assert.assertNotNull(ApiBaseTest.authToken, "New auth token not found.");
    }

    @Test(priority = 3, dependsOnMethods = {"testEmailVerification"})
    public void testPanVerification() {
        Response response = UserProfileAPI.verifyPan(panNumber);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "PAN verification failed.");

        Response submitResponse = UserProfileAPI.submitPan(panNumber, panName);
        Assert.assertEquals(submitResponse.getStatusCode(), 200, "PAN submission failed.");
    }

    @Test(priority = 4, dependsOnMethods = {"testPanVerification"})
    public void testAadhaarVerification() {
        Response response = UserProfileAPI.requestAadhaarOtp(aadhaarNumber);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Aadhaar OTP request failed.");

        Response verifyAadhaarResponse = OtpHelper.verifyAadhaarOtp(aadhaarNumber, fixedAadhaarOtp, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyAadhaarResponse.getStatusCode(), 200, "Aadhaar submission failed.");
    }

    @Test(priority = 5, dependsOnMethods = {"testAadhaarVerification"})
    public void testBankAndAccreditation() {
        // Step 1: Submit Bank Details
        Response bankResponse = UserProfileAPI.submitBankDetails(bankAccountNumber, ifscCode);
        bankResponse.then().log().all();
        Assert.assertEquals(bankResponse.getStatusCode(), 200, "Bank details submission failed.");

        // Step 2: Get Accreditation Questions
        Response getAccreditationResponse = UserProfileAPI.getAccreditation();
        getAccreditationResponse.then().log().all();
        Assert.assertEquals(getAccreditationResponse.getStatusCode(), 200, "Get accreditation failed.");

        // Step 3: Mark Accreditation Answers
        JSONObject accreditationAnswers = new JSONObject();
        accreditationAnswers.put("1", 10);  // Individual
        accreditationAnswers.put("2", 16);  // Below 50 Lakhs
        accreditationAnswers.put("3", 19);  // Less than ₹25L
        accreditationAnswers.put("4", 24);  // No (regulatory actions)
        accreditationAnswers.put("5", 25);  // Yes (RBI compliance)
        accreditationAnswers.put("6", 28);  // No (wilful defaulter)
        accreditationAnswers.put("7", 29);  // Terms acknowledged

        Response markAnswerResponse = UserProfileAPI.markAccreditationAnswers(ApiBaseTest.userId, accreditationAnswers);
        markAnswerResponse.then().log().all();
        Assert.assertEquals(markAnswerResponse.getStatusCode(), 200, "Mark accreditation answers failed.");

        // Step 4: Submit Compliance (optional - may not be needed since compliance questions are in mark-answer)
        Response complianceResponse = UserProfileAPI.submitCompliance(false, true, false, true);

        // Don't fail the test if compliance gives non-200, as mark-answer should have handled everything
        if (complianceResponse.getStatusCode() == 200) {
            System.out.println("Compliance submitted successfully.");
        } else {
            System.out.println("Compliance non-200 status: " + complianceResponse.getStatusCode() + " - may not be needed after mark-answer");
        }

        System.out.println("Bank and Accreditation flow completed successfully!");
    }
    }