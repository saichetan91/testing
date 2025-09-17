package flows;

import common.HeaderHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

public class InvestmentFlowAPI {
    public static void main(String[] args) {
        RestAssured.baseURI = "https://kraken-stage.tapinvest.in";

        try {
            // Step 1: Fetch available deals
            System.out.println("=== Fetching Deals ===");
            Response dealsResponse = HeaderHelper.getRequestWithHeaders()
                    .get("/v2/deals");

            if (dealsResponse.getStatusCode() != 200) {
                System.out.println("Failed to fetch deals. Status: " + dealsResponse.getStatusCode());
                return;
            }

            System.out.println("Deals Response: " + dealsResponse.prettyPrint());

            // Step 2: Select a deal ID (using 4489 from your example)
            int dealId = 4487;
            double investmentAmount = 5.0;

            // Step 3: Fetch specific deal details
            System.out.println("\n=== Fetching Deal Details for Deal ID: " + dealId + " ===");
            Response dealDetails = HeaderHelper.getRequestWithHeaders()
                    .get("/v2/deals/" + dealId);

            if (dealDetails.getStatusCode() != 200) {
                System.out.println("Failed to fetch deal details. Status: " + dealDetails.getStatusCode());
                return;
            }

            System.out.println("Deal Details: " + dealDetails.prettyPrint());

            // Step 3.1: Validate deal availability and investment amount
            JSONObject dealResult = new JSONObject(dealDetails.getBody().asString()).getJSONObject("result");

            // Check if deal is sold out
            boolean isSoldOut = dealResult.getBoolean("isSoldOut");
            if (isSoldOut) {
                System.err.println("ERROR: Deal ID " + dealId + " is SOLD OUT. Cannot proceed with investment.");
                return;
            }

            // Check minimum investment amount
            JSONObject minInvestment = dealResult.getJSONObject("minInvestment");
            double minimumAmount = minInvestment.getDouble("amount");

            if (investmentAmount < minimumAmount) {
                System.err.println("ERROR: Investment amount " + investmentAmount + " is below minimum required amount of " + minimumAmount);
                return;
            }

            // Check maximum investment amount if available
            if (dealResult.has("maxInvestment")) {
                JSONObject maxInvestment = dealResult.getJSONObject("maxInvestment");
                double maximumAmount = maxInvestment.getDouble("amount");

                if (investmentAmount > maximumAmount) {
                    System.err.println("ERROR: Investment amount " + investmentAmount + " exceeds maximum allowed amount of " + maximumAmount);
                    return;
                }
            }

            // Check if investment is allowed
            if (dealResult.has("allowInvestment")) {
                boolean allowInvestment = dealResult.getBoolean("allowInvestment");
                if (!allowInvestment) {
                    System.err.println("ERROR: Investment is not allowed for this deal at the moment.");
                    return;
                }
            }

            System.out.println("✓ Deal validation passed. Proceeding with investment...");
            System.out.println("✓ Deal is available (not sold out)");
            System.out.println("✓ Investment amount " + investmentAmount + " is within valid range [" + minimumAmount + " - " + (dealResult.has("maxInvestment") ? dealResult.getJSONObject("maxInvestment").getDouble("amount") : "unlimited") + "]");

            // Step 4: Get investment terms for the amount
            System.out.println("\n=== Fetching Investment Terms ===");
            Response investmentTerms = HeaderHelper.getRequestWithHeaders()
                    .queryParam("amount", investmentAmount)
                    .queryParam("financeType", "INVOICE_DISCOUNTING")
                    .get("/v2/deals/" + dealId + "/investment-terms");

            if (investmentTerms.getStatusCode() != 200) {
                System.out.println("Failed to fetch investment terms. Status: " + investmentTerms.getStatusCode());
                return;
            }

            System.out.println("Investment Terms: " + investmentTerms.prettyPrint());

            // Step 5: Check buffer availability (optional)
            System.out.println("\n=== Checking Buffer Availability ===");
            Response bufferCheck = HeaderHelper.getRequestWithHeaders()
                    .queryParam("amount", investmentAmount)
                    .get("/v2/investments/buffer-available");

            System.out.println("Buffer Check Status: " + bufferCheck.getStatusCode());
            if (bufferCheck.getStatusCode() == 200) {
                System.out.println("Buffer Response: " + bufferCheck.prettyPrint());
            }

            // Step 6: Start investment flow
            System.out.println("\n=== Starting Investment Flow ===");

            // Create the payload based on the API documentation
            JSONObject dealDetail = new JSONObject();
            dealDetail.put("dealId", dealId);
            dealDetail.put("investmentAmount", investmentAmount);
            dealDetail.put("reinvestment", false);

            JSONObject paymentOrderRequest = new JSONObject();
            paymentOrderRequest.put("successPath", "https://stage.getultra.club/dashboard?status=success&rechargeStatus=PAID");
            paymentOrderRequest.put("failurePath", "https://stage.getultra.club/dashboard?status=failure");

            JSONObject payload = new JSONObject();
            payload.put("dealDetail", dealDetail);
            payload.put("useWalletBalance", true);
            payload.put("allowPartialPayment", true);
            payload.put("paymentOrderRequest", paymentOrderRequest);

            Response investmentResponse = HeaderHelper.getRequestWithHeaders()
                    .contentType(ContentType.JSON)
                    .body(payload.toString())
                    .post("/v2/investments/start-invoice-discounting-investment-flow");

            System.out.println("Investment Flow Response Status: " + investmentResponse.getStatusCode());
            System.out.println("Investment Response: " + investmentResponse.prettyPrint());

            // Step 7: If investment was successful, fetch transaction view
            if (investmentResponse.getStatusCode() == 200) {
                System.out.println("\n=== Fetching Transaction View ===");

                JSONObject transactionFilter = new JSONObject();
                transactionFilter.put("pageNumber", 0);
                transactionFilter.put("pageSize", 3);

                JSONObject investmentStatusFilter = new JSONObject();
                investmentStatusFilter.put("values", new String[]{"SUCCESS"});
                investmentStatusFilter.put("filterType", "IN");
                transactionFilter.put("investmentStatus", investmentStatusFilter);

                JSONObject transactionTypeFilter = new JSONObject();
                transactionTypeFilter.put("values", new String[]{"INVESTMENT"});
                transactionTypeFilter.put("filterType", "IN");
                transactionFilter.put("transactionType", transactionTypeFilter);

                JSONObject transactionDateSort = new JSONObject();
                transactionDateSort.put("sortOrder", "DESC");
                transactionFilter.put("transactionDate", transactionDateSort);

                Response transactionView = HeaderHelper.getRequestWithHeaders()
                        .contentType(ContentType.JSON)
                        .body(transactionFilter.toString())
                        .post("/v2/investment-dashboard/get-transaction-view");

                System.out.println("Transaction View Status: " + transactionView.getStatusCode());
                if (transactionView.getStatusCode() == 200) {
                    System.out.println("Transaction View: " + transactionView.prettyPrint());
                }

                // Step 8: Get investment metrics
                System.out.println("\n=== Fetching Investment Metrics ===");
                Response metricsResponse = HeaderHelper.getRequestWithHeaders()
                        .get("/v2/investment-dashboard/get-metrics/ALL");

                System.out.println("Metrics Status: " + metricsResponse.getStatusCode());
                if (metricsResponse.getStatusCode() == 200) {
                    System.out.println("Investment Metrics: " + metricsResponse.prettyPrint());
                }

                // Step 9: Check if user is invested
                System.out.println("\n=== Checking User Investment Status ===");
                Response userInvestmentStatus = HeaderHelper.getRequestWithHeaders()
                        .get("/v2/user/is-invested");

                System.out.println("User Investment Status: " + userInvestmentStatus.getStatusCode());
                if (userInvestmentStatus.getStatusCode() == 200) {
                    System.out.println("User Investment Response: " + userInvestmentStatus.prettyPrint());
                }
            }

        } catch (Exception e) {
            System.err.println("Error occurred during investment flow: " + e.getMessage());
            e.printStackTrace();
        }
    }
}