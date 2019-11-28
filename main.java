package TowDef;

import TowDef.GUI.GUI;
import TowDef.Game.Game;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Main extends Application {
    public static void main() {launch();}

    @Override
    public void start(Stage primaryStage) {
        GUI.getInstance().init(primaryStage);
        GUI.getInstance().getMainMenuGraphics().showMainMenu();
        primaryStage.show();
    }
}
