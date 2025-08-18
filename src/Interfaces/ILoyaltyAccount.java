package Interfaces;

import Core.LoyaltyAccount;

public interface ILoyaltyAccount {
    public int save(LoyaltyAccount account);
    public   void update(LoyaltyAccount account);
    public LoyaltyAccount findById(int id);
    public void delete(int id);
}
