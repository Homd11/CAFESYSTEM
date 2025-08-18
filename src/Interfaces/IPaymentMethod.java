package Interfaces;

import Values.Money;

/**
 * Payment interface following Single Responsibility Principle
 * Each payment method implements this interface
 */
public interface IPaymentMethod {

    /**
     * Process the payment for the given amount
     * @param amount The amount to be paid
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(Money amount);

    /**
     * Get the payment method name
     * @return The name of the payment method
     */
    String getPaymentMethodName();

    /**
     * Get payment details for receipt/confirmation
     * @return Payment details string
     */
    String getPaymentDetails();

    /**
     * Validate if the payment method can process the given amount
     * @param amount The amount to validate
     * @return true if valid, false otherwise
     */
    boolean validatePayment(Money amount);
}
