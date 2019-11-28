package TowDef.Units.Intruders;

import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;

public class CasualIntruder extends Intruder {
    private static final int DAMAGE = 2;

    public CasualIntruder(PathCell cell, int pathLength) {
        super(cell, "Ca",  pathLength);
        MOVING_TIME_STEP = 10;
        moneyIncrease = 10;
        hp = 100;//for example 100!
    }

    @Override
    public void specialAbility(int damage, Tower tower) {
        //do nothing
    }


    @Override
    public int getDamage() {
        return DAMAGE;
    }
}
