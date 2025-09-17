// src/test/java/flows/SignupFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;


import common.ApiBaseTest;
import common.OtpHelper;
import common.DataGenerator;

public class SignupFlow extends ApiBaseTest {

    private final String countryCode = "+91";
    private final String fixedOtp = "1001";
    private final String testMobileNumber = DataGenerator.generateRandomMobileNumber();

    private String tapSessionId;
    private String deviceId;
    private String tapDshan;
    private String sessionCookie;

    @Test(priority = 1, groups = {"signup"})
    public void testCompleteSignup() {
        this.deviceId = DataGenerator.generateDeviceId();

        System.out.println("Generated Mobile Number: " + testMobileNumber);

        Response requestOtpResponse = OtpHelper.requestPhoneOtp(testMobileNumber, countryCode, this.deviceId, commonHeaders);

        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "OTP request failed.");

        this.tapSessionId = requestOtpResponse.header("X-Tap-Session");
        this.deviceId = requestOtpResponse.header("X-Device-Id");
        this.tapDshan = requestOtpResponse.header("X-Tap-Dshan");
        this.sessionCookie = requestOtpResponse.header("_session");

        Response verifyOtpResponse = OtpHelper.verifyPhoneOtp(testMobileNumber, countryCode, fixedOtp, this.tapSessionId, this.deviceId, this.tapDshan, this.sessionCookie, commonHeaders);

        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "OTP verification failed.");

        ApiBaseTest.tapAuth = verifyOtpResponse.header("X-Tap-Auth");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        ApiBaseTest.userId = verifyOtpResponse.jsonPath().getInt("userId");
        ApiBaseTest.tapSessionId = this.tapSessionId;
        ApiBaseTest.deviceId = this.deviceId;
        ApiBaseTest.tapDshan = this.tapDshan;
        ApiBaseTest.sessionCookie = this.sessionCookie;
        ApiBaseTest.mobileNumber = this.testMobileNumber;

        Assert.assertNotNull(ApiBaseTest.authToken, "Auth token not found.");
        Assert.assertNotNull(ApiBaseTest.userId, "User ID not found.");
    }
}