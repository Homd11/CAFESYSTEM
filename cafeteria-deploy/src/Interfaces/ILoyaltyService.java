package Interfaces;

import Values.Discount;
import Values.Money;
import Core.Student;

public interface ILoyaltyService {
    public void awardPoints(Student student, Money amount);
    public Discount redeem(Student student, int points);
    public int getBalance(Student student);

}
