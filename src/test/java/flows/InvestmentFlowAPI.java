package flows;

import io.restassured.response.Response;

import common.InvestmentAPI;

public class InvestmentFlowAPI {
    public static void main(String[] args) {
        try {
            // Step 1: Fetch available deals
            System.out.println("=== Fetching Deals ===");
            Response dealsResponse = InvestmentAPI.getDeals();

            if (dealsResponse.getStatusCode() != 200) {
                System.out.println("Failed to fetch deals. Status: " + dealsResponse.getStatusCode());
                return;
            }

            System.out.println("Deals Response: " + dealsResponse.prettyPrint());

            // Step 2: Select a deal ID
            int dealId = 4487;
            double investmentAmount = 5.0;

            // Step 3: Fetch specific deal details
            System.out.println("\n=== Fetching Deal Details for Deal ID: " + dealId + " ===");
            Response dealDetails = InvestmentAPI.getDealDetails(dealId);

            System.out.println("Deal Details: " + dealDetails.prettyPrint());

            // Step 3.1: Validate deal availability and investment amount
            if (!InvestmentAPI.validateDealForInvestment(dealDetails, investmentAmount)) {
                return;
            }

            // Step 4: Get investment terms for the amount
            System.out.println("\n=== Fetching Investment Terms ===");
            Response investmentTerms = InvestmentAPI.getInvestmentTerms(dealId, investmentAmount, "INVOICE_DISCOUNTING");

            if (investmentTerms.getStatusCode() != 200) {
                System.out.println("Failed to fetch investment terms. Status: " + investmentTerms.getStatusCode());
                return;
            }

            System.out.println("Investment Terms: " + investmentTerms.prettyPrint());

            // Step 5: Check buffer availability (optional)
            System.out.println("\n=== Checking Buffer Availability ===");
            Response bufferCheck = InvestmentAPI.checkBufferAvailable(investmentAmount);

            System.out.println("Buffer Check Status: " + bufferCheck.getStatusCode());
            if (bufferCheck.getStatusCode() == 200) {
                System.out.println("Buffer Response: " + bufferCheck.prettyPrint());
            }

            // Step 6: Start investment flow
            System.out.println("\n=== Starting Investment Flow ===");
            Response investmentResponse = InvestmentAPI.startInvoiceDiscountingInvestment(
                    dealId, 
                    investmentAmount, 
                    true, 
                    true,
                    "https://stage.getultra.club/dashboard?status=success&rechargeStatus=PAID",
                    "https://stage.getultra.club/dashboard?status=failure"
            );

            System.out.println("Investment Flow Response Status: " + investmentResponse.getStatusCode());
            System.out.println("Investment Response: " + investmentResponse.prettyPrint());

            // Step 7: If investment was successful, fetch transaction view
            if (investmentResponse.getStatusCode() == 200) {
                System.out.println("\n=== Fetching Transaction View ===");
                Response transactionView = InvestmentAPI.getTransactionView(
                        0, 3, 
                        new String[]{"SUCCESS"}, 
                        new String[]{"INVESTMENT"}, 
                        "DESC"
                );

                System.out.println("Transaction View Status: " + transactionView.getStatusCode());
                if (transactionView.getStatusCode() == 200) {
                    System.out.println("Transaction View: " + transactionView.prettyPrint());
                }

                // Step 8: Get investment metrics
                System.out.println("\n=== Fetching Investment Metrics ===");
                Response metricsResponse = InvestmentAPI.getInvestmentMetrics("ALL");

                System.out.println("Metrics Status: " + metricsResponse.getStatusCode());
                if (metricsResponse.getStatusCode() == 200) {
                    System.out.println("Investment Metrics: " + metricsResponse.prettyPrint());
                }

                // Step 9: Check if user is invested
                System.out.println("\n=== Checking User Investment Status ===");
                Response userInvestmentStatus = InvestmentAPI.checkUserInvestmentStatus();

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