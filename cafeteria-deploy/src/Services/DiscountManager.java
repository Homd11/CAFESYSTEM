package Services;

import Core.Student;
import Values.Discount;
import Values.Money;
import Enums.Currency;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class DiscountManager {
    // Store available discounts per student ID
    private static Map<Integer, List<Discount>> studentDiscounts = new HashMap<>();

    public static void addDiscount(Student student, Discount discount) {
        int studentId = student.getId();
        studentDiscounts.computeIfAbsent(studentId, k -> new ArrayList<>()).add(discount);
    }

    public static List<Discount> getAvailableDiscounts(Student student) {
        return studentDiscounts.getOrDefault(student.getId(), new ArrayList<>());
    }

    public static boolean hasAvailableDiscounts(Student student) {
        List<Discount> discounts = getAvailableDiscounts(student);
        return !discounts.isEmpty();
    }

    public static Money getTotalDiscountValue(Student student) {
        List<Discount> discounts = getAvailableDiscounts(student);
        double totalValue = discounts.stream()
                .mapToDouble(Discount::getAmount)
                .sum();
        return new Money(totalValue, Currency.EGP);
    }

    public static Money applyDiscounts(Student student, Money orderTotal) {
        List<Discount> discounts = getAvailableDiscounts(student);
        if (discounts.isEmpty()) {
            return orderTotal;
        }

        double totalDiscount = discounts.stream()
                .mapToDouble(Discount::getAmount)
                .sum();

        double finalAmount = Math.max(0, orderTotal.getAmount().doubleValue() - totalDiscount);

        // Clear used discounts
        studentDiscounts.put(student.getId(), new ArrayList<>());

        return new Money(finalAmount, orderTotal.getCurrency());
    }

    public static void clearDiscounts(Student student) {
        studentDiscounts.put(student.getId(), new ArrayList<>());
    }
}
