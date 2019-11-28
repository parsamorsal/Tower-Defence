package TowDef.Units.Towers;


import TowDef.GUI.GUI;
import TowDef.Game.Game;
import TowDef.Game.TimeLine;
import TowDef.Map.MapCells.LandCell;
import TowDef.Map.MapCells.PathCell;
import TowDef.Map.Map;
import TowDef.Units.Intruders.Intruder;
import TowDef.Units.Unit;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Tower extends Unit {
    protected int level = 1;
    protected int value;
    protected int maximumLevelPossible = 3;
    private double extraStepTime = 0;
    private final int EXTRASTEPTIMECHANGE = 1;
    private ImageView imageView;
    private Class<? extends Tower> combinedWith = null;

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void addExtraDelay(int x) {
        extraStepTime += x;
    }

    public static Class<? extends Intruder> highPerformance;
    public static Class<? extends Intruder> lowPerformance;
    public static int rotationRate = 200;

    public ImageView getImageView() {
        return imageView;
    }

    private LandCell cell;
    private int time = 0;

    public Tower(LandCell cell) {
        super.cell = cell;
        this.cell = cell;
        TimeLine.getInstance().addTower(this);
    }

    public int getMaximumLevelPossible() {
        return maximumLevelPossible;
    }

    public abstract void SpecialAbilityAttacking(Intruder choice, int damage);

    public int getLevel() {
        return level;
    }

    public abstract int getCostByLevel(int index) ;
    public abstract int getDamageByLevel(int index) ;
    public abstract int getRangeByLevel(int index) ;
    public abstract int getTimeStepByLevel(int index) ;

    public LandCell getCell() {
        return cell;
    }

    public Intruder watch(Map map, int range) {
        //finiding all of intruders in the range of the Tower
        ArrayList<Intruder> nearIntruders = new ArrayList<>();
        for (int i = Math.max(super.cell.getX() - range - 1, 0); i < Math.min(super.cell.getX() + range, Game.getInstance().getMap().getXSize()); i++) {
            for (int j = Math.max(super.cell.getY() - range - 1, 0); j < Math.min(super.cell.getY() + range, Game.getInstance().getMap().getYSize()); j++) {

                int tempX = super.cell.getX() - i;
                int tempY = super.cell.getY() - j;
                if (tempX * tempX + tempY * tempY <= range * range) {
                    if (map.getContent()[i][j] instanceof PathCell) {
                        PathCell tempCell = (PathCell) (map.getContent()[i][j]);
                        if (tempCell.getContent() != null)
                            for (Intruder intruder : tempCell.getContent()) {
                                nearIntruders.add(intruder);

                            }
                    }
                }
            }
        }

        if (nearIntruders.isEmpty())
            return null;
        //finding the intruder which have the least HP
        ArrayList<Intruder> lessHpIntruders = new ArrayList<>();
        int tempHp = nearIntruders.get(0).getHp();
        for (Intruder intruder : nearIntruders) {
            if (intruder.getHp() < tempHp) {
                tempHp = intruder.getHp();
                lessHpIntruders.clear();
                lessHpIntruders.add(intruder);
            } else if (intruder.getHp() == tempHp) {
                lessHpIntruders.add(intruder);
            }
        }
        //finding the intruder which is nearest to the gate
        ArrayList<Intruder> nearGateIntruder = new ArrayList<>();
        int tempDistance = lessHpIntruders.get(0).getDistance();
        for (Intruder intruder : lessHpIntruders) {
            if (intruder.getDistance() < tempDistance) {
                tempDistance = intruder.getDistance();
                nearGateIntruder.clear();
                nearGateIntruder.add(intruder);
            } else if (intruder.getDistance() == tempDistance) {
                nearGateIntruder.add(intruder);
            }
        }

        Intruder choice = nearGateIntruder.get(0);
        return choice;





    }

    public void allAttackings(Intruder choice , int damage){
        damageByPerformance(choice, damage);
        SpecialAbilityAttacking(choice, damage);
        mergedSpecialAbility(choice,damage);
    }

    public void mergedSpecialAbility(Intruder choice,  int damage){
        if(combinedWith == null)
            return;
        try {
            Method specialAbility = combinedWith.getMethod("SpecialAbilityAttacking", Intruder.class, int.class);
            specialAbility.invoke(choice,damage);
        }catch (Exception e){e.getStackTrace();}

    }

    public void setCombinedWith(Class<? extends Tower> combinedWith) {
        this.combinedWith = combinedWith;
    }

    public void damageByPerformance(Intruder choice, int damage) {
        double damageFactor = 1;
        if(combinedWith != null){
            try {
                Field combinedHP = combinedWith.getDeclaredField("highPerformance");
                Field combinedLP = combinedWith.getDeclaredField("lowPerformance");
                if (choice.getClass().equals(combinedHP.get(null)) ){
                    damageFactor *= 2;
                } else if (choice.getClass().equals(combinedLP.get(null))){
                    damageFactor /= 2;
                }
            }catch (Exception e) {
                e.getStackTrace();
            }
        }
        if (choice.getClass().equals(highPerformance) ){
            damageFactor*=2;
        } else if (choice.getClass().equals(lowPerformance)){
            damageFactor/=2;
        }
//        GUI.getInstance().getGameGraphics().addBullet(cell,choice, "/files/images/bullet/3.gif",(int)(damage * damageFactor));
        fire(cell,choice,(int)(damage * damageFactor));


    }

    public abstract void fire(LandCell cell, Intruder choice, int damage);

    public void breakDown() {
        TimeLine.getInstance().removeTower(this);
        ((Group)imageView.getParent()).getChildren().remove(imageView);

        cell.setContent(null);
    }

    public void action() {
        if(time % (getTimeStepByLevel(level - 1) + extraStepTime) == 0) {
            Intruder choice = watch(Game.getInstance().getMap(), getRangeByLevel(level - 1));
            if(choice != null)
                allAttackings(choice,getDamageByLevel(level - 1));

        }
//        if(time % (getTimeStepByLevel(level - 1) + extraStepTime)/3 == 0) {
            Intruder choice = watch(Game.getInstance().getMap(), getRangeByLevel(level - 1));
            if(choice != null)
                GUI.getInstance().getGameGraphics().rotateTower(imageView,choice.getCell());
            if (extraStepTime > 0)
                extraStepTime -= EXTRASTEPTIMECHANGE;
//        }
        time++;


    }
    public void upgrade(){
        System.out.println("upgrade button 1clicked right");

        if (level == maximumLevelPossible) {
            GUI.getInstance().getGameGraphics().error("tower is fully upgraded!");
            System.out.println(("tower is fully upgraded!"));
            return;
        }
        if (Game.getInstance().getPlayers(0).getMoney() >= getCostByLevel(level)) {
            Game.getInstance().getPlayers(0).reduceMoney(getCostByLevel(level));
            value+=getCostByLevel(level);
            level++;
            String towerType = getClass().getName().replace("TowDef.Units.Towers.","");
            imageView.setImage(new Image(getClass().getResource("/files/images/towers/" + towerType + "/"+level+".gif").toString()));//check
            System.out.println("towerUpgraded");

        } else {
            GUI.getInstance().getGameGraphics().error("spend no more than what you have\n\t\t     \"Cyrus the Great\"");
            System.out.println("no money!");
        }
        return;
    }


    public Class<? extends Tower> getCombinedWith() {
        return combinedWith;
    }

    public int getValue() {
        return value;
    }

    public void merge(Class<? extends Tower> clazz , int cost){
        setCombinedWith(clazz);
        value += cost;
        System.out.println("towers merged");
    }
}

