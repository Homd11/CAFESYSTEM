package Services;

import Interfaces.IPaymentMethod;
import Values.Money;

/**
 * MasterCard Payment Implementation
 * Follows Single Responsibility Principle - handles only MasterCard payments
 */
public class MasterCardPayment implements IPaymentMethod {
    private Money lastPaymentAmount;
    private boolean lastPaymentStatus;
    private String authorizationCode;

    @Override
    public boolean processPayment(Money amount) {
        if (!validatePayment(amount)) {
            this.lastPaymentStatus = false;
            return false;
        }

        this.lastPaymentAmount = amount;

        // Simulate MasterCard payment processing
        System.out.println("💳 Processing MasterCard payment of " + amount.toString());
        System.out.println("🔄 Connecting to MasterCard secure network...");

        // Simulate processing delay
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate mock authorization code
        this.authorizationCode = "MC" + (System.currentTimeMillis() % 1000000);
        this.lastPaymentStatus = true;

        System.out.println("✅ MasterCard payment authorized!");
        System.out.println("🔐 Authorization Code: " + authorizationCode);

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "MasterCard";
    }

    @Override
    public String getPaymentDetails() {
        if (lastPaymentStatus && lastPaymentAmount != null) {
            return "💳 MasterCard Payment: " + lastPaymentAmount.toString() +
                   " | Auth Code: " + authorizationCode;
        }
        return "❌ MasterCard payment failed or not processed";
    }

    @Override
    public boolean validatePayment(Money amount) {
        if (amount == null) {
            System.out.println("❌ Invalid payment amount");
            return false;
        }

        if (amount.getAmount().doubleValue() <= 0) {
            System.out.println("❌ Payment amount must be positive");
            return false;
        }

        // Simulate card validation
        System.out.println("🔍 Validating MasterCard...");

        return true;
    }
}
