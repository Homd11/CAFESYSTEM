package Services;

import Core.Student;
import Core.LoyaltyAccount;
import DB.StudentDAO;
import Interfaces.IStudentRepositor;

public class StudentManager {
    private IStudentRepositor repo;

    public StudentManager() {
        this.repo = new StudentDAO();
    }

    public StudentManager(IStudentRepositor repo) {
        this.repo = repo;
    }

    public Student register(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        Student student = new Student();
        student.setName(name);
        student.setAccount(new LoyaltyAccount());

        // Generate a temporary student code before saving
        // We'll use a timestamp-based approach since we don't have the ID yet
        String tempStudentCode = generateTempStudentCode();
        student.setStudentCode(tempStudentCode);

        // Save student with temporary code
        repo.save(student);

        // Generate final student code based on ID
        String finalStudentCode = generateStudentCode(student.getId());
        student.setStudentCode(finalStudentCode);

        // Update with the final generated code
        repo.update(student);

        return student;
    }

    public Student login(String studentCode) {
        if (studentCode == null || studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student code cannot be null or empty");
        }

        return repo.findByCode(studentCode);
    }

    public java.util.List<Student> listStudents() {
        return repo.findAll();
    }

    public void updateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        repo.update(student);
    }

    public Student findById(int id) {
        return repo.findById(id);
    }

    public Student findByCode(String studentCode) {
        return repo.findByCode(studentCode);
    }

    // Add delete student functionality
    public boolean deleteStudent(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }

        try {
            // Check if student exists
            Student student = repo.findById(studentId);
            if (student == null) {
                return false;
            }

            // Delete the student
            repo.delete(studentId);
            return true;
        } catch (Exception e) {
            // Log the error in a real application
            return false;
        }
    }

    private String generateStudentCode(int id) {
        return String.format("STU%06d", id);
    }

    private String generateTempStudentCode() {
        // Generate a temporary unique code using timestamp
        long timestamp = System.currentTimeMillis();
        return String.format("TEMP%010d", timestamp % 10000000000L);
    }
}
