package Interfaces;

import Values.Money;
import Enums.PaymentMethod;

/**
 * Payment Processor interface following Open/Closed Principle
 * Allows adding new payment methods without modifying existing code
 */
public interface IPaymentProcessor {

    /**
     * Process payment using the specified method
     * @param amount The amount to be charged
     * @param paymentMethod The payment method to use
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(Money amount, PaymentMethod paymentMethod);

    /**
     * Get available payment methods
     * @return Array of available payment methods
     */
    PaymentMethod[] getAvailablePaymentMethods();

    /**
     * Get payment confirmation details
     * @return Payment confirmation string
     */
    String getPaymentConfirmation();
}
