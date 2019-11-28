package TowDef.Units.Towers;

import TowDef.GUI.GUI;
import TowDef.Map.MapCells.LandCell;
import TowDef.Units.Intruders.Intruder;

public class CasualTower extends Tower {
    private static final int BULLET_SPEED = 60;

    public static int[] costByLevel = new int[]{100, 100, 100};

    @Override
    public int getTimeStepByLevel(int index) {
        return timeStepByLevel[index];
    }

    @Override
    public int getRangeByLevel(int index) {
        return rangeByLevel[index];
    }

    @Override
    public int getDamageByLevel(int index) {
        return damageByLevel[index];
    }

    public static int[] rangeByLevel = new int[]{7, 10, 12};
    static int[] damageByLevel = new int[]{20, 8, 9};
    static int[] timeStepByLevel = new int[]{25, 17, 10};


    public int getCostByLevel(int index) {
        return costByLevel[index];
    }

    public CasualTower(LandCell cell) {
        super(cell);

        value = costByLevel[0];
        highPerformance = null;

    }

    @Override
    public void SpecialAbilityAttacking(Intruder choice, int damage) {
    }



    @Override
    public void fire(LandCell cell, Intruder choice, int damage) {
        GUI.getInstance().getGameGraphics().addBullet(cell,choice, "/files/images/bullet/12.png",damage,BULLET_SPEED);
    }
}
