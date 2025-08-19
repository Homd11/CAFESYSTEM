package Enums;

public enum PaymentMethod {
    CASH("Cash Payment"),
    VISA("Visa Card"),
    MASTERCARD("MasterCard");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
