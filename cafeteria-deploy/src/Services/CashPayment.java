package Services;

import Interfaces.IPaymentMethod;
import Values.Money;

/**
 * Cash Payment Implementation
 * Follows Single Responsibility Principle - handles only cash payments
 */
public class CashPayment implements IPaymentMethod {
    private Money lastPaymentAmount;
    private boolean lastPaymentStatus;

    @Override
    public boolean processPayment(Money amount) {
        if (!validatePayment(amount)) {
            this.lastPaymentStatus = false;
            return false;
        }

        this.lastPaymentAmount = amount;
        this.lastPaymentStatus = true;

        // Simulate cash payment processing
        System.out.println("💵 Processing cash payment of " + amount.toString());
        System.out.println("✅ Cash payment received successfully!");

        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Cash Payment";
    }

    @Override
    public String getPaymentDetails() {
        if (lastPaymentStatus && lastPaymentAmount != null) {
            return "💵 Cash Payment: " + lastPaymentAmount.toString() + " - Paid in full";
        }
        return "❌ Cash payment failed or not processed";
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

        return true;
    }
}
