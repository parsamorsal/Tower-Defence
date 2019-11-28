package TowDef.network;

import TowDef.Game.Game;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Faria on 7/14/2016.
 */
public class ServerGUI {
    public static final double SERVER_OPTIONS_HEIGHT = 1500;
    public static final double SERVER_OPTIONS_WIDTH = 1000;
    public static final int MAP_IMAGEVIEW_HEIGHT = 300;
    private Button submitButton;
    private Stage primaryStage;
    private Scene serverOptionScene;
    private Label mapNumberLabel;
    private ImageView slideShow;
    private int viewIngImageNumber = 0;
    private Server server;
    private TextField port;
    private TextField maxClients;
    private Label situation;
    private Thread thread;

    public ServerGUI(Stage primaryStage, Server server){
        this.primaryStage = primaryStage;
        this.server = server;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void showServerOptions(){

        if (serverOptionScene == null) buildServerOptions();

        System.gc();
        primaryStage.setScene(serverOptionScene);
    }
    public void buildServerOptions(){
        // Platform.runLater(()-> {


        VBox root = new VBox();
        root.setId("towerChooserPopup");
        root.getStylesheets().add("TowDef/GUI//popupCSS.css");

        serverOptionScene = new Scene(root, SERVER_OPTIONS_WIDTH, SERVER_OPTIONS_HEIGHT);

        Label info = new Label(
                "welcome to the server setting, please complete the server options!");
        info.setId("info");

        Button leftButton = new Button("<");
        leftButton.setId("navButton");

        slideShow = new ImageView(new Image(getClass().getResource(
                "/files/images/maps/" + viewIngImageNumber + ".gif"
        ).toString()));
        slideShow.setPreserveRatio(true);
        slideShow.setFitHeight(MAP_IMAGEVIEW_HEIGHT);
        slideShow.setId("mapSlideShow");

        Button rightButton = new Button(">");
        rightButton.setId("navButton");

        leftButton.setOnAction(event -> setImage("prev"));
        rightButton.setOnAction(event -> setImage("next"));

        HBox sliderWrapper = new HBox(leftButton, slideShow, rightButton);
        sliderWrapper.setId("lineWrapper");

        mapNumberLabel = new Label("map 0 over " + Game.MAPS_COUNT);

        Label maxClientsNumber = new Label("Maximum number of Clients ");
        maxClients = new TextField("2");
        HBox clientNumberWraper = new HBox(maxClientsNumber, maxClients);

        Label portNumber = new Label("Your portnumber: ");
        port = new TextField("1375");
        HBox portWraper = new HBox(portNumber,port);

        situation = new Label("");
        situation.setId("networkSituation");

        Button submitButton = new Button("whe're all done!");
        submitButton.setOnAction(event -> {
            server.getServerThread().setPortNumber(Integer.parseInt(port.getText()));
            server.getServerThread().setMaxClientsCount(Integer.parseInt(maxClients.getText()));
            server.getServerThread().setMapNumber(viewIngImageNumber);
            situation.setText("server is running");
            thread = new Thread(server.getServerThread());
            thread.start();


        });

        Button stopButton = new Button("stopServer");
        stopButton.setOnAction(event -> {

            situation.setText("");
            try {
                if(server != null){
                    server.stop();
                    if(server.getServerThread() != null) {
                        server.getServerThread().stop();
                        thread.stop();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }



        });

        root.getChildren().addAll(
                info,
                clientNumberWraper,
                portWraper,
                sliderWrapper,
                mapNumberLabel,
                situation,
                submitButton,
                stopButton

        );

        root.setOnKeyPressed(ke -> {
            if (ke.getCode().toString().equals("RIGHT")) setImage("next");
            if (ke.getCode().toString().equals("LEFT")) setImage("prev");
        });
    }

    private void setImage(String nextOrPrev) {
        switch (nextOrPrev) {
            case "next":
                if (viewIngImageNumber != Game.MAPS_COUNT - 1) {
                    mapNumberLabel.setText("map " + (++viewIngImageNumber + 1) + " over " + Game.MAPS_COUNT);
                    slideShow.setImage(new Image(getClass().getResource(
                            "/files/images/maps/" + viewIngImageNumber + ".gif"
                    ).toString()));
                }
                break;
            case "prev":
                if (viewIngImageNumber != 0) {
                    mapNumberLabel.setText("map " + (--viewIngImageNumber + 1) + " over " + Game.MAPS_COUNT);
                    slideShow.setImage(new Image(getClass().getResource(
                            "/files/images/maps/" + viewIngImageNumber + ".gif"
                    ).toString()));
                }
                break;
        }
    }

}
