import lombok.Getter;
import lombok.Setter;

import java.io.*;
@Getter
@Setter
class MoneyStorage {

    private final String MONEY_STORAGE = "C:\\Users\\Sergei\\IdeaProjects\\AtmSenla\\src\\main\\resources\\moneyStorage.csv";
    private int fiveBill;
    private int tenBill;
    private int twentyBill;

    int getBalance() {
        return (fiveBill * 5000) + (tenBill * 10000) + (twentyBill * 20000);
    }

    void putMoneyInMoneyStorage( int fiveBill , int tenBill, int twentyBill) {
        setFiveBill(getFiveBill()+fiveBill);
        setTenBill(getTenBill()+tenBill);
        setTwentyBill(getTwentyBill()+twentyBill);
        saveMoneyStorage();
    }

    boolean giveMoney(int needMoney) {

        int countOfTwentyBill = needMoney / 20000;
        int moneyWithoutTwentyBills = needMoney - (countOfTwentyBill * 20000);
        int countOfTenBill = moneyWithoutTwentyBills / 10000;
        int moneyWithoutTwentyAndTenBills = moneyWithoutTwentyBills - (countOfTenBill * 10000);
        int countOfFiveBill = moneyWithoutTwentyAndTenBills / 5000;
        int diff;

        if (needMoney > getBalance()) {
            System.out.println("Недостаточно средств в терминале");
            return false;
        }
        if (needMoney % 5000 != 0) {
            System.out.println("Отсутствуют необходимые купюры");
            return false;
        }
        if (this.twentyBill >= countOfTwentyBill) {
            this.twentyBill -= countOfTwentyBill;
        }
        else {
            diff = countOfTwentyBill - this.twentyBill;
            countOfTenBill += 2 * diff;
        }
        if (this.tenBill >= countOfTenBill) {
            this.tenBill -= countOfTenBill;
        } else {
            diff = countOfTenBill - this.tenBill;
            countOfFiveBill += 2 * diff;
        }
        if (this.fiveBill >= countOfFiveBill) {
            this.fiveBill -= countOfFiveBill;
        }
        else {
            System.out.println("Отсутствуют необходимые купюры");
            return false;
        }
        saveMoneyStorage();
        return true;
    }

    void updateMoneyStorage() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(MONEY_STORAGE));
            String line = reader.readLine();
            String[] values = line.split(" ");
            this.fiveBill = Integer.parseInt(values[0]);
            this.tenBill = Integer.parseInt(values[1]);
            this.twentyBill = Integer.parseInt(values[2]);
        } catch (FileNotFoundException e) {
            System.out.println("Невозможно открыть файл moneyStorage.csv, завершение работы программы.");
            throw new RuntimeException();
        } catch (IOException e) {
            System.out.println("Невозможно произвести чтение из файла moneyStorage.csv, завершение работы программы.");
            throw new RuntimeException();
        }
    }

    void saveMoneyStorage() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(MONEY_STORAGE, false));
        writer.write(this.getFiveBill() + " " + this.getTenBill() + " " + this.getTwentyBill());
        writer.flush();
        writer.close();
    } catch (IOException e) {
            System.out.println("Невозможно произвести запись в файл moneyStorage.csv, завершение работы программы.");
            throw new RuntimeException();
        }
    }
}
