package Core;

import Enums.PaymentMethod;
import Values.Money;
import java.time.LocalDateTime;

/**
 * Payment entity class representing a payment record
 */
public class Payment {
    private int id;
    private int orderId;
    private PaymentMethod paymentMethod;
    private Money amount;
    private String transactionId;
    private String authorizationCode;
    private LocalDateTime paymentDate;
    private boolean successful;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(int orderId, PaymentMethod paymentMethod, Money amount) {
        this();
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, orderId=%d, method=%s, amount=%s, successful=%s, date=%s}",
                id, orderId, paymentMethod, amount, successful, paymentDate);
    }
}
