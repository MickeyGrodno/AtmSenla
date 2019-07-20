import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

class AtmCode {
    private final int MINUTES_PER_DAY = 1440;
    private final String CARD_LIST = "C:\\Users\\Sergei\\IdeaProjects\\AtmSenla\\src\\main\\resources\\CardList.csv";
    private DateFormat df = new SimpleDateFormat("MM.dd.yyyy.HH:mm:ss");
    private Scanner sc = new Scanner(System.in);

    HashMap<String, Card> cardsLoad() throws InterruptedException {
        HashMap<String, Card> cardsMap = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(CARD_LIST));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                String cardID = values[0];
                int pinCode = Integer.parseInt(values[1]);
                int balance = Integer.parseInt(values[2]);
                Date block;
                block = df.parse(values[3]);
                Card card = new Card(cardID, pinCode, balance, block);
                System.out.println("Номер карты " + cardID + " ПИН-код: " + pinCode);
                cardsMap.put(cardID, card);
            }
        } catch (IOException e) {
            System.out.println("Чтение из файла CardList.csv невозможно, завершение работы программы.");
            Thread.sleep(3000);
            throw new RuntimeException();
        } catch (ParseException e) {
            System.out.println("Дата в файле CardList.csv введена некорректно, завершение работы программы");
            Thread.sleep(3000);
            throw new RuntimeException();
        }
        System.out.println("Номер карты 7777 для оператора.");
        return cardsMap;
    }

    private void cardsSave(HashMap<String, Card> cardMap) throws InterruptedException {

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(CARD_LIST, false));
            StringBuilder forWrite = new StringBuilder();
            for (HashMap.Entry<String, Card> pair : cardMap.entrySet()) {
                String cardId = pair.getValue().getCardId();
                int pinСode = pair.getValue().getPinСode();
                int balance = pair.getValue().getBalance();
                Date block = pair.getValue().getBlock();
                forWrite.append(cardId).append(" ").append(pinСode).append(" ").append(balance).append(" ").append(df.format(block)).append("\n");
            }
            writer.write(forWrite.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Запись в файл CardList.csv невозможна, завершение работы программы.");
            Thread.sleep(3000);
            throw new RuntimeException();
        }
    }

    private Card insertCard(HashMap<String, Card> cards) throws InterruptedException {

        while (true) {
            System.out.println("Вставьте карту.");
            String entryCardID = sc.next();
            if (entryCardID.equals("7777")) {
                atmMenuAdmin();
            } else {
                if (cards.containsKey(entryCardID) && blockCheck(cards.get(entryCardID))) {
                    Card currentCard = cards.get(entryCardID);
                    if (pinCheck(currentCard)) {
                        return currentCard;
                    }
                } else if (!cards.containsKey(entryCardID)) {
                    System.out.println("Данная карта не поддерживается");
                }
            }
        }
    }

    boolean blockCheck(Card currentCard) {

        Date date = new Date();
        int lockoutMinuteTime;
        lockoutMinuteTime = (int) ((date.getTime() - currentCard.getBlock().getTime()) / 60000);

        if (lockoutMinuteTime < MINUTES_PER_DAY) {
            System.out.println("Ваша карта заблокирована");
            int hours;
            int min;
            hours = (MINUTES_PER_DAY - lockoutMinuteTime) / 60;
            min = (MINUTES_PER_DAY - lockoutMinuteTime) % 60;
            System.out.println(String.format("Карта будет разблокирована через %s ч., %s мин.", hours, min));
            return false;
        } else {
            return true;
        }
    }

    private boolean pinCheck(Card card) throws InterruptedException {
        for (int i = 3; ; i--) {
            if (i == 0) {
                System.out.println("Введен неверный пинкод 3 раза. Ваша карта заблокирована");
                Date date = new Date();
                card.setBlock(date);
                HashMap<String, Card> cards = cardsLoad();
                cards.put(card.getCardId(), card);
                cardsSave(cards);
                Thread.sleep(3000);
                return false;
            }

            System.out.println(String.format("Введите PIN-код карты. Осталось попыток %s", i));
            try {
                int pinCode;
                pinCode = sc.nextInt();

                if (pinCode == card.getPinСode()) {
                    return true;
                } else {
                    System.out.println("Неверный PIN-код.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Введен неверный формат пин-кода. Допустимы только числовые значения. Завершение работы программы.");
                Thread.sleep(3000);
                throw new RuntimeException();
            }
        }
    }

    void atmMenu() throws InterruptedException {
        while (true) {
            HashMap<String, Card> cards = cardsLoad();
            Card checkedCard = insertCard(cards);
            MoneyStorage moneyStorage = new MoneyStorage();

            while (true) {
                int cardMoney = checkedCard.getBalance();

                System.out.println("Выберите необходимую операцию");
                System.out.println("1 - баланс");
                System.out.println("2 - снятие наличных");
                System.out.println("3 - пополнение счета");
                System.out.println("0 - вернуть карту");

                try {
                    int menuNum = sc.nextInt();

                    if (menuNum == 0) {
                        checkedCard.setBalance(cardMoney);
                        cardsSave(cards);
                        System.out.println("Возьмите вашу карту");
                        Thread.sleep(3000);
                        break;
                    }
                    if (menuNum == 1) {
                        System.out.println(String.format("Баланс вашего счета %s р.", cardMoney));
                        Thread.sleep(3000);
                        continue;
                    }
                    if (menuNum == 2) {

                        noteInfo(moneyStorage);

                        System.out.println("Введите необходимую сумму");
                        int needMoney = sc.nextInt();
                        if (cardMoney >= needMoney) {
                            boolean isMoneyGiven = moneyStorage.giveMoney(needMoney);
                            if (!isMoneyGiven) {
                                Thread.sleep(3000);
                                continue;
                            }
                            cardMoney -= needMoney;
                            moneyStorage.saveMoneyStorage();
                            System.out.println(String.format("Выдана сумма %s р.", needMoney));
                            checkedCard.setBalance(cardMoney);
                            cards.put(checkedCard.getCardId(), checkedCard);
                            cardsSave(cards);
                            Thread.sleep(3000);
                        } else {
                            System.out.println("Недостаточно средств на счёте");
                            Thread.sleep(3000);
                        }
                        continue;
                    }

                    if (menuNum == 3) {
                        int fiveBill, tenBill, twentyBill;
                        System.out.println("Максимальная сумма пополнения не должна превышать 1000000р.");
                        System.out.println("Добавьте необходимое количество купюр в 5000р.");
                        fiveBill = sc.nextInt();
                        System.out.println("Добавьте необходимое количество купюр в 10000р.");
                        tenBill = sc.nextInt();
                        System.out.println("Добавьте необходимое количество купюр в 20000р.");
                        twentyBill = sc.nextInt();
                        int sum = fiveBill * 5000 + tenBill * 10000 + twentyBill * 20000;

                        if (sum <= 1000000) {
                            moneyStorage.updateMoneyStorage();
                            moneyStorage.putMoneyInMoneyStorage(fiveBill, tenBill, twentyBill);
                            checkedCard.setBalance(checkedCard.getBalance() + sum);
                            cards.put(checkedCard.getCardId(), checkedCard);
                            cardsSave(cards);
                            System.out.println(String.format("Баланс счета пополнен на %s р. и составляет %s р.", sum, checkedCard.getBalance()));
                        } else {
                            System.out.println(String.format("Введенная сумма %s р. превышает установленную сумму в 1000000 р.", sum));
                            System.out.println("Возврат средств.");
                        }
                        Thread.sleep(3000);
                    } else {
                        System.out.println("Неверный запрос");
                        Thread.sleep(3000);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Введен неверный формат запроса. Допустимы только числовые значения. Завершение работы программы.");
                    Thread.sleep(3000);
                    throw new RuntimeException();
                }
            }
        }
    }

    private void atmMenuAdmin() throws InterruptedException {
        MoneyStorage moneyStorage = new MoneyStorage();
        moneyStorage.updateMoneyStorage();

        while (true) {

            System.out.println("Выберите необходимую операцию:");
            System.out.println("1 - Добавить купюры в банкомат.");
            System.out.println("0 - Вернуть карту");
            try {
                int menuNum = sc.nextInt();
                if (menuNum == 1) {
                    int twentyBill = moneyStorage.getTwentyBill();
                    int tenBill = moneyStorage.getTenBill();
                    int fiveBill = moneyStorage.getFiveBill();

                    System.out.println(String.format("В наличии купюры номиналом 20000р %s щт. Добавьте необходимое количесво купюр", twentyBill));
                    moneyStorage.setTwentyBill(twentyBill + sc.nextInt());

                    System.out.println(String.format("В наличии купюры номиналом 10000р %s щт. Добавьте необходимое количесво купюр", tenBill));
                    moneyStorage.setTenBill(tenBill + sc.nextInt());

                    System.out.println(String.format("В наличии купюры номиналом 5000р %s щт. Добавьте необходимое количесво купюр", fiveBill));
                    moneyStorage.setFiveBill(fiveBill + sc.nextInt());

                    System.out.println(String.format("В наличии купюры 20000р %s шт., 10000р %s шт., 5000р %s шт., общей суммой %s р.", moneyStorage.getTwentyBill(), moneyStorage.getTenBill(), moneyStorage.getFiveBill(), moneyStorage.getBalance()));
                    moneyStorage.saveMoneyStorage();
                    Thread.sleep(3000);
                }
                if (menuNum == 0) {
                    System.out.println("Заберите карту");
                    Thread.sleep(3000);
                    break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Введен неверный формат запроса. Допустимы только числовые значения. Завершение работы программы.");
                Thread.sleep(3000);
                throw new RuntimeException();
            }
        }
    }

    private void noteInfo(MoneyStorage moneyStorage) {
        moneyStorage.updateMoneyStorage();
        System.out.println("В терменали имеются купюры номиналом:");
        if (moneyStorage.getTwentyBill() > 0) {
            System.out.print("20000р. ");
        }
        if (moneyStorage.getTenBill() > 0) {
            System.out.print("10000р. ");
        }
        if (moneyStorage.getFiveBill() > 0) {
            System.out.print("5000р. ");
        }
        System.out.println();
    }
}
