package common;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

/**
 * Common API helper for wallet related operations
 */
public class WalletAPIHelper {

    /**
     * Get wallet transactions with filters
     */
    public static Response getWalletTransactions(String type, String state, int pageNo, int pageSize, boolean sortByUpdatedAtDesc) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .queryParam("type", type)
                .queryParam("state", state)
                .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize)
                .queryParam("sortByUpdatedAtDesc", sortByUpdatedAtDesc)
                .get("/v2/wallet/transactions");
    }

    /**
     * Get wallet balance
     */
    public static Response getWalletBalance() {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/wallet/balance");
    }

    /**
     * Check buffer available for amount
     */
    public static Response getBufferAvailable(double amount) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .queryParam("amount", amount)
                .get("/v2/investments/buffer-available");
    }

    /**
     * Recharge wallet
     */
    public static Response rechargeWallet(double amount, String paymentMode) {
        JSONObject payload = new JSONObject();
        payload.put("amount", amount);
        payload.put("paymentMode", paymentMode);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .body(payload.toString())
                .post("/v2/wallet/recharge");
    }

    /**
     * Request OTP for wallet operations
     */
    public static Response requestOTP() {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/wallet/otp/request");
    }

    /**
     * Request wallet withdrawal
     */
    public static Response withdrawRequest(double amount) {
        JSONObject payload = new JSONObject();
        payload.put("amount", amount);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .body(payload.toString())
                .post("/v2/wallet/withdraw-request");
    }
}
