package DB;

import Core.LoyaltyAccount;
import Core.Student;
import Interfaces.IStudentRepositor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StudentDAO implements IStudentRepositor {
    private static final Logger logger = Logger.getLogger(StudentDAO.class.getName());
    private final Connection conn;
    private final LoyaltyAccountDAO loyaltyAccountDAO;

    public StudentDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
        this.loyaltyAccountDAO = new LoyaltyAccountDAO(conn);
    }

    // Constructor for dependency injection
    public StudentDAO(Connection connection) {
        this.conn = connection;
        this.loyaltyAccountDAO = new LoyaltyAccountDAO(conn);
    }

    @Override
    public void save(Student student) {
        // First save the loyalty account and get its ID
        int loyaltyAccountId = loyaltyAccountDAO.save(student.getAccount());

        String sql = "INSERT INTO students (name, studentCode, loyaltyAccountId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getStudentCode());
            ps.setInt(3, loyaltyAccountId);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving student: " + student.getName(), e);
        }
    }

    @Override
    public void update(Student student) {
        String sql = "UPDATE students SET name=?, studentCode=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getStudentCode());
            ps.setInt(3, student.getId());
            ps.executeUpdate();

            // Also update the loyalty account
            loyaltyAccountDAO.update(student.getAccount());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating student with id: " + student.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting student with id: " + id, e);
        }
    }

    @Override
    public Student findById(int id) {
        String sql = "SELECT s.id, s.name, s.studentCode, s.loyaltyAccountId FROM students s WHERE s.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding student with id: " + id, e);
        }
        return null;
    }

    public Student findByStudentCode(String studentCode) {
        String sql = "SELECT s.id, s.name, s.studentCode, s.loyaltyAccountId FROM students s WHERE s.studentCode=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding student with code: " + studentCode, e);
        }
        return null;
    }

    @Override
    public Student findByCode(String code) {
        // Delegate to the existing method to avoid code duplication
        return findByStudentCode(code);
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT s.id, s.name, s.studentCode, s.loyaltyAccountId FROM students s";
        List<Student> students = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all students", e);
        }
        return students;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setStudentCode(rs.getString("studentCode"));

        // Get the loyalty account
        int loyaltyAccountId = rs.getInt("loyaltyAccountId");
        if (loyaltyAccountId > 0) {
            LoyaltyAccount account = loyaltyAccountDAO.findById(loyaltyAccountId);
            student.setAccount(account);
        } else {
            // Create a new loyalty account if none exists
            LoyaltyAccount account = new LoyaltyAccount();
            student.setAccount(account);
        }

        return student;
    }
}
