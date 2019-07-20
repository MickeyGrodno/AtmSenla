import org.junit.Test;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class MoneyStorageTest {
    @Test
    public void giveMoneyPositiveTest() {
        MoneyStorage moneyStorage = new MoneyStorage();
        moneyStorage.updateMoneyStorage();
        int needMoney = 100000;
        boolean isMoneyEnough = moneyStorage.giveMoney(needMoney);
        assertTrue(isMoneyEnough);
    }

    @Test
    public void giveMoneyNegativeTest() {
        MoneyStorage moneyStorage = new MoneyStorage();
        moneyStorage.updateMoneyStorage();
        int needMoney = 100000000;
        boolean isMoneyEnough = moneyStorage.giveMoney(needMoney);
        assertFalse(isMoneyEnough);
    }
}
