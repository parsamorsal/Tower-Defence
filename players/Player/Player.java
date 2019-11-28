package TowDef.players.Player;

import TowDef.Units.Castle;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {
    private String name;
    private Castle castle;
    private int score;
    private SimpleIntegerProperty money = new SimpleIntegerProperty();
    private String color;

    public Player(String name, Castle castle, int money, String color) {
        this.name = name;
        this.castle = castle;
        this.money.set(money);
        this.color = color;
    }

    public Castle getCastle() {
        return castle;
    }

    public int getMoney() {
        return money.get();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public SimpleIntegerProperty moneyProperty() {
        return money;
    }

    public void reduceMoney(int x) {
        money.set(money.get() - x);
    }

    public void addMoney(int x) {
        money.set(money.get() + x);
    }
}