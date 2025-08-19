package Services;

import Interfaces.IPaymentMethod;
import Interfaces.IPaymentProcessor;
import Values.Money;
import Enums.PaymentMethod;
import java.util.Scanner;

/**
 * Payment Processor Implementation
 * Follows Open/Closed Principle - can be extended with new payment methods
 * Follows Dependency Inversion Principle - depends on abstractions not concretions
 */
public class PaymentProcessor implements IPaymentProcessor {
    private IPaymentMethod lastUsedPaymentMethod;
    private String lastPaymentConfirmation;

    @Override
    public boolean processPayment(Money amount, PaymentMethod paymentMethod) {
        IPaymentMethod payment = createPaymentMethod(paymentMethod);

        if (payment == null) {
            System.out.println("❌ Unsupported payment method: " + paymentMethod);
            return false;
        }

        boolean success = payment.processPayment(amount);

        if (success) {
            this.lastUsedPaymentMethod = payment;
            this.lastPaymentConfirmation = payment.getPaymentDetails();
        }

        return success;
    }

    @Override
    public PaymentMethod[] getAvailablePaymentMethods() {
        return PaymentMethod.values();
    }

    @Override
    public String getPaymentConfirmation() {
        return lastPaymentConfirmation != null ? lastPaymentConfirmation : "No payment processed";
    }

    /**
     * Factory method to create payment method instances
     * Follows Factory Pattern and Open/Closed Principle
     */
    private IPaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CASH:
                return new CashPayment();
            case VISA:
                return new VisaPayment();
            case MASTERCARD:
                return new MasterCardPayment();
            default:
                return null;
        }
    }

    /**
     * Interactive payment method selection for users
     * @param scanner Scanner for user input
     * @param amount Amount to be paid
     * @return true if payment successful, false otherwise
     */
    public boolean processInteractivePayment(Scanner scanner, Money amount) {
        System.out.println("\n💳 PAYMENT SELECTION");
        System.out.println("=".repeat(40));
        System.out.println("Total Amount: " + amount.toString());
        System.out.println("=".repeat(40));

        PaymentMethod[] methods = getAvailablePaymentMethods();

        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i].getDisplayName());
        }

        System.out.println("0. Cancel Payment");
        System.out.println("=".repeat(40));
        System.out.print("👉 Select payment method (0-" + methods.length + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 0) {
                System.out.println("❌ Payment cancelled.");
                return false;
            }

            if (choice < 1 || choice > methods.length) {
                System.out.println("❌ Invalid choice. Payment cancelled.");
                return false;
            }

            PaymentMethod selectedMethod = methods[choice - 1];
            System.out.println("\n📝 You selected: " + selectedMethod.getDisplayName());
            System.out.println("=".repeat(40));

            boolean success = processPayment(amount, selectedMethod);

            if (success) {
                System.out.println("\n🎉 PAYMENT SUCCESS!");
                System.out.println("=".repeat(40));
                System.out.println(getPaymentConfirmation());
                System.out.println("=".repeat(40));
            } else {
                System.out.println("\n❌ PAYMENT FAILED!");
                System.out.println("Please try again or contact support.");
            }

            return success;

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Payment cancelled.");
            return false;
        }
    }

    /**
     * Get last used payment method for logging/tracking purposes
     */
    public IPaymentMethod getLastUsedPaymentMethod() {
        return lastUsedPaymentMethod;
    }
}
