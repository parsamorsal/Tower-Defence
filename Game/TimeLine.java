package TowDef.Game;

import TowDef.Active;
import TowDef.GUI.GUI;
import TowDef.Units.Intruders.Intruder;
import TowDef.Units.Towers.Tower;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Vector;

public class TimeLine {
    public static double TIMELINE_DELAY = 50;//milli seconds

    private static TimeLine ourInstance = new TimeLine();
    public SimpleDoubleProperty timelineRate = new SimpleDoubleProperty(1);

    public static TimeLine getInstance() {
        return ourInstance;
    }

    private TimeLine() {
    }

    private Vector<Intruder> intrudersList = new Vector<>();
    private Vector<Active> otherActivesList = new Vector<>();
    private Vector<Tower> towersList = new Vector<>();
    Timeline timeline;

    public void begin() {
        System.out.println("TimeLine began working!");
        timeline = new Timeline(
                new KeyFrame(Duration.millis(TIMELINE_DELAY), event -> {
                    intrudersList.forEach(Intruder::action);
                    towersList.forEach(Tower::action);
                    otherActivesList.forEach(Active::action);
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.rateProperty().bind(Game.getInstance().gameRateProperty());
        timeline.play();
        //while(Game.getInstance().)
    }

    public void pause() {
        timeline.pause();
    }

    public void resume() {
        timeline.play();
    }

    public void end() {
        timeline.stop();
    }

    public void newGame() {
        ourInstance = new TimeLine();
    }

    public boolean isWaveDown() {
        return intrudersList.size() == 0;
    }

    public void addIntruder(Intruder intruder) {
        intrudersList.add(intruder);
    }

    public void addIntruder(ArrayList<Intruder> intrudersList) {
        this.intrudersList.addAll(intrudersList);
    }

    public void removeIntruder(Intruder intruder) {
        Platform.runLater(() -> intrudersList.remove(intruder));
    }

    public void removeActive(Active active) {
        Platform.runLater(() -> otherActivesList.remove(active));
    }


    public void addTower(Tower tower) {
        towersList.add(tower);
    }

    public void removeTower(Tower tower) {
        Platform.runLater(() -> towersList.remove(tower));
    }

    public void addOtherActives(Active active) {
        otherActivesList.add(active);
    }
}
