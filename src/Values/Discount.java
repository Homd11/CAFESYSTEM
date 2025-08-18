package Values;

public class Discount {
    private double amount;
    private String description;

    public Discount(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;

    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}