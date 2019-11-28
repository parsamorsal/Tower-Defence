package TowDef.Units.Intruders;

import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;

public class LightIntruder extends Intruder {
    private static final int DAMAGE = 1;

    public LightIntruder(PathCell cell, int pathLength) {
        super(cell, "Li",  pathLength);
        MOVING_TIME_STEP = 8;
        moneyIncrease = 40;

        hp = 100;//for example 100!
    }

    @Override
    public void specialAbility(int damage, Tower tower) {
        //haste
        double rnd = Math.random();
        System.out.println(rnd);

        if (rnd > 0.4)
            delayFactor /= DELAY_FACTOR_RISE_STEP;
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }
}
