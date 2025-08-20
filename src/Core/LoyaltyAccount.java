package Core;

public class LoyaltyAccount {
    private int id;
    private int points;

    public LoyaltyAccount() {
        this.points = 0; // starts from zero
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    // Method from class diagram
    public int balance() {
        return points;
    }

    // Method from class diagram
    public void add(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.points += points;
    }

    // Method from class diagram
    public void deduct(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        if (this.points < points) {
            throw new IllegalArgumentException("Insufficient points. Available: " + this.points + ", Required: " + points);
        }
        this.points -= points;
    }

    // Legacy methods for compatibility
    public void addPoints(int amount) {
        add(amount);
    }

    public boolean deductPoints(int amount) {
        try {
            deduct(amount);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPoints(int points) {
        if (points >= 0) {
            this.points = points;
        } else {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{" +
                "id=" + id +
                ", points=" + points +
                '}';
    }


}
