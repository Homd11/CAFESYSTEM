package Services;

import Interfaces.IPaymentMethod;
import Values.Money;

/**
 * Visa Payment Implementation
 * Follows Single Responsibility Principle - handles only Visa card payments
 */
public class VisaPayment implements IPaymentMethod {
    private Money lastPaymentAmount;
    private boolean lastPaymentStatus;
    private String transactionId;

    @Override
    public boolean processPayment(Money amount) {
        if (!validatePayment(amount)) {
            this.lastPaymentStatus = false;
            return false;
        }

        this.lastPaymentAmount = amount;

        // Simulate Visa payment processing
        System.out.println("💳 Processing Visa payment of " + amount.toString());
        System.out.println("🔄 Connecting to Visa payment gateway...");

        // Simulate processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate mock transaction ID
        this.transactionId = "VISA" + System.currentTimeMillis();
        this.lastPaymentStatus = true;

        System.out.println("✅ Visa payment approved!");
        System.out.println("📄 Transaction ID: " + transactionId);

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Visa Card";
    }

    @Override
    public String getPaymentDetails() {
        if (lastPaymentStatus && lastPaymentAmount != null) {
            return "💳 Visa Payment: " + lastPaymentAmount.toString() +
                   " | Transaction ID: " + transactionId;
        }
        return "❌ Visa payment failed or not processed";
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
        System.out.println("🔍 Validating Visa card...");

        return true;
    }
}
