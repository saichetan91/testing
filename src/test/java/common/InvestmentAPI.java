package common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

/**
 * Common API helper for investment related operations
 */
public class InvestmentAPI {

    /**
     * Fetch all available deals
     */
    public static Response getDeals() {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/deals");
    }

    /**
     * Fetch specific deal details by deal ID
     */
    public static Response getDealDetails(int dealId) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/deals/" + dealId);
    }

    /**
     * Get investment terms for a deal
     */
    public static Response getInvestmentTerms(int dealId, double amount, String financeType) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .queryParam("amount", amount)
                .queryParam("financeType", financeType)
                .get("/v2/deals/" + dealId + "/investment-terms");
    }

    /**
     * Check buffer availability
     */
    public static Response checkBufferAvailable(double amount) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .queryParam("amount", amount)
                .get("/v2/investments/buffer-available");
    }

    /**
     * Start invoice discounting investment flow
     */
    public static Response startInvoiceDiscountingInvestment(int dealId, double investmentAmount, 
                                                           boolean useWalletBalance, boolean allowPartialPayment,
                                                           String successPath, String failurePath) {
        JSONObject dealDetail = new JSONObject();
        dealDetail.put("dealId", dealId);
        dealDetail.put("investmentAmount", investmentAmount);
        dealDetail.put("reinvestment", false);

        JSONObject paymentOrderRequest = new JSONObject();
        paymentOrderRequest.put("successPath", successPath);
        paymentOrderRequest.put("failurePath", failurePath);

        JSONObject payload = new JSONObject();
        payload.put("dealDetail", dealDetail);
        payload.put("useWalletBalance", useWalletBalance);
        payload.put("allowPartialPayment", allowPartialPayment);
        payload.put("paymentOrderRequest", paymentOrderRequest);

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .contentType(ContentType.JSON)
                .body(payload.toString())
                .post("/v2/investments/start-invoice-discounting-investment-flow");
    }

    /**
     * Get transaction view with filters
     */
    public static Response getTransactionView(int pageNumber, int pageSize, String[] investmentStatus, 
                                            String[] transactionType, String sortOrder) {
        JSONObject transactionFilter = new JSONObject();
        transactionFilter.put("pageNumber", pageNumber);
        transactionFilter.put("pageSize", pageSize);

        if (investmentStatus != null && investmentStatus.length > 0) {
            JSONObject investmentStatusFilter = new JSONObject();
            investmentStatusFilter.put("values", investmentStatus);
            investmentStatusFilter.put("filterType", "IN");
            transactionFilter.put("investmentStatus", investmentStatusFilter);
        }

        if (transactionType != null && transactionType.length > 0) {
            JSONObject transactionTypeFilter = new JSONObject();
            transactionTypeFilter.put("values", transactionType);
            transactionTypeFilter.put("filterType", "IN");
            transactionFilter.put("transactionType", transactionTypeFilter);
        }

        if (sortOrder != null) {
            JSONObject transactionDateSort = new JSONObject();
            transactionDateSort.put("sortOrder", sortOrder);
            transactionFilter.put("transactionDate", transactionDateSort);
        }

        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .contentType(ContentType.JSON)
                .body(transactionFilter.toString())
                .post("/v2/investment-dashboard/get-transaction-view");
    }

    /**
     * Get investment metrics
     */
    public static Response getInvestmentMetrics(String metricsType) {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/investment-dashboard/get-metrics/" + metricsType);
    }

    /**
     * Check if user is invested
     */
    public static Response checkUserInvestmentStatus() {
        return RestAssured.given()
                .headers(ApiBaseTest.commonHeaders)
                .get("/v2/user/is-invested");
    }

    /**
     * Validate deal for investment
     */
    public static boolean validateDealForInvestment(Response dealDetailsResponse, double investmentAmount) {
        if (dealDetailsResponse.getStatusCode() != 200) {
            System.err.println("Failed to fetch deal details. Status: " + dealDetailsResponse.getStatusCode());
            return false;
        }

        try {
            JSONObject dealResult = new JSONObject(dealDetailsResponse.getBody().asString()).getJSONObject("result");

            // Check if deal is sold out
            boolean isSoldOut = dealResult.getBoolean("isSoldOut");
            if (isSoldOut) {
                System.err.println("ERROR: Deal is SOLD OUT. Cannot proceed with investment.");
                return false;
            }

            // Check minimum investment amount
            JSONObject minInvestment = dealResult.getJSONObject("minInvestment");
            double minimumAmount = minInvestment.getDouble("amount");

            if (investmentAmount < minimumAmount) {
                System.err.println("ERROR: Investment amount " + investmentAmount + " is below minimum required amount of " + minimumAmount);
                return false;
            }

            // Check maximum investment amount if available
            if (dealResult.has("maxInvestment")) {
                JSONObject maxInvestment = dealResult.getJSONObject("maxInvestment");
                double maximumAmount = maxInvestment.getDouble("amount");

                if (investmentAmount > maximumAmount) {
                    System.err.println("ERROR: Investment amount " + investmentAmount + " exceeds maximum allowed amount of " + maximumAmount);
                    return false;
                }
            }

            // Check if investment is allowed
            if (dealResult.has("allowInvestment")) {
                boolean allowInvestment = dealResult.getBoolean("allowInvestment");
                if (!allowInvestment) {
                    System.err.println("ERROR: Investment is not allowed for this deal at the moment.");
                    return false;
                }
            }

            System.out.println("✓ Deal validation passed. Proceeding with investment...");
            System.out.println("✓ Deal is available (not sold out)");
            System.out.println("✓ Investment amount " + investmentAmount + " is within valid range [" + minimumAmount + " - " + (dealResult.has("maxInvestment") ? dealResult.getJSONObject("maxInvestment").getDouble("amount") : "unlimited") + "]");
            
            return true;
        } catch (Exception e) {
            System.err.println("Error validating deal: " + e.getMessage());
            return false;
        }
    }
}
