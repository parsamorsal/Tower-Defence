package TowDef.Units.Intruders;

import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class TreeIntruder extends Intruder {
    private static final int DAMAGE = 3;

    public TreeIntruder(PathCell cell, int pathLength) {
        super(cell, "Tr", pathLength);
        MOVING_TIME_STEP = 14;
        moneyIncrease = 50;
        hp = 100;//for example 100!
    }

    @Override
    public void specialAbility(int damage, Tower tower) {
        //death
        double rnd = Math.random();
        System.out.println(rnd);

        if (rnd > 0.05)
            tower.breakDown();
    }


    @Override
    public int getDamage() {
        return DAMAGE;
    }
}
