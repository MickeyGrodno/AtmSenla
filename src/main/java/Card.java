import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
public class Card {

    private String cardId;
    private int pinСode;
    private int balance;
    private Date block;
}
