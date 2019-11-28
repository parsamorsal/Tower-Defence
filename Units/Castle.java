package TowDef.Units;

import TowDef.Game.Game;
import TowDef.Map.MapCells.PathCell;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Castle {
    private SimpleIntegerProperty hp = new SimpleIntegerProperty(30);
    private PathCell cell;

    public Castle(PathCell cell) {
        this.cell = cell;
        cell.setCastle(this);
    }

    public SimpleIntegerProperty hpProperty() {
        return hp;
    }

    public void damage(int dmg) {
        this.hp.set(this.hp.get() - dmg);
        if (this.hp.get() < 1)
            Game.getInstance().end();
        System.out.println(this.hp.get());
    }
}
