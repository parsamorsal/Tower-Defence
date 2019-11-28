package TowDef.Units.Intruders;

import TowDef.GUI.GUI;
import TowDef.Game.Game;
import TowDef.Game.TimeLine;
import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Towers.Tower;
import TowDef.Units.Unit;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public abstract class Intruder extends Unit {
    public static final int SMOOTHING_MOVING_STEP = 2; // so it will move towards the middle of 2 cells next and 2 prev! its better not to change it! its not so flexible!
    private static final int BACKFIRE_DAMAGE = 5;
    private static final int BACKFIRE_STEP = 5;
    private static final double DELAY_FACTOR_STEP = 1.05;
    public static final double DELAY_FACTOR_RISE_STEP = 1.1;
    protected static int ADD_TIME_STEP = 5;
    private static final int STUN_STEP = 20;
    private int distance = 0;
    protected PathCell cell;
    protected int MOVING_TIME_STEP;//check -> should be final
    protected int hp;
    protected int delay;
    private int stunned = 0;
    private int backFire1 = 0;
    private int backFire2 = 0;
    private ImageView imgView;
    private int time = 0;
    protected double delayFactor = 1;
    private double currentDirectionAngle;
    protected static int moneyIncrease;

    /**
     * @param cell     is the initial cell of the intruder in map.
     * @param typeCode is one of the following:
     *                 Ca:CasualIntruder,
     *                 Da:DarkIntruder,
     *                 Fi:FireIntruder,
     *                 Li:LightIntruder,
     *                 Tr:TreeIntruder
     */
    public Intruder(PathCell cell, String typeCode, int pathLength) {
        distance = pathLength;
        this.cell = cell;
        currentDirectionAngle = 90 * cell.xDir() + 180 * cell.yDir();
        TimeLine.getInstance().addIntruder(this);
        try {
            imgView = GUI.getInstance().getGameGraphics().addIntruder(cell,
                    getClass().getResource("/files/images/Intruders/" + typeCode + ".gif").toURI().toURL().toString());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
//        System.out.println("a new " + typeCode + " came in from " + cell);
    }

    public abstract int getDamage();

    public int getDistance() {
        return distance;
    }

    public int getHp() {
        return hp;
    }

    public void damage(int dmg, Tower tower) {
        hp -= dmg;
        //System.out.println("damage to intruder at: " + cell.getX() + ", " + cell.getY() + " hp: " + hp);
        specialAbility(dmg, tower);
        if (hp < 0)
            die();
    }

    public void damage(int dmg) {
        hp -= dmg;
        if (hp < 0)
            die();
    }

    public void slow() {
        delayFactor *= DELAY_FACTOR_RISE_STEP;
    }

    public abstract void specialAbility(int damage, Tower tower);


    public void stun() {
        stunned = STUN_STEP;
    }

    public void innerFire() {
        backFire1 = BACKFIRE_STEP;
    }

    public PathCell getCell() {
        return cell;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public PathCell setCell(PathCell cell) {
        return this.cell = cell;
    }

    public void die() {
        this.getCell().getContent().remove(this);
        TimeLine.getInstance().removeIntruder(this);
        Game.getInstance().getPlayers(0).addMoney(moneyIncrease);
        GUI.getInstance().getGameGraphics().removeIntruder(imgView);
        //System.out.println("death of intruder at: " + cell + " hp: " + hp);
    }

    public void action() {
        time = (time + 1) % ((int)((MOVING_TIME_STEP) * delayFactor));
        if (stunned > 0)
            stunned -= 1;
        else {
            if (backFire1 > 0) {
                backFire1 -= 1;
                damage(BACKFIRE_DAMAGE);
                if (backFire1 == 0) {
                    backFire2 = BACKFIRE_STEP;
                }
            } else if (backFire2 > 0) {
                backFire2 -= 1;
                damage(BACKFIRE_DAMAGE);
            }
            if (delayFactor < 1) {
                delayFactor *= DELAY_FACTOR_STEP;
            } else if (delayFactor > 1) {
                delayFactor /= DELAY_FACTOR_STEP;
            }
            if (time == 0) {
                moveForward();
            }
        }
    }

    public ImageView getImgView() {
        return imgView;
    }

    /**
     * well... the following function is crappy!
     * what it does is it calculates the weighted average
     * of the two previous and two next cells and then the
     * intruder moves towards that point.
     * the formula is smth like:
     * (prevOfPrev + prev * 2 + next * 2 + nextOfNext)/6
     * where the current cell is actually the next cell to
     * where the intruder is at the moment.
     * but its not exactly like that, in the beginning:
     * prevOfPrev = prev = current position so the average
     * will tend a little to front so the intruder kinda
     * jump starts to the map, and at the end since the
     * next of end should be itself, a temporary cell is
     * made in the map in which the castle exists and is
     * set as the next of all mapEnd cells, and for the
     * moving towards that cell, the program continues
     * the direction it was going to and assumes some
     * imaginary cells after map end.
     * this part is not flexible and you can not change the
     * SMOOTHING_MOVING_STEP variable!
     * the whole thing is to have the intruders turn smoothly
     * and not so sharp, in the corners.
     */
    public void moveForward() {
        if (cell.getX() == 5 && cell.getY() == 33) {
            int sex = 1;
        }
        //System.out.print("Intruder mover from :" + cell);
        if (distance == 0) {
            this.cell.getCastle().damage(getDamage());
            die();
            return;
        }
        double[] temp = new Object() {
            double[] calcNormalized(PathCell cell) {
                PathCell temp = cell;
                double x = 0, y = 0;
                if (cell != cell.getPrev()) {
                    x += temp.getX() + temp.getPrev().getX();
                    y += temp.getY() + temp.getPrev().getY();
                } else {
                    x += temp.getX() + (temp.getX() - temp.xDir());
                    y += temp.getY() + (temp.getY() - temp.yDir());
                }
                for (int i = 0; i < SMOOTHING_MOVING_STEP; i++) {
                    if (temp.getNext() != Game.getInstance().getMap().getOut()) {
                        temp = temp.getNext();
                        x += temp.getX();
                        y += temp.getY();
                    } else {
                        x += (temp.getX() + temp.getPrev().xDir() * (i + 1));
                        y += (temp.getY() + temp.getPrev().yDir() * (i + 1));
                    }
                }
                return new double[]{x, y};
            }

            double[] calcTranslate(PathCell cell) {
                double xTransition;
                double yTransition;
                //if (cell != cell.getPrev()) {
                    xTransition = (calcNormalized(cell)[0] - calcNormalized(cell.getPrev())[0]) / 4d;//4 = 1 + 1 + 1 + 1
                    yTransition = (calcNormalized(cell)[1] - calcNormalized(cell.getPrev())[1]) / 4d;
                //} else {
                 //   xTransition = cell.xDir();
                 //   yTransition = cell.yDir();
                //}
                return new double[]{xTransition, yTransition, calcRotation(xTransition, yTransition)};
            }

            double calcRotation(double x, double y) {
                if (x == 0 && y == 0) return 0;
                if (x == 0)
                    return ((y > 0 ? 180 : 0) - currentDirectionAngle + 540) % 360 - 180;
                if (y == 0)
                    return ((x > 0 ? 90 : 270) - currentDirectionAngle + 540) % 360 - 180;
                return (((x > 0 ? 0 : -180) + Math.toDegrees(Math.atan(y / x)) + 90 - currentDirectionAngle) + 540) % 360 - 180;
            }
        }.calcTranslate(cell);
        GUI.getInstance().getGameGraphics().moveIntruderTowards(
                imgView,
                temp[0],
                temp[1],
                temp[2],
                MOVING_TIME_STEP
        );
        currentDirectionAngle = (currentDirectionAngle + temp[2]) % 360;
        cell.getContent().remove(this);
        cell = cell.getNext();
        cell.addContent(this);
        distance--;
        //System.out.println(" to " + cell + ", transition = (" + temp[0] + ", " + temp[1] + ")");
    }
}
