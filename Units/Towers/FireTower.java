package TowDef.Units.Towers;

import TowDef.GUI.GUI;
import TowDef.Map.MapCells.LandCell;
import TowDef.Units.Intruders.Intruder;
import TowDef.Units.Intruders.LightIntruder;
import TowDef.Units.Intruders.TreeIntruder;

public class FireTower extends Tower{
    private static final int BULLET_SPEED = 20;

    public static int[] costByLevel = new int[]{100, 100, 100};
    static int[] rangeByLevel = new int[]{7, 10, 12};
    static int[] damageByLevel = new int[]{20, 8, 9};
    static int[] timeStepByLevel = new int[]{10, 4, 3};
    public FireTower(LandCell cell) {
        super(cell);

        value = costByLevel[0];

        highPerformance = TreeIntruder.class;
        lowPerformance = LightIntruder.class;

    }

    public int getCostByLevel(int index) {
        return costByLevel[index];
    }
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

    @Override
    public void SpecialAbilityAttacking(Intruder target, int damage) {
        //innerfire
        target.innerFire();
    }

    @Override
    public void fire(LandCell cell, Intruder choice, int damage) {
        GUI.getInstance().getGameGraphics().addBullet(cell,choice, "/files/images/bullet/15.gif",damage,BULLET_SPEED);
    }
}
