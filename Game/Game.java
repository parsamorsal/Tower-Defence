package TowDef.Game;

import TowDef.GUI.GUI;
import TowDef.Main;
import TowDef.Map.Map;
import TowDef.Map.MapCells.LandCell;
import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Castle;
import TowDef.Units.Towers.*;
import TowDef.players.Player.Player;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class Game {
    private static Game ourInstance = new Game();
    public static final int DEFAULT_MAP_NUMBER = 0;
    public static final int MAPS_COUNT = 6;
    public static final int PLAYER_INITIAL_MONEY = 500;
    public static final ArrayList<String> TOWER_NAMES =
            new ArrayList<>(Arrays.asList("CasualTower", "DarkTower", "FireTower", "LightTower", "TreeTower"));

    private Map map;
    private SimpleStringProperty highScoreLabelText;
    private SimpleDoubleProperty gameRate = new SimpleDoubleProperty(1);
    //private SimpleStringProperty mapFileAddress = new SimpleStringProperty("");
    private SimpleDoubleProperty soundVolume = new SimpleDoubleProperty(1);
    private SimpleBooleanProperty isSoundMuted = new SimpleBooleanProperty(true);
    private int lastHighScore;
    private Vector<Player> players = new Vector<>();

    {
        try {
            lastHighScore = new Scanner(new File(Main.class.getResource("/files/settings/highScore.txt").toURI())).nextInt();
            highScoreLabelText = new SimpleStringProperty("highscore: " + ((Integer) (lastHighScore)).toString());
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Game() {
    }

    public static Game getInstance() {
        return ourInstance;
    }

    public void init(String playerName, String color) {
        Game.getInstance().addPlayer(playerName, PLAYER_INITIAL_MONEY, color);
    }

    public SimpleDoubleProperty gameRateProperty() {
        return gameRate;
    }

    public String getHighScoreLabelText() {
        return highScoreLabelText.get();
    }

    public SimpleStringProperty highScoreLabelTextProperty() {
        return highScoreLabelText;
    }

    public SimpleDoubleProperty soundVolumeProperty() {
        return soundVolume;
    }

    public SimpleBooleanProperty isSoundMutedProperty() {
        return isSoundMuted;
    }

    public Map setMap(Map map) {
        return this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void addPlayer(String playerName, int initialMoney, String color) {
        players.add(new Player(playerName, new Castle(map.getOut()), initialMoney, color));
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Player getPlayers(int i) {
        return players.get(i);
    }

    public void mapClicked(int x, int y, String selectedButton) {
        if (map.getContent()[x][y] instanceof PathCell) {
            switch (selectedButton) {
                case ("upgrade"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to upgrade it.");
                    return;
                case ("sell"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to sell it.");
                    return;
                case ("repair"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to repair it.");
                    return;
                default:
                    GUI.getInstance().getGameGraphics().error("you can't built towers on the road!");
                    return;
            }

        }
        Tower tower = ((LandCell) map.getContent()[x][y]).getContent();
        if (tower == null) {
            switch (selectedButton) {
                case ("upgrade"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to upgrade it.");
                    return;
                case ("sell"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to sell it.");
                    return;
                case ("repair"):
                    GUI.getInstance().getGameGraphics().error("click on a tower to repair it.");
                    return;
                default:
                    try {
                        Class<?> clazz = Class.forName("TowDef.Units.Towers." + selectedButton);
                        Field costField = clazz.getDeclaredField("costByLevel");
                        if (players.get(0).getMoney() >= ((int[]) costField.get(null))[0]) {//_CHECK_
                            Constructor<?> constructor = clazz.getConstructor(LandCell.class);
                            Tower newTower = (Tower) constructor.newInstance((LandCell) map.getContent()[x][y]);
                            ((LandCell) map.getContent()[x][y]).setContent(newTower);
                            players.get(0).reduceMoney(((int[]) costField.get(null))[0]);
                            newTower.setImageView(GUI.getInstance().getGameGraphics().addTower((LandCell) map.getContent()[x][y], selectedButton));
                            GUI.getInstance().getGameGraphics().showTowerRange(x,y,newTower.getRangeByLevel(0));
                        } else {
                            GUI.getInstance().getGameGraphics().error("spend no more than what you have\n\t\t     \"Cyrus the Great\"");
                            return;
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return;
            }
        } else {
            switch (selectedButton) {
                case ("upgrade"):
                    tower.upgrade();
                    return;
                case ("sell"):
                    players.get(0).addMoney(tower.getValue() / 2);
                    tower.breakDown();
                    System.out.println("tower sold");
                    return;
                case ("repair"):
                    //tower.repair();
                    return;
                default:
                    System.out.println("merging");
                    if (tower.getCombinedWith() != null) {
                        GUI.getInstance().getGameGraphics().error("tower is already merged!");
                        System.out.println("tower is already merged!");
                        return;
                    }
                    if (tower.getClass().getName().equals("TowDef.Units.Towers." + selectedButton)) {
                        GUI.getInstance().getGameGraphics().error("you cannot merge a tower with it self!");
                        System.out.println("you cannot merge a tower with it self!");
                        return;
                    }
                    if (tower.getClass().getName().equals("TowDef.Units.Towers." + "CasualTower")) {
                        GUI.getInstance().getGameGraphics().error("you cannot merge a casual tower!");
                        System.out.println("you cannot merge a tower with it self!");
                        return;
                    }
                    try {
                        Class<?> clazz = Class.forName("TowDef.Units.Towers." + selectedButton);
                        Field costField = clazz.getDeclaredField("costByLevel");
                        if (players.get(0).getMoney() >= ((int[]) costField.get(null))[0]) {//_CHECK_
                            tower.merge((Class<? extends Tower>) clazz, ((int[]) costField.get(null))[0]);
                            players.get(0).reduceMoney(((int[]) costField.get(null))[0]);
                        } else {
                            GUI.getInstance().getGameGraphics().error("spend no more than what you have\n\t\t     \"Cyrus the Great\"");
                            System.out.println("spend no more than what you have\n\t\t     \"Cyrus the Great\"");
                        }
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //tower.merge(selectedButton);
            }
        }


    }

    public void begin() {
        System.out.println("game began!");
        TimeLine.getInstance().begin();
    }

    public void end() {
        TimeLine.getInstance().end();
        GUI.getInstance().getGameGraphics().gameOver();
        System.out.println("game over");
    }

    public void saveHighScore() {
        if (Integer.parseInt(highScoreLabelText.get().substring("highscore: ".length())) != lastHighScore) {
            try {
                FileWriter writer = new FileWriter(new File("src\\highScore.txt"));
                writer.write(highScoreLabelText.get().substring("highscore: ".length()));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
