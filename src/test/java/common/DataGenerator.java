package common;

import java.util.Random;

/**
 * Utility class for generating test data
 */
public class DataGenerator {

    /**
     * Generate a random mobile number starting with 6-9
     */
    public static String generateRandomMobileNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6; // 6, 7, 8, or 9
        String remainingDigits = String.format("%09d", random.nextInt(1_000_000_000));
        return String.valueOf(firstDigit) + remainingDigits;
    }

    /**
     * Generate a bank account number with even last digit
     */
    public static String generateEvenBankAccountNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        // Ensure first digit is non-zero
        sb.append(random.nextInt(9) + 1);
        
        // Generate middle digits
        for (int i = 1; i < length - 1; i++) {
            sb.append(random.nextInt(10));
        }
        
        // Last digit must be even
        int[] evenDigits = {0, 2, 4, 6, 8};
        sb.append(evenDigits[random.nextInt(evenDigits.length)]);
        
        return sb.toString();
    }

    /**
     * Generate a test email from mobile number
     */
    public static String generateTestEmail(String mobileNumber) {
        return "testuser" + mobileNumber + "@example.com";
    }

    /**
     * Generate a random device ID
     */
    public static String generateDeviceId() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Generate a random IFSC code (mock format)
     */
    public static String generateMockIfscCode() {
        Random random = new Random();
        String[] banks = {"SBIN", "HDFC", "ICIC", "AXIS", "KOTK"};
        String bank = banks[random.nextInt(banks.length)];
        return bank + "000" + String.format("%04d", random.nextInt(10000));
    }
}
