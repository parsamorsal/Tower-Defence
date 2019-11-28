package TowDef.Units.Intruders;

import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;

public class FireIntruder extends Intruder {
    private static final int DAMAGE = 4;

    public FireIntruder(PathCell cell, int pathLength) {
        super(cell, "Fi", pathLength);
        MOVING_TIME_STEP = 9;
        moneyIncrease = 30;
        hp = 100;//for example 100!
    }

    @Override
    public void specialAbility(int damage, Tower tower) {
        //isolate
        double rnd = Math.random();
        System.out.println(rnd);
        if (rnd > 0.2)
            hp += damage;
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }
}
