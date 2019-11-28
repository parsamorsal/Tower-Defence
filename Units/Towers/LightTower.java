package TowDef.Units.Towers;

import TowDef.GUI.GUI;
import TowDef.Map.MapCells.LandCell;
import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Intruders.Intruder;
import TowDef.Game.Game;
import TowDef.Units.Intruders.LightIntruder;
import TowDef.Units.Intruders.TreeIntruder;

import java.util.LinkedList;

public class LightTower extends Tower {
    private static final int BULLET_SPEED = 20;
    public static int[] costByLevel = new int[]{100, 100, 100};
    static int[] rangeByLevel = new int[]{7, 10, 12};
    static int[] damageByLevel = new int[]{20, 8, 9};
    static int[] timeStepByLevel = new int[]{10, 4, 3};

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
    public LightTower(LandCell cell) {
        super(cell);
        value = costByLevel[0];
        highPerformance = LightIntruder.class;
        lowPerformance = TreeIntruder.class;
    }

    public int getCostByLevel(int index) {
        return costByLevel[index];
    }

    @Override
    public void SpecialAbilityAttacking(Intruder choice, int damage) {
        //splash


        //geting the intruders near selected one
        LinkedList<Intruder> near1 = new LinkedList<>();
        LinkedList<Intruder> near2 = new LinkedList<>();
        LinkedList<Intruder> near3 = new LinkedList<>();
        LinkedList<Intruder> near4 = new LinkedList<>();
        if(Game.getInstance().getMap().getContent()[choice.getCell().getX() - 1][choice.getCell().getY()] instanceof PathCell){
            PathCell temp =(PathCell) Game.getInstance().getMap().getContent()[choice.getCell().getX() - 1][choice.getCell().getY()];
            near1 = temp.getContent();
        }

        if(Game.getInstance().getMap().getContent()[choice.getCell().getX() + 1][choice.getCell().getY()] instanceof PathCell){
            PathCell temp =(PathCell) Game.getInstance().getMap().getContent()[choice.getCell().getX() + 1][choice.getCell().getY()];
            near2 = temp.getContent();
        }

        if(Game.getInstance().getMap().getContent()[choice.getCell().getX()][choice.getCell().getY() - 1] instanceof PathCell){
            PathCell temp =(PathCell) Game.getInstance().getMap().getContent()[choice.getCell().getX()][choice.getCell().getY() - 1];
            near3 = temp.getContent();
        }

        if(Game.getInstance().getMap().getContent()[choice.getCell().getX()][choice.getCell().getY() + 1] instanceof PathCell){
            PathCell temp =(PathCell) Game.getInstance().getMap().getContent()[choice.getCell().getX()][choice.getCell().getY() + 1];
            near4 = temp.getContent();
        }

        for(Intruder intruder: near1){
            intruder.damage(damage * 4 / 10, this);
        }
        for(Intruder intruder: near2){
            intruder.damage(damage * 4 / 10, this);
        }
        for(Intruder intruder: near3){
            intruder.damage(damage * 4 / 10, this);
        }
        for(Intruder intruder: near4){
            intruder.damage(damage * 4 / 10, this);
        }


    }
    public void fire(LandCell cell, Intruder choice, int damage) {
//        GUI.getInstance().getGameGraphics().laserAnimation(cell,choice, "/files/images/bullet/3.gif",damage,BULLET_SPEED);
        GUI.getInstance().getGameGraphics().addBullet(cell,choice, "/files/images/bullet/3.gif",damage,BULLET_SPEED);
    }
}
