package TowDef.players.AI;

import TowDef.Game.TimeLine;
import TowDef.Map.MapCells.PathCell;
import TowDef.Active;
import javafx.beans.property.SimpleStringProperty;

public class AI implements Active {
    private static final int TIME_STEP = 4;
    private PathCell[] beginning;
    private WaveReader wr = new WaveReader();
    private int time;
    private static AI ourInstance = new AI();
    private SimpleStringProperty nextWaveInfo = new SimpleStringProperty();

    public static AI getInstance() {
        return ourInstance;
    }

    private AI() {
        TimeLine.getInstance().addOtherActives(this);
    }

    //this method should be called in the Map class constructor.
    public void setMapBeginning(PathCell[] beginning) {
        this.beginning = beginning;
    }

    public SimpleStringProperty nextWaveInfoProperty() {
        return nextWaveInfo;
    }

    public void setNextWaveInfo(String nextWaveInfo) {
        this.nextWaveInfo.set(nextWaveInfo);
    }

    public void action() {
        //System.out.println("AI actioned!");
        time = (time + 1) % TIME_STEP;
        if (time == 0) {
            if (TimeLine.getInstance().isWaveDown()) {
                wr.BeginNextWave();
            }
            wr.SendNextLine(beginning);
        }
        /*Rectangle rectangle = new Rectangle(i * MAP_CELLS_WIDTH, j * MAP_CELLS_HEIGHT, MAP_CELLS_WIDTH, MAP_CELLS_HEIGHT);
        try {
            rectangle.setFill(new ImagePattern(new Image(getClass().getResource("/towers/0/2.gif").toURI().toURL().toString())));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        gameRoot.getChildren().addAll(rectangle);*/
    }
}
