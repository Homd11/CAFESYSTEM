package Values;

import Enums.Currency;

import java.math.BigDecimal;

public class Money {
    private BigDecimal amount;
    private Currency currency; //  String

    public Money(double amount, Currency currency) {
        this.amount = BigDecimal.valueOf(amount);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null money");
        }
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currencies must match to add: " + this.currency + " vs " + other.currency);
        }
        return new Money(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null money");
        }
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currencies must match to subtract: " + this.currency + " vs " + other.currency);
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result.doubleValue(), this.currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)).doubleValue(), this.currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return amount.compareTo(money.amount) == 0 && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
