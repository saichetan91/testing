package flows;

import io.restassured.response.Response;

import common.WalletAPIHelper;

public class WalletAPI {

    // GET Wallet Transactions
    public static void getWalletTransactions() {
        Response response = WalletAPIHelper.getWalletTransactions("CREDIT,DEBIT", "INITIATED,SUCCESS,FAILED", 0, 10, true);
        System.out.println("Wallet Transactions Response: " + response.asPrettyString());
    }

    // GET Wallet Balance
    public static void getWalletBalance() {
        Response response = WalletAPIHelper.getWalletBalance();
        System.out.println("Wallet Balance Response: " + response.asPrettyString());
    }

    // GET Buffer Available
    public static void getBufferAvailable(int amount) {
        Response response = WalletAPIHelper.getBufferAvailable(amount);
        System.out.println("Buffer Available Response: " + response.asPrettyString());
    }

    // POST Wallet Recharge
    public static void rechargeWallet(double amount, String paymentMode) {
        Response response = WalletAPIHelper.rechargeWallet(amount, paymentMode);
        System.out.println("Wallet Recharge Response: " + response.asPrettyString());
    }

    // GET OTP Request
    public static void requestOTP() {
        Response response = WalletAPIHelper.requestOTP();
        System.out.println("OTP Request Response: " + response.asPrettyString());
    }

    // POST Withdraw Request
    public static void withdrawRequest(double amount) {
        Response response = WalletAPIHelper.withdrawRequest(amount);
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