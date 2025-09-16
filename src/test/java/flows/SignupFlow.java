// src/test/java/flows/SignupFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.UUID;

import common.ApiBaseTest;
import common.OtpHelper;

public class SignupFlow extends ApiBaseTest {

    private final String countryCode = "+91";
    private final String fixedOtp = "1001";

    private final String testMobileNumber = generateRandomMobileNumber();

    private String tapSessionId;
    private String deviceId;
    private String tapDshan;
    private String sessionCookie;

    private String generateRandomMobileNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6;
        String remainingDigits = String.format("%09d", random.nextInt(1_000_000_000));
        return String.valueOf(firstDigit) + remainingDigits;
    }

    @Test(priority = 1, groups = {"signup"})
    public void testCompleteSignup() {
        this.deviceId = UUID.randomUUID().toString();

        System.out.println("Generated Mobile Number: " + testMobileNumber);

        Response requestOtpResponse = OtpHelper.requestPhoneOtp(testMobileNumber, countryCode, this.deviceId, commonHeaders);

        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "OTP request failed.");

        this.tapSessionId = requestOtpResponse.header("X-Tap-Session");
        this.deviceId = requestOtpResponse.header("X-Device-Id");
        this.tapDshan = requestOtpResponse.header("X-Tap-Dshan");
        this.sessionCookie = requestOtpResponse.header("_session");

        Response verifyOtpResponse = OtpHelper.verifyPhoneOtp(testMobileNumber, countryCode, fixedOtp, this.tapSessionId, this.deviceId, this.tapDshan, this.sessionCookie, commonHeaders);

        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "OTP verification failed.");

        this.tapAuth = verifyOtpResponse.header("X-Tap-Auth");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        ApiBaseTest.userId = verifyOtpResponse.jsonPath().getInt("userId");
        ApiBaseTest.tapSessionId = this.tapSessionId;
        ApiBaseTest.deviceId = this.deviceId;
        ApiBaseTest.tapDshan = this.tapDshan;
        ApiBaseTest.sessionCookie = this.sessionCookie;
        ApiBaseTest.mobileNumber = this.testMobileNumber;
        ApiBaseTest.tapAuth = this.tapAuth;

        Assert.assertNotNull(ApiBaseTest.authToken, "Auth token not found.");
        Assert.assertNotNull(ApiBaseTest.userId, "User ID not found.");
    }
}