package Interfaces;

import Core.Student;
import java.util.Scanner;

/**
 * Interface for student service operations
 */
public interface IStudentService {
    Student registerStudent(Scanner scanner);
    Student loginStudent(Scanner scanner);
    void viewMenuAndOrder(Student student, Scanner scanner);
    void viewOrderHistory(Student student, Scanner scanner);
    void redeemLoyaltyPoints(Student student, Scanner scanner);
    void viewProfile(Student student, Scanner scanner);
    void checkOrderStatus(Student student, Scanner scanner);
}
