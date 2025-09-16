// src/test/java/flows/InvestmentFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.SkipException;
import org.testng.annotations.Test;

import common.ApiBaseTest;
import io.restassured.specification.RequestSpecification;
import java.util.Random;

public class InvestmentFlow extends ApiBaseTest {

    // Test data for the investment flow
    private final int dealId = 4489;
    private final int investmentAmount = 5;
    private final String financeType = "INVOICE_DISCOUNTING";
    private final boolean useWalletBalance = true;

    @BeforeClass(alwaysRun = true)
    public void ensureExistingSession() {
        if (authToken == null || tapSessionId == null || deviceId == null || tapDshan == null || sessionCookie == null) {
            throw new SkipException("Missing existing user session. Populate session_data.json with X-Tap-Auth, X-Tap-Session, X-Device-Id, X-Tap-Dshan, _session.");
        }
    }

    private String generateRandomMobileNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6;
        String remainingDigits = String.format("%09d", random.nextInt(1_000_000_000));
        return String.valueOf(firstDigit) + remainingDigits;
    }

    @Test(priority = 1)
    public void testCompleteInvestmentFlow() {
        // Step 1: Get Deal Details
        // This is a GET request to retrieve information about a specific deal.
        String dealDetailsUrl = String.format("/v2/deals/data/%d", dealId);
        
        System.out.println("Executing: GET " + dealDetailsUrl);
        RequestSpecification dealReq = requestSpec.given();
        if (authToken != null) dealReq.header("X-Tap-Auth", authToken);
        if (tapSessionId != null) dealReq.header("X-Tap-Session", tapSessionId);
        if (deviceId != null) dealReq.header("X-Device-Id", deviceId);
        if (tapDshan != null) dealReq.header("X-Tap-Dshan", tapDshan);
        if (sessionCookie != null) dealReq.cookie("_session", sessionCookie);
        Response dealResponse = dealReq
            .log().all()
            .get(dealDetailsUrl);
        
        dealResponse.then().log().all();
        Assert.assertEquals(dealResponse.getStatusCode(), 200, "Failed to get deal details.");

        // Step 2: Get Investment Terms
        // This GET request is a pre-check to get investment terms based on amount.
        String investmentTermsUrl = String.format("/v2/deals/%d/investment-terms?amount=%d&financeType=%s", dealId, investmentAmount, financeType);
        
        System.out.println("Executing: GET " + investmentTermsUrl);
        RequestSpecification termsReq = requestSpec.given();
        if (authToken != null) termsReq.header("X-Tap-Auth", authToken);
        if (tapSessionId != null) termsReq.header("X-Tap-Session", tapSessionId);
        if (deviceId != null) termsReq.header("X-Device-Id", deviceId);
        if (tapDshan != null) termsReq.header("X-Tap-Dshan", tapDshan);
        if (sessionCookie != null) termsReq.cookie("_session", sessionCookie);
        Response termsResponse = termsReq
            .log().all()
            .get(investmentTermsUrl);
        
        termsResponse.then().log().all();
        Assert.assertEquals(termsResponse.getStatusCode(), 200, "Failed to get investment terms.");
        Assert.assertEquals(termsResponse.jsonPath().getInt("result.amount"), investmentAmount, "Investment amount mismatch.");
        
        // Step 3: Start the Investment Flow (The final POST request)
        // This is the core API call to submit the investment request.
        String investUrl = "/v2/investments/start-invoice-discounting-investment-flow";

        String requestBody = String.format("{\"dealDetail\": {\"dealId\": %d, \"investmentAmount\": %d, \"reinvestment\": false, \"multiDeal\": false}, \"useWalletBalance\": %b, \"allowPartialPayment\": true}",
                                           dealId, investmentAmount, useWalletBalance);
        
        System.out.println("Executing: POST " + investUrl);
        RequestSpecification investReq = requestSpec.given();
        if (authToken != null) investReq.header("X-Tap-Auth", authToken);
        if (tapSessionId != null) investReq.header("X-Tap-Session", tapSessionId);
        if (deviceId != null) investReq.header("X-Device-Id", deviceId);
        if (tapDshan != null) investReq.header("X-Tap-Dshan", tapDshan);
        if (sessionCookie != null) investReq.cookie("_session", sessionCookie);
        investReq.header("X-Platform-Request", "WEB_WRITE");
        Response investResponse = investReq
            .body(requestBody)
            .log().all()
            .post(investUrl);
        
        investResponse.then().log().all();
        Assert.assertEquals(investResponse.getStatusCode(), 200, "Investment failed.");
        Assert.assertEquals(investResponse.jsonPath().getString("flowStatus.CREATE_ID_INVESTMENT"), "SUCCESS", "Investment was not successful.");
    }
}