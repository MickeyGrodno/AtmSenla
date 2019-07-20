import org.junit.Test;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AtmCodeTest {

    @Test
    public void cardsLoadTest() throws InterruptedException {
        AtmCode atmCode = new AtmCode();
        HashMap<String, Card> cardsMap = atmCode.cardsLoad();
        assertTrue(cardsMap != null );
        assertEquals(3, cardsMap.keySet().size());

    }
    @Test
    public void blockCheckNegativeTest() {
        AtmCode atmCode = new AtmCode();
        Card card = new Card("65161dsd", 10, 12333, new Date());
        boolean isOk = atmCode.blockCheck(card);
        assertFalse(isOk);
    }
    @Test
    public void blockCheckPositiveTest() {
        AtmCode atmCode = new AtmCode();
        Date date = new Date();
        date.setTime(1000000);
        Card card = new Card("65161dsd", 10, 12333, date);
        boolean isOk = atmCode.blockCheck(card);
        assertTrue(isOk);
    }
}