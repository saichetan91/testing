package flows;

import common.HeaderHelper;
import io.restassured.response.Response;
import org.json.JSONObject;

public class WalletAPI {

    // GET Wallet Transactions
    public static void getWalletTransactions() {
        Response response = HeaderHelper.getRequestWithHeaders()
                .queryParam("type", "CREDIT,DEBIT")
                .queryParam("state", "INITIATED,SUCCESS,FAILED")
                .queryParam("pageNo", "0")
                .queryParam("pageSize", "10")
                .queryParam("sortByUpdatedAtDesc", "true")
                .get("https://kraken-stage.tapinvest.in/v2/wallet/transactions");

        System.out.println("Wallet Transactions Response: " + response.asPrettyString());
    }

    // GET Wallet Balance
    public static void getWalletBalance() {
        Response response = HeaderHelper.getRequestWithHeaders()
                .get("https://kraken-stage.tapinvest.in/v2/wallet/balance");

        System.out.println("Wallet Balance Response: " + response.asPrettyString());
    }

    // GET Buffer Available
    public static void getBufferAvailable(int amount) {
        Response response = HeaderHelper.getRequestWithHeaders()
                .queryParam("amount", amount)
                .get("https://kraken-stage.tapinvest.in/v2/investments/buffer-available");

        System.out.println("Buffer Available Response: " + response.asPrettyString());
    }

    // POST Wallet Recharge
    public static void rechargeWallet(double amount, String paymentMode) {
        JSONObject payload = new JSONObject();
        payload.put("amount", amount);
        payload.put("paymentMode", paymentMode); // e.g., "UPI", "CARD"

        Response response = HeaderHelper.getRequestWithHeaders()
                .body(payload.toString())
                .post("https://kraken-stage.tapinvest.in/v2/wallet/recharge");

        System.out.println("Wallet Recharge Response: " + response.asPrettyString());
    }

    // GET OTP Request
    public static void requestOTP() {
        Response response = HeaderHelper.getRequestWithHeaders()
                .get("https://kraken-stage.tapinvest.in/v2/wallet/otp/request");

        System.out.println("OTP Request Response: " + response.asPrettyString());
    }

    // POST Withdraw Request
    public static void withdrawRequest(double amount) {
        JSONObject payload = new JSONObject();
        payload.put("amount", amount);

        Response response = HeaderHelper.getRequestWithHeaders()
                .body(payload.toString())
                .post("https://kraken-stage.tapinvest.in/v2/wallet/withdraw-request");

        System.out.println("Withdraw Request Response: " + response.asPrettyString());
    }

    public static void main(String[] args) {
        getWalletTransactions();
        getWalletBalance();
        getBufferAvailable(100);
        rechargeWallet(500, "UPI");
        requestOTP();
        withdrawRequest(200);
    }
}