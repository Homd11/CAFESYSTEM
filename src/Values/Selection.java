package Values;

public class Selection {
    private int itemId;
    private int qty;

    public Selection(int itemId, int qty) {
        this.itemId = itemId;
        this.qty = qty;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQty(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "Selection{" +
                "itemId=" + itemId +
                ", qty=" + qty +
                '}';
    }
}
