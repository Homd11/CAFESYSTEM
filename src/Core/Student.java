package Core;

public class Student {
    private int id;
    private String studentCode;
    private String name;
    private LoyaltyAccount account;

    public Student() {
        this.account = new LoyaltyAccount();
    }

    public Student(String name, String studentCode) {
        this.name = name;
        this.studentCode = studentCode;
        this.account = new LoyaltyAccount();
    }

    public Student(int id, String name, String studentCode, LoyaltyAccount account) {
        this.id = id;
        this.name = name;
        this.studentCode = studentCode;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        if (studentCode == null || studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student code cannot be null or empty");
        }
        this.studentCode = studentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public LoyaltyAccount getAccount() {
        return account;
    }

    public void setAccount(LoyaltyAccount account) {
        this.account = account;
    }

    // Legacy getter for compatibility
    public LoyaltyAccount getLoyaltyAccount() {
        return account;
    }

    public void setLoyaltyAccount(LoyaltyAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentCode='" + studentCode + '\'' +
                ", name='" + name + '\'' +
                ", account=" + account +
                '}';
    }
}
