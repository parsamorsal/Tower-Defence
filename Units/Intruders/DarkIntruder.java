package TowDef.Units.Intruders;

import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;

public class DarkIntruder extends Intruder {
    private static final int DAMAGE = 3;
    public DarkIntruder(PathCell cell, int pathLength) {
        super(cell, "Da", pathLength);
        MOVING_TIME_STEP = 12;
        moneyIncrease = 20;
        hp = 100;//for example 100!
    }

    @Override
    public void specialAbility(int damage, Tower tower) {
        //slow

        double rnd = Math.random();
        System.out.println(rnd);

        if(rnd > 0.4)
            tower.addExtraDelay(ADD_TIME_STEP);
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }
}
