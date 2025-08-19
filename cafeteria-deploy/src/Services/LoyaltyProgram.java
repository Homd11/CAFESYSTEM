package Services;

import Core.LoyaltyAccount;
import Core.Student;
import DB.StudentDAO;
import Interfaces.ILoyaltyService;
import Interfaces.IStudentRepositor;
import Values.Discount;
import Values.Money;

public class LoyaltyProgram implements ILoyaltyService {
    private static final double POINTS_PER_EGP = 1.0; // 1 point per EGP spent
    private static final double EGP_PER_POINT = 0.1; // 0.1 EGP per point

    private final IStudentRepositor repo;

    public LoyaltyProgram() {
        this.repo = new StudentDAO();
    }

    public LoyaltyProgram(IStudentRepositor repo) {
        this.repo = repo;
    }

    @Override
    public void awardPoints(Student student, Money amount) {
        if (student == null || student.getAccount() == null) {
            throw new IllegalArgumentException("Student and loyalty account cannot be null");
        }
        if (amount == null || amount.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Convert amount to points (assuming EGP base currency)
        int pointsToAward = (int) (amount.getAmount().doubleValue() * POINTS_PER_EGP);

        LoyaltyAccount account = student.getAccount();
        account.add(pointsToAward);

        // Update student in repository
        repo.update(student);
    }

    @Override
    public Discount redeem(Student student, int points) {
        if (student == null || student.getAccount() == null) {
            throw new IllegalArgumentException("Student and loyalty account cannot be null");
        }
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }

        LoyaltyAccount account = student.getAccount();

        try {
            account.deduct(points);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Insufficient points for redemption");
        }

        // Update student in repository
        repo.update(student);

        double discountValue = points * EGP_PER_POINT;
        return new Discount(discountValue, "Loyalty points redemption: " + points + " points");
    }

    @Override
    public int getBalance(Student student) {
        if (student == null || student.getAccount() == null) {
            return 0;
        }
        return student.getAccount().balance();
    }
}
