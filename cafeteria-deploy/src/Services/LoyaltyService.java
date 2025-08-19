package Services;

import Core.LoyaltyAccount;
import Core.Student;
import DB.LoyaltyAccountDAO;
import Interfaces.ILoyaltyService;
import Values.Discount;
import Values.Money;

public class LoyaltyService implements ILoyaltyService {
    private static final int POINTS_PER_EGP = 1; // 1 point per EGP spent
    private static final double DISCOUNT_VALUE_PER_POINT = 0.1; // 0.1 EGP per point

    private final LoyaltyAccountDAO loyaltyAccountDAO;

    public LoyaltyService() {
        this.loyaltyAccountDAO = new LoyaltyAccountDAO();
    }

    @Override
    public void awardPoints(Student student, Money amount) {
        if (student == null || student.getLoyaltyAccount() == null) {
            throw new IllegalArgumentException("Student and loyalty account cannot be null");
        }
        if (amount == null || amount.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Convert amount to points (assuming EGP base currency)
        int pointsToAward = (int) (amount.getAmount().doubleValue() * POINTS_PER_EGP);

        LoyaltyAccount account = student.getLoyaltyAccount();
        account.addPoints(pointsToAward);
        loyaltyAccountDAO.update(account);
    }

    @Override
    public Discount redeem(Student student, int points) {
        if (student == null || student.getLoyaltyAccount() == null) {
            throw new IllegalArgumentException("Student and loyalty account cannot be null");
        }
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }

        LoyaltyAccount account = student.getLoyaltyAccount();
        if (!account.deductPoints(points)) {
            throw new IllegalStateException("Insufficient points for redemption");
        }

        loyaltyAccountDAO.update(account);

        double discountValue = points * DISCOUNT_VALUE_PER_POINT;
        return new Discount(discountValue, "Loyalty points redemption: " + points + " points");
    }

    @Override
    public int getBalance(Student student) {
        if (student == null || student.getLoyaltyAccount() == null) {
            return 0;
        }
        return student.getLoyaltyAccount().getPoints();
    }
}
