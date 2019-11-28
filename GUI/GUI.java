package TowDef.GUI;

import TowDef.Game.Game;
import TowDef.Game.TimeLine;
import TowDef.Map.MapCells.LandCell;
import TowDef.Map.MapCells.PathCell;
import TowDef.Map.MapReader;
import TowDef.Units.Intruders.Intruder;
import TowDef.Units.Towers.Bullet;
import TowDef.Units.Towers.Tower;
import TowDef.network.Client;
import TowDef.network.Server;
import TowDef.players.AI.AI;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import sun.security.krb5.internal.KdcErrException;

import java.util.ArrayList;

public class GUI {
    public static final double MAIN_MENU_HEIGHT = 650;
    public static final double MAIN_MENU_WIDTH = 500;
    public static final double GAME_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    public static double MAP_CELLS_HEIGHT;
    public static double MAP_CELLS_WIDTH;
    public static final double TOWER_IMAGES_HEIGHT = 50;
    public static final int MAP_PREVIEW_HEIGHT = 40;
    public static final int POPUP_IMAGEVIEW_HEIGHT = 500;
    public static final int RIGHT_MENU_ICONS_HEIGHT = 40;
    private Client client;

    private static GUI ourInstance = new GUI();
    private Stage primaryStage;
    private GameGraphics gameGraphics = new GameGraphics();
    private MainMenuGraphics mainMenuGraphics = new MainMenuGraphics();

    private MediaPlayer playingMediaPlayer = null;
    private MediaPlayer menuMediaPlayer = null;
    private MediaPlayer gameMediaPlayer = null;
    private MediaPlayer gameOverMediaPlayer = null;

    public static GUI getInstance() {
        return ourInstance;
    }

    private GUI() {
    }

    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setMinHeight(MAIN_MENU_HEIGHT);
        primaryStage.setTitle("TowDef");

        menuMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/files/media/song.mp3").toString()));
        menuMediaPlayer.muteProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());
        menuMediaPlayer.volumeProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());

        gameMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/files/media/getLow.mp3").toString()));
        gameMediaPlayer.muteProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());
        gameMediaPlayer.volumeProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());

        gameOverMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/files/media/gameOver.mp3").toString()));
        gameOverMediaPlayer.muteProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());
        gameOverMediaPlayer.volumeProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());

        playingMediaPlayer = menuMediaPlayer;
    }

    public class MainMenuGraphics {
        private int chosenMapNumber = Game.DEFAULT_MAP_NUMBER;
        private Popup towerChooserPopup = new Popup();
        private Popup multiPlayerPopup = new Popup();

        private boolean isSubmit = false;

        private Scene mainMenuScene;
        private ImageView mapPreview = null;
        private int viewIngImageNumber = 0;
        private ImageView slideShow = null;
        private Label mapNumberLabel;
        private ComboBox<String> colorComboBox;
        private TextField nameField;

        public void buildMainMenu() {
            if (towerChooserPopup.getContent().size() == 0) buildTowerChooserPopup();
            if (multiPlayerPopup.getContent().size() == 0) buildMultiPlayerPopup();

            VBox vBox = new VBox();
            vBox.setId("menuWrapper");
            HBox hBox = new HBox(vBox);
            hBox.setId("menuWrapperWrapper");
            VBox mainMenuRoot = new VBox(hBox);
            mainMenuScene = new Scene(mainMenuRoot, MAIN_MENU_WIDTH, MAIN_MENU_HEIGHT);

            mainMenuRoot.getStylesheets().add("TowDef/GUI//menusCSS.css");

            Label info = new Label("welcome the the SUTian version\nof the well known tower-defense game!\n\n");
            info.setId("info");

            Label nickNameLabel = new Label("I'm Funny! And you are?");
            nameField = new TextField("nick name");
            nameField.setId("nameField");
            HBox nickNameFieldWrapper = new HBox(nickNameLabel, nameField);
            nickNameFieldWrapper.setId("lineWrapper");

            Label pickColorLabel = new Label("Choose a Color:");
            colorComboBox = new ComboBox<>();
            colorComboBox.setMaxSize(150, 50);
            //colorComboBox.setPadding(Insets.EMPTY);
            ObservableList<String> data = FXCollections.observableArrayList(
                    "chocolate", "salmon", "gold", "coral", "darkorchid",
                    "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
                    "blueviolet", "brown");
            colorComboBox.setItems(data);
            Callback<ListView<String>, ListCell<String>> factory = list -> new ListCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    Rectangle rect = new Rectangle(120, 30);
                    if (item != null) {
                        rect.setFill(Color.web(item));
                        setGraphic(rect);
                    }
                }
            };
            colorComboBox.setCellFactory(factory);
            colorComboBox.setButtonCell(factory.call(null));
            colorComboBox.setId("colorComboBox");
            HBox colorChooserWrapper = new HBox(pickColorLabel, colorComboBox);
            colorChooserWrapper.setId("lineWrapper");

            Label mapChooserLabel = new Label("This is the map you will play on, click to change");
            mapPreview = new ImageView(
                    new Image(getClass().getResource("/files/images/maps/" + chosenMapNumber + ".gif").toString())
            );
            mapPreview.setPreserveRatio(true);
            mapPreview.setFitHeight(MAP_PREVIEW_HEIGHT);
            mapPreview.setOnMouseClicked(event -> towerChooserPopup.show(primaryStage));
            mapPreview.setId("mapPreview");
            HBox mapChooserWrapper = new HBox(mapChooserLabel, mapPreview);
            mapChooserWrapper.setId("lineWrapper");

            Button startGame = new Button("lets get the party started!");

            Button startMultiPlayer = new Button("or maybe bigger party!");

            Button startServer = new Button("create a server");

            Label highScoreLabel = new Label();
            highScoreLabel.textProperty().bind(Game.getInstance().highScoreLabelTextProperty());
            highScoreLabel.setId("highScore");

            CheckBox soundOn = new CheckBox("mute sound?");
            Tooltip tooltip = new Tooltip("if this box is checked, music won't be played!");
            soundOn.setTooltip(tooltip);
            soundOn.selectedProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());

            HBox changeVolumeWrapper = new HBox();
            changeVolumeWrapper.setId("changeVolumeWrapper");
            Label sliderLabel = new Label("sound volume: ");
            sliderLabel.setId("sliderLabel");
            Slider soundVolume = new Slider(0, 1, .5);
            soundVolume.valueProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());
            changeVolumeWrapper.getChildren().addAll(sliderLabel, soundVolume);

            Button quitGame = new Button("Oh! Get me out of this game!");

            Label error = new Label("");
            error.setId("errorLabel");

            startGame.setOnAction(event -> {
                        if (Game.getInstance().setMap(MapReader.getInstance().loadMap("/files/maps/" + chosenMapNumber + ".txt")) != null) {
                            Game.getInstance().init(nameField.getText(), colorComboBox.getValue());
                            gameGraphics.startGame(chosenMapNumber);
                            Game.getInstance().begin();
                        } else {
                            System.out.println("map not found!");
                        }
                    }
            );

            startMultiPlayer.setOnAction(event -> {
                multiPlayerPopup.show(primaryStage);
                multiPlayerPopup.requestFocus();
            });

            quitGame.setOnAction(event -> {
                        Game.getInstance().saveHighScore();
                        primaryStage.close();

                    }
            );

            vBox.getChildren().
                    addAll(
                            info,
                            nickNameFieldWrapper,
                            colorChooserWrapper,
                            mapChooserWrapper,
                            startGame,
                            startMultiPlayer,
                            highScoreLabel,
                            soundOn,
                            changeVolumeWrapper,
                            quitGame,
                            error
                    );
        }

        public void buildTowerChooserPopup() {
            VBox root = new VBox();
            root.setId("towerChooserPopup");
            root.getStylesheets().add("TowDef/GUI//popupCSS.css");

            Label info = new Label(
                    "You can navigate between maps either by using the buttons or Left\\Right keys. \n" +
                            "press Enter or the below button to make your choice!");
            info.setId("info");

            Button leftButton = new Button("<");
            leftButton.setId("navButton");

            slideShow = new ImageView(new Image(getClass().getResource(
                    "/files/images/maps/" + viewIngImageNumber + ".gif"
            ).toString()));
            slideShow.setPreserveRatio(true);
            slideShow.setFitHeight(POPUP_IMAGEVIEW_HEIGHT);
            slideShow.setId("mapSlideShow");

            Button rightButton = new Button(">");
            rightButton.setId("navButton");

            leftButton.setOnAction(event -> setImage("prev"));
            rightButton.setOnAction(event -> setImage("next"));

            HBox sliderWrapper = new HBox(leftButton, slideShow, rightButton);
            sliderWrapper.setId("lineWrapper");

            mapNumberLabel = new Label("map 0 over " + Game.MAPS_COUNT);

            Button submitButton = new Button("Let's try this one:)");
            submitButton.setOnAction(event -> {
                chosenMapNumber = viewIngImageNumber;
                mapPreview.setImage(new Image(getClass().getResource("/files/images/maps/" + chosenMapNumber + ".gif").toString()));
                towerChooserPopup.hide();
            });

            root.getChildren().addAll(
                    info,
                    sliderWrapper,
                    mapNumberLabel,
                    submitButton
            );

            root.setOnKeyPressed(ke -> {
                if (ke.getCode().toString().equals("RIGHT")) setImage("next");
                if (ke.getCode().toString().equals("LEFT")) setImage("prev");
            });
            towerChooserPopup.getContent().add(root);
        }

        public void buildMultiPlayerPopup() {
            VBox root = new VBox();
            root.setId("multiPlayerPopup");
            root.getStylesheets().add("TowDef/GUI//popupCSS.css");

            Label info = new Label(
                    "Choose a IPadress and portnumber");
            info.setId("info");

            Label IpAdress = new Label("Your IPAdress: ");
            TextField Ip = new TextField("localhost");
            HBox IpAdressWraper = new HBox(IpAdress, Ip);

            Label portNumber = new Label("Your portnumber: ");
            TextField port = new TextField("1375");
            HBox portWraper = new HBox(portNumber, port);

            Label situation = new Label("");
            situation.setId("networkSituation");

            Button submitButton = new Button("submit");

            submitButton.setOnAction(event -> {
                if (!isSubmit) {
                    client = new Client(Integer.parseInt(port.getText()), Ip.getText());
                    client.sendMessage("///name///"+ nameField.getText());
                    client.sendMessage("///color///"+ colorComboBox.getValue());
                    situation.setText("wait please...");
                    isSubmit = true;
                }
            });

//            Button exitButton = new Button("get me out of here!");
//            exitButton.setOnAction(event -> {
//
//                multiPlayerPopup.hide();
//            });

            root.getChildren().addAll(
                    info,
                    IpAdressWraper,
                    portWraper,
                    situation,
                    submitButton
            );
            root.setOnKeyPressed(ke -> {
                if (ke.getCode().toString().equals("ESCAPE")) {
                    if (client != null)
                        client.close();
                    situation.setText("");
                    multiPlayerPopup.hide();
                    isSubmit = false;
                }
            });

            multiPlayerPopup.getContent().add(root);
        }

        public void startMultiPlayerGame(int mapNumber) {
            Platform.runLater(() -> {
                if (Game.getInstance().setMap(MapReader.getInstance().loadMap("/files/maps/" + mapNumber + ".txt")) != null) {
                    Game.getInstance().init(nameField.getText(), colorComboBox.getValue());
                    client.setMoney(Game.getInstance().getPlayers(0).moneyProperty());

                    gameGraphics.startGame(mapNumber);
                    Game.getInstance().begin();
                } else {
                    System.out.println("map not found!");
                }
                mainMenuGraphics.multiPlayerPopup.hide();
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

        public void showMainMenu() {
            if (mainMenuScene == null) buildMainMenu();
            playingMediaPlayer.stop();
            playingMediaPlayer = menuMediaPlayer;
            playingMediaPlayer.play();
            primaryStage.setScene(mainMenuScene);
        }
    }

    public MainMenuGraphics getMainMenuGraphics() {
        return mainMenuGraphics;
    }

    public class GameGraphics {
        public final int SHOW_RANGE_EFFECT_DURATION = 2000;//millis

        private Group gameRoot;
        private GridPane rightMenu;
        private Popup pausedGamePopup = new Popup();
        private Popup gameOverPopup = new Popup();

        private ImageView gameMap;
        private Label error;

        public ChatGraphics getChatGraphics() {
            return chatGraphics;
        }

        private ToggleGroup towersAndAbilities;
        private ChatGraphics chatGraphics = new ChatGraphics();

        public class ChatGraphics{
            public static final double CHAT_BOX_WIDTH = 250;
            private VBox chatBox;
            private VBox chatHistory;
            private Button backToPublicChat;
            private Text chatWindowInfo;


            public void addMsg(String senderName, String color, String text, String time) {

                Label snderName = new Label(senderName + ":");
                snderName.setId("senderName");
                snderName.setMaxWidth(200);
                snderName.setAlignment(Pos.BASELINE_LEFT);

                Label msgText = new Label(text);
                msgText.setId("msgText");
                msgText.setWrapText(true);
                msgText.setMaxWidth(200);

                Label msgTime = new Label(time);
                msgTime.setId("msgTime");
                msgTime.setMaxWidth(200);
                msgTime.setAlignment(Pos.BASELINE_RIGHT);

                VBox msg = new VBox(snderName, msgText, msgTime);
                msg.setBackground(new Background(new BackgroundFill(Color.web(color), new CornerRadii(4d), Insets.EMPTY)));
                msg.setPadding(new Insets(5));
                msg.setMaxWidth(200);
                msg.setEffect(new DropShadow(2, Color.DARKBLUE));

                HBox hBox = new HBox(msg);
                hBox.setBackground(new Background(new BackgroundFill(Color.web(color), new CornerRadii(4d), Insets.EMPTY)));
                hBox.getStyleClass().add(senderName.equals(Game.getInstance().getPlayer(0).getName()) ? "clientMsg" : "");

                snderName.setOnMouseClicked(event -> {
                    addHighScoreToolip(senderName).show(primaryStage);
                });
                chatHistory.getChildren().add(hBox);
            }

            public void buildChatBox() {
                backToPublicChat = new Button("<");
                backToPublicChat.setId("backToPublicChat");
                backToPublicChat.setVisible(false);
                chatWindowInfo = new Text("public chat room");
                chatWindowInfo.setId("chatWindowInfo");
                chatHistory = new VBox();
                chatHistory.setId("chatHistory");
                chatHistory.setPrefWidth(CHAT_BOX_WIDTH);
                chatHistory.setPrefHeight(GAME_HEIGHT - 50);//badsmell
                TextField messageField = new TextField("type your message...");
                messageField.setId("messageField");
                messageField.setPrefHeight(30);
                messageField.setPrefWidth(CHAT_BOX_WIDTH - 40);
                Button sendButton = new Button("send");
                sendButton.setId("sendButton");
                sendButton.setPrefHeight(30);
                sendButton.setOnMouseClicked(event -> {
                    client.sendMessage("///public///" + messageField.getText());
                });
                ScrollPane scrlPane = new ScrollPane(chatHistory);
                scrlPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrlPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrlPane.setId("scrolPane");
                chatBox = new VBox(new HBox(backToPublicChat, chatWindowInfo), scrlPane, new HBox(messageField, sendButton));
                chatBox.setId("chatBox");
                chatBox.getStylesheets().add("TowDef/GUI//chatBoxCSS.css");
            }

            public Tooltip addHighScoreToolip(String senderName){
                return new Tooltip(client.getScore(senderName) + "");
            }
        }
        public void startGame(int mapNumber) {
            buildNewGame(mapNumber);
            if (rightMenu == null) buildRightMenu();
            if (chatGraphics.chatHistory == null) chatGraphics.buildChatBox();
            showGame();
        }

        public void buildRightMenu() {
            rightMenu = new GridPane();
            rightMenu.setId("rightMenu");
            rightMenu.getStylesheets().add("TowDef/GUI//rightMenuCSS.css");

            Label towerMenuTitle = new Label("towers:");
            towerMenuTitle.setId("towerName");

            ArrayList<ToggleButton> towerButtons = new ArrayList<>();
            ArrayList<Label> towerLabels = new ArrayList<>();

            towersAndAbilities = new ToggleGroup();
            for (int i = 0; i < Game.TOWER_NAMES.size(); i++) {
                ToggleButton towerButton = new ToggleButton();
                towerButton.getStyleClass().add("towerButton");
                towerButton.setToggleGroup(towersAndAbilities);
                towerButton.setUserData(Game.TOWER_NAMES.get(i));
                towerButton.setBackground(new Background(new BackgroundImage(
                        new Image(getClass().getResource("/files/images/towers/" + Game.TOWER_NAMES.get(i) + "/1.gif").toString()),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, true, false)
                )));
                towerButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT * 2, RIGHT_MENU_ICONS_HEIGHT * 2);
                towerButtons.add(towerButton);
                towerLabels.add(new Label(Game.TOWER_NAMES.get(i) + ": "));
            }

            ToggleButton sellButton = new ToggleButton();
            sellButton.setId("sellButton");
            sellButton.setToggleGroup(towersAndAbilities);
            sellButton.setTooltip(new Tooltip("Sell a building and gain half of its price."));
            sellButton.setUserData("sell");

            sellButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT, RIGHT_MENU_ICONS_HEIGHT);
            ToggleButton repairButton = new ToggleButton();
            repairButton.setId("repairButton");
            repairButton.setToggleGroup(towersAndAbilities);
            repairButton.setTooltip(new Tooltip("Repair a building."));
            repairButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT, RIGHT_MENU_ICONS_HEIGHT);
            repairButton.setUserData("repair");

            ToggleButton upgradeButton = new ToggleButton();
            upgradeButton.setId("upgradeButton");
            upgradeButton.setToggleGroup(towersAndAbilities);
            upgradeButton.setTooltip(new Tooltip("Upgrade a tower."));
            upgradeButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT, RIGHT_MENU_ICONS_HEIGHT);
            upgradeButton.setUserData("upgrade");

            ToggleGroup speed = new ToggleGroup();

            ToggleButton pauseButton = new ToggleButton();
            pauseButton.setId("pauseButton");
            pauseButton.setToggleGroup(speed);
            pauseButton.setTooltip(new Tooltip("Pauses the game."));
            pauseButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT * 2, RIGHT_MENU_ICONS_HEIGHT);
            pauseButton.setOnMouseClicked(event -> pauseGame());
            ToggleButton playButton = new ToggleButton();
            playButton.setId("playButton");
            playButton.setToggleGroup(speed);
            playButton.setTooltip(new Tooltip("Makes the game speed normal."));
            playButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT * 2, RIGHT_MENU_ICONS_HEIGHT);
            playButton.setOnMouseClicked(event -> Game.getInstance().gameRateProperty().setValue(1));
            ToggleButton doubleSpeedButton = new ToggleButton();
            doubleSpeedButton.setId("doubleSpeedButton");
            doubleSpeedButton.setToggleGroup(speed);
            doubleSpeedButton.setTooltip(new Tooltip("Makes the game speed fast."));
            doubleSpeedButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT * 2, RIGHT_MENU_ICONS_HEIGHT);
            doubleSpeedButton.setOnMouseClicked(event -> Game.getInstance().gameRateProperty().setValue(2));
            ToggleButton tripleSpeedButton = new ToggleButton();
            tripleSpeedButton.setId("tripleSpeedButton");
            tripleSpeedButton.setToggleGroup(speed);
            tripleSpeedButton.setTooltip(new Tooltip("Makes the game speed too fast!"));
            tripleSpeedButton.setMinSize(RIGHT_MENU_ICONS_HEIGHT * 2, RIGHT_MENU_ICONS_HEIGHT);
            tripleSpeedButton.setOnMouseClicked(event -> Game.getInstance().gameRateProperty().setValue(4));

            Label playerMoney = new Label();
            playerMoney.textProperty().bind(Bindings.concat("funds: $").concat(Game.getInstance().getPlayer(0).moneyProperty()));

            Label castleHp = new Label();
            castleHp.textProperty().bind(Bindings.concat("castle hp: ♥").concat(Game.getInstance().getPlayer(0).getCastle().hpProperty()));

            Label nextWaveInfoTitle = new Label("Next wave:");
            nextWaveInfoTitle.getStyleClass().add("title");

            Label nextWaveInfo = new Label();
            nextWaveInfo.textProperty().bind(AI.getInstance().nextWaveInfoProperty());

            error = new Label("");

            rightMenu.add(towerMenuTitle, 0, 0, 4, 1);
            for (int i = 0; i < towerButtons.size(); i++) {
                rightMenu.add(towerLabels.get(i), 0, i + 1, 3, 1);
                rightMenu.add(towerButtons.get(i), 3, i + 1);
            }

            HBox hbox = new HBox(sellButton, repairButton, upgradeButton);
            hbox.setSpacing(3);
            rightMenu.add(hbox, 0, 6, 4, 1);

            rightMenu.add(pauseButton, 0, 7);
            rightMenu.add(playButton, 1, 7);
            rightMenu.add(doubleSpeedButton, 2, 7);
            rightMenu.add(tripleSpeedButton, 3, 7);
            rightMenu.add(playerMoney, 0, 8, 4, 1);
            rightMenu.add(castleHp, 0, 9, 4, 1);
            rightMenu.add(nextWaveInfoTitle, 0, 10, 4, 1);
            rightMenu.add(nextWaveInfo, 0, 11, 4, 1);
            rightMenu.add(error, 0, 12, 4, 1);
        }

        public void buildNewGame(int mapNumber) {
            gameMap = new ImageView(new Image(getClass().getResource("/files/images/maps/" + mapNumber + ".gif").toString()));
            gameMap.setPreserveRatio(true);
            gameMap.setFitHeight(GAME_HEIGHT);
            MAP_CELLS_HEIGHT = MAP_CELLS_WIDTH = GAME_HEIGHT / Game.getInstance().getMap().getYSize();
            gameMap.setOnMouseClicked(event -> {
                if (towersAndAbilities.getSelectedToggle() != null) {
                    Game.getInstance().mapClicked(
                            (int) (event.getX() / MAP_CELLS_WIDTH),
                            (int) (event.getY() / MAP_CELLS_HEIGHT),
                            (String) towersAndAbilities.getSelectedToggle().getUserData()
                    );
                }
            });

            gameRoot = new Group(gameMap);
            buildPausedGamePopup();
            buildGameOverPopup();
        }

        public void buildPausedGamePopup() {
            VBox root = new VBox();
            root.setId("root");
            root.getStylesheets().add(getClass().getResource("/TowDef/GUI/popupCSS.css").toString());

            Label info = new Label(
                    "game is paused!\n");
            info.setId("info");

            Button playAgain = new Button("get me back to the game!");
            playAgain.setOnAction(event -> resumeGame());

            Label highScoreLabel = new Label();
            highScoreLabel.textProperty().bind(Game.getInstance().highScoreLabelTextProperty());
            highScoreLabel.setId("highScore");


            CheckBox soundOn = new CheckBox("mute sound?");
            soundOn.setTooltip(new Tooltip("if this box is checked, music won't be played!"));
            soundOn.selectedProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());

            Label sliderLabel = new Label("sound volume: ");
            sliderLabel.setId("sliderLabel");
            Slider soundVolume = new Slider(0, 1, .5);
            soundVolume.valueProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());
            HBox changeVolumeWrapper = new HBox(sliderLabel, soundVolume);
            changeVolumeWrapper.setId("changeVolumeWrapper");

            Button backToMainMenu = new Button("get me to the main menu!");
            backToMainMenu.setOnAction(event -> {
                        Game.getInstance().saveHighScore();
                        playingMediaPlayer.stop();
                        playingMediaPlayer = menuMediaPlayer;
                        playingMediaPlayer.play();
                        mainMenuGraphics.showMainMenu();
                        pausedGamePopup.hide();
                    }
            );

            Button quitGame = new Button("Oh! Get me out of this game!");


            quitGame.setOnAction(event -> {
                        Game.getInstance().saveHighScore();
                        primaryStage.close();
                    }
            );

            root.getChildren().addAll(
                    info,
                    playAgain,
                    highScoreLabel,
                    soundOn,
                    changeVolumeWrapper,
                    backToMainMenu,
                    quitGame
            );

            root.setOnKeyPressed(ke -> {
                if (ke.getCode().toString().equals("ESCAPE")) resumeGame();
            });
            pausedGamePopup.getContent().add(root);
        }

        public void rotateTower(ImageView imageView, PathCell intruderCell) {
            if (imageView != null)
                imageView.setRotate(rotateDegree(intruderCell.getPrev().getX() * MAP_CELLS_WIDTH - imageView.getX(), intruderCell.getPrev().getY() * MAP_CELLS_HEIGHT - imageView.getY()));

        }

        public void buildGameOverPopup() {
            VBox root = new VBox();
            root.setId("root");
            root.getStylesheets().add(getClass().getResource("/TowDef/GUI/popupCSS.css").toString());

            Label info = new Label(
                    "This game is over.\n" +
                            "well... " +
                            "any star that shines in a night, " +
                            "dies in another!\n" +
                            "do you think that is the highest" +
                            "score you can achieve?");
            info.setId("info");


            Button resumeGame = new Button("let me try again!mr. Christopher:-\"");
            resumeGame.setOnAction(event -> {
                Game.getInstance().saveHighScore();
                newGame();
                gameOverPopup.hide();
            });

            Label highScoreLabel = new Label();
            highScoreLabel.textProperty().bind(Game.getInstance().highScoreLabelTextProperty());
            highScoreLabel.setId("highScore");

            CheckBox soundOn = new CheckBox("mute sound?");
            Tooltip tooltip = new Tooltip("if this box is checked, music won't be played!");
            soundOn.setTooltip(tooltip);
            soundOn.selectedProperty().bindBidirectional(Game.getInstance().isSoundMutedProperty());

            Label sliderLabel = new Label("sound volume: ");
            sliderLabel.setId("sliderLabel");
            Slider soundVolume = new Slider(0, 1, .5);
            soundVolume.valueProperty().bindBidirectional(Game.getInstance().soundVolumeProperty());
            HBox changeVolumeWrapper = new HBox(sliderLabel, soundVolume);
            changeVolumeWrapper.setId("changeVolumeWrapper");

            Button backToMainMenu = new Button("get me to the main menu!");
            backToMainMenu.setOnAction(event -> {
                        Game.getInstance().saveHighScore();
                        mainMenuGraphics.showMainMenu();
                        gameOverPopup.hide();
                    }
            );

            Button quitGame = new Button("Oh! Get me out of this game!");
            quitGame.setOnAction(event -> {
                Game.getInstance().saveHighScore();
                primaryStage.close();
            });

            root.getChildren().addAll(
                    info,
                    resumeGame,
                    highScoreLabel,
                    soundOn,
                    changeVolumeWrapper,
                    backToMainMenu,
                    quitGame
            );
            gameOverPopup.getContent().add(root);
        }

        public void error(String errorText) {
            error.setText(errorText);
        }

        public void showGame() {
            playingMediaPlayer.stop();
            playingMediaPlayer = gameMediaPlayer;
            playingMediaPlayer.play();

            BorderPane root = new BorderPane();
            root.getStylesheets().add("TowDef/GUI//gameCSS.css");

            primaryStage.setScene(new Scene(root, MAIN_MENU_WIDTH, MAIN_MENU_HEIGHT));
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setFullScreen(true);

            root.setCenter(gameRoot);
            root.setRight(rightMenu);
            root.setLeft(chatGraphics.chatBox);

            root.requestFocus();
            root.setOnKeyPressed(ke -> {
                if (ke.getCode().toString().equals("ESCAPE")) pauseGame();
            });
        }

        public ImageView addIntruder(PathCell cell, String iconAddress) {
            ImageView imgView = new ImageView(new Image(iconAddress));
            imgView.setX(cell.getX() * MAP_CELLS_WIDTH);
            imgView.setY(cell.getY() * MAP_CELLS_HEIGHT);
            imgView.setPreserveRatio(true);
            imgView.setFitWidth(MAP_CELLS_HEIGHT);
            imgView.setRotate(90 * cell.xDir() + 180 * cell.yDir());
            gameRoot.getChildren().add(imgView);
            return imgView;
        }

        public void removeIntruder(ImageView imgView) {
            gameRoot.getChildren().remove(imgView);
        }

        //ImageView temp;
        public void moveIntruderTowards(ImageView imgView, double xTranslate, double yTranslate, double rotation, int timeStep) {//x and y are the place of cell.
            //double xTranslate = x * MAP_CELLS_WIDTH - imgView.getBoundsInParent().getMinX();
            //double yTranslate = y * MAP_CELLS_HEIGHT - imgView.getBoundsInParent().getMinY();
            //if (Math.abs(xTranslate) < 7) xTranslate = 0;
            //if (Math.abs(yTranslate) < 7) yTranslate = 0;
            xTranslate *= MAP_CELLS_WIDTH;
            yTranslate *= MAP_CELLS_HEIGHT;
            //if (temp == null) temp = imgView;
            //if (temp == imgView) {
            //    if (rotation > 180){
            //        int sex = 1;
            //    }
            //System.out.println(rotation);
            //}
                /*System.out.println(String.format("%.2f", yTranslate) + "\t" +
                        String.format("%.2f", xTranslate) + "\t" +
                        String.format("%.2f", Math.toDegrees(Math.atan(yTranslate / xTranslate)) + 90) + "\t" +
                        String.format("%.2f", imgView.getRotate()));
            System.out.println("(atan(" + yTranslate + " / " + xTranslate + ") = " +
                    String.format("%.2f", Math.toDegrees(Math.atan(yTranslate / xTranslate))) +
                    ") + 90 - " +
                    String.format("%.2f", imgView.getRotate()) + " = " +
                    String.format("%.2f", (Math.toDegrees(Math.atan(yTranslate / xTranslate)) + 90 - imgView.getRotate())));
            }*/

            TranslateTransition trnsltTrns = new TranslateTransition(Duration.millis(timeStep * TimeLine.TIMELINE_DELAY), imgView);
            trnsltTrns.setByX(xTranslate);
            trnsltTrns.setByY(yTranslate);
            RotateTransition rttTrns = new RotateTransition(Duration.millis(timeStep * TimeLine.TIMELINE_DELAY), imgView);
            rttTrns.setByAngle(rotation);
            ParallelTransition prllTrns = new ParallelTransition(trnsltTrns, rttTrns);
            prllTrns.rateProperty().bind(Game.getInstance().gameRateProperty());
            prllTrns.play();
        }

        public double rotateDegree(double x, double y) {
            double theta = Math.atan(y / x) * 180 / Math.PI;
            if (x < 0)
                return theta - 90;
            return theta + 90;

        }

        public void addBullet(LandCell cell, Intruder choice, String bulletAddress, int damage, int speed) {

            ImageView imageView = new ImageView(new Image(getClass().getResource(bulletAddress).toString()));
            imageView.setX(cell.getX() * MAP_CELLS_WIDTH);
            imageView.setY(cell.getY() * MAP_CELLS_HEIGHT);
            imageView.setFitHeight(MAP_CELLS_HEIGHT);
            imageView.setFitWidth(MAP_CELLS_WIDTH);
            gameRoot.getChildren().add(imageView);
            new Bullet(imageView, choice, damage, speed);
        }

        public void shooting(Intruder intruder, int damage, Bullet bullet) {
            PathCell intruderCell = intruder.getCell();
            TranslateTransition translateTransition = new TranslateTransition(
                    Duration.millis(TimeLine.TIMELINE_DELAY), bullet.getImageView());
            double x = (intruderCell.getPrev().getX()) * MAP_CELLS_WIDTH + -bullet.getImageView().getBoundsInParent().getMinX();//change
            double y = (intruderCell.getPrev().getY()) * MAP_CELLS_HEIGHT - bullet.getImageView().getBoundsInParent().getMinY();//change
            double s = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (s > bullet.speed) {
                translateTransition.setByX(x / s * bullet.speed);
                translateTransition.setByY(y / s * bullet.speed);
                bullet.getImageView().setRotate(rotateDegree(x, y));
                translateTransition.play();
            } else {
                translateTransition.setByX(x);
                translateTransition.setByY(y);
                bullet.getImageView().setRotate(rotateDegree(x, y));
                translateTransition.play();
                translateTransition.setOnFinished(event -> {
                    bullet.delete();
                    intruder.damage(damage);

                });
            }


        }

        public void laserAnimation(Tower tower, Intruder choice, Boolean isRed) {
            if (isRed) {
                Line laser = new Line(
                        (tower.getImageView().getBoundsInParent().getMinX() + tower.getImageView().getBoundsInParent().getMaxX()) / 2,
                        (tower.getImageView().getBoundsInParent().getMinY() + tower.getImageView().getBoundsInParent().getMaxY()) / 2,
                        (tower.getImageView().getBoundsInParent().getMinX() + tower.getImageView().getBoundsInParent().getMaxX()) / 2,
                        (tower.getImageView().getBoundsInParent().getMinY() + tower.getImageView().getBoundsInParent().getMaxY()) / 2
                );
                laser.setStroke(Color.RED);
                laser.setStrokeWidth(3);
                laser.setEffect(new GaussianBlur(5));
                choice.getImgView().boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    laser.setEndX((newValue.getMinX() + newValue.getMaxX()) / 2);
                    laser.setEndY((newValue.getMinY() + newValue.getMaxY()) / 2);
                });
                tower.getImageView().boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    laser.setStartX((newValue.getMinX() + newValue.getMaxX()) / 2);
                    laser.setStartY((newValue.getMinY() + newValue.getMaxY()) / 2);
                });
                Timeline tmln = new Timeline(new KeyFrame(Duration.millis(50), new KeyValue(laser.opacityProperty(), 0)));
                tmln.setAutoReverse(true);
                tmln.setCycleCount(tower.getTimeStepByLevel(tower.getLevel()));
                tmln.setOnFinished(event -> gameRoot.getChildren().remove(laser));
                tmln.play();
                gameRoot.getChildren().add(laser);
            } else {
                double xInset = MAP_CELLS_HEIGHT * Math.sin(tower.getImageView().getRotate()) / 3;
                double yInset = MAP_CELLS_HEIGHT * Math.cos(tower.getImageView().getRotate()) / 3;
                Line laser1 = new Line(
                        (tower.getImageView().getBoundsInParent().getMinX() + tower.getImageView().getBoundsInParent().getMaxX()) / 2 + xInset,
                        (tower.getImageView().getBoundsInParent().getMinY() + tower.getImageView().getBoundsInParent().getMaxY()) / 2 + yInset,
                        (choice.getImgView().getBoundsInParent().getMinX() + choice.getImgView().getBoundsInParent().getMaxX()) / 2,
                        (choice.getImgView().getBoundsInParent().getMinY() + choice.getImgView().getBoundsInParent().getMaxY()) / 2
                );
                Line laser2 = new Line(
                        (tower.getImageView().getBoundsInParent().getMinX() + tower.getImageView().getBoundsInParent().getMaxX()) / 2 - xInset,
                        (tower.getImageView().getBoundsInParent().getMinY() + tower.getImageView().getBoundsInParent().getMaxY()) / 2 - yInset,
                        (choice.getImgView().getBoundsInParent().getMinX() + choice.getImgView().getBoundsInParent().getMaxX()) / 2,
                        (choice.getImgView().getBoundsInParent().getMinY() + choice.getImgView().getBoundsInParent().getMaxY()) / 2
                );
                laser1.setStroke(Color.GREEN);
                laser1.setStrokeWidth(3);
                laser1.setEffect(new GaussianBlur(5));
                laser2.setStroke(Color.GREEN);
                laser2.setStrokeWidth(3);
                laser2.setEffect(new GaussianBlur(5));
                choice.getImgView().boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    laser1.setEndX((newValue.getMinX() + newValue.getMaxX()) / 2);
                    laser1.setEndY((newValue.getMinY() + newValue.getMaxY()) / 2);
                    laser2.setEndX((newValue.getMinX() + newValue.getMaxX()) / 2);
                    laser2.setEndY((newValue.getMinY() + newValue.getMaxY()) / 2);
                });
                tower.getImageView().boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    double xInset2 = MAP_CELLS_HEIGHT * Math.sin(tower.getImageView().getRotate()) / 3;
                    double yInset2 = MAP_CELLS_HEIGHT * Math.cos(tower.getImageView().getRotate()) / 3;
                    laser1.setStartX((newValue.getMinX() + newValue.getMaxX()) / 2 + xInset2);
                    laser1.setStartY((newValue.getMinY() + newValue.getMaxY()) / 2 + yInset2);
                    laser2.setStartX((newValue.getMinX() + newValue.getMaxX()) / 2 - xInset2);
                    laser2.setStartY((newValue.getMinY() + newValue.getMaxY()) / 2 - yInset2);
                });
                Timeline tmln = new Timeline(new KeyFrame(
                        Duration.millis(50),
                        new KeyValue(laser1.opacityProperty(), 0),
                        new KeyValue(laser2.opacityProperty(), 0)
                ));
                tmln.setAutoReverse(true);
                tmln.setCycleCount(tower.getTimeStepByLevel(tower.getLevel())/2);
                tmln.setOnFinished(event -> gameRoot.getChildren().removeAll(laser1, laser2));
                tmln.play();
                gameRoot.getChildren().addAll(laser1, laser2);
            }
        }

        public ImageView addTower(LandCell cell, String towerType) {
            ImageView imageView = new ImageView(new Image(getClass().getResource("/files/images/towers/" + towerType + "/1.gif").toString()));
            imageView.setX(cell.getX() * MAP_CELLS_WIDTH);
            imageView.setY(cell.getY() * MAP_CELLS_HEIGHT);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(MAP_CELLS_HEIGHT);
            gameRoot.getChildren().add(imageView);
            return imageView;
        }

        public void showTowerRange(int x, int y, double range) {
            Circle circle = new Circle(
                    x * MAP_CELLS_WIDTH + MAP_CELLS_WIDTH / 2,
                    y * MAP_CELLS_HEIGHT + MAP_CELLS_HEIGHT / 2,
                    MAP_CELLS_WIDTH,
                    Color.RED
            );
            circle.setOpacity(.5);
            gameRoot.getChildren().add(circle);

            Circle border = new Circle(
                    x * MAP_CELLS_WIDTH + MAP_CELLS_WIDTH / 2,
                    y * MAP_CELLS_HEIGHT + MAP_CELLS_HEIGHT / 2,
                    MAP_CELLS_WIDTH,
                    Color.TRANSPARENT
            );
            border.setEffect(new GaussianBlur(3));
            border.setStroke(Color.RED);
            border.setStrokeWidth(3);
            gameRoot.getChildren().add(border);

            Circle[] blurredBorders = new Circle[3];
            Timeline[] blurredBorderTmlns = new Timeline[3];
            for (int i = 0; i < 3; i++) {
                blurredBorders[i] = new Circle(
                        x * MAP_CELLS_WIDTH + MAP_CELLS_WIDTH / 2,
                        y * MAP_CELLS_HEIGHT + MAP_CELLS_HEIGHT / 2,
                        1,
                        Color.TRANSPARENT
                );
                blurredBorders[i].setEffect(new GaussianBlur(8));
                blurredBorders[i].setStroke(Color.RED);
                blurredBorders[i].setStrokeWidth(3);
                gameRoot.getChildren().add(blurredBorders[i]);
                blurredBorderTmlns[i] = new Timeline(new KeyFrame(
                        Duration.millis(SHOW_RANGE_EFFECT_DURATION / 1.3),
                        new KeyValue(
                                blurredBorders[i].radiusProperty(),
                                range * MAP_CELLS_WIDTH
                        )
                ));
                blurredBorderTmlns[i].setAutoReverse(true);
                blurredBorderTmlns[i].setCycleCount(3);
            }


            Timeline blur = new Timeline(new KeyFrame(
                    Duration.millis(SHOW_RANGE_EFFECT_DURATION),
                    new KeyValue(
                            circle.radiusProperty(),
                            range * MAP_CELLS_WIDTH
                    ),
                    new KeyValue(
                            circle.opacityProperty(),
                            .2
                    ),
                    new KeyValue(
                            border.radiusProperty(),
                            range * MAP_CELLS_WIDTH
                    )
            ));
            Timeline borderShooter = new Timeline(new KeyFrame(
                    Duration.millis(100),
                    event -> {
                        if (blur.getCurrentTime().toMillis() > 150)
                            if (blurredBorderTmlns[0].getCurrentTime().toMillis() == 0)
                                blurredBorderTmlns[0].play();
                            else {
                                if (blurredBorderTmlns[1].getCurrentTime().toMillis() == 0)
                                    blurredBorderTmlns[1].play();
                                else
                                    blurredBorderTmlns[2].play();
                            }
                    }));
            borderShooter.setCycleCount(4);
            borderShooter.play();
            blur.play();
            Timeline tmln = new Timeline(new KeyFrame(
                    Duration.millis(1000),
                    new KeyValue(circle.opacityProperty(), 0),
                    new KeyValue(border.opacityProperty(), 0),
                    new KeyValue(blurredBorders[0].opacityProperty(), 0),
                    new KeyValue(blurredBorders[1].opacityProperty(), 0),
                    new KeyValue(blurredBorders[2].opacityProperty(), 0)
            ));
            blurredBorderTmlns[2].setOnFinished(event -> {
                tmln.play();
                gameRoot.getChildren().remove(blurredBorders[0]);
                gameRoot.getChildren().remove(blurredBorders[1]);
                gameRoot.getChildren().remove(blurredBorders[2]);
            });
            tmln.setOnFinished(event -> {
                gameRoot.getChildren().remove(circle);
                gameRoot.getChildren().remove(border);
            });
        }

        private void pauseGame() {
            freezeGame();
            pausedGamePopup.show(primaryStage);
        }

        private void resumeGame() {
            TimeLine.getInstance().resume();
            primaryStage.getScene().getRoot().setEffect(null);
            primaryStage.getScene().getRoot().setDisable(false);
            pausedGamePopup.hide();
            primaryStage.getScene().getRoot().requestFocus();
        }

        public void gameOver() {
            freezeGame();
            playingMediaPlayer.stop();
            playingMediaPlayer = gameOverMediaPlayer;
            playingMediaPlayer.play();
            gameOverMediaPlayer.setOnEndOfMedia(() -> {
                playingMediaPlayer = menuMediaPlayer;
                playingMediaPlayer.play();
            });
            gameOverPopup.show(primaryStage);
        }

        private void newGame() {
        /*TimeLine.getInstance().newGame();
        game = new Game();
        System.gc();
        ui = new UI();
        main.getPrimaryStage().setScene(game.scene);
        main.getPrimaryStage().getScene().getRoot().requestFocus();
        Popup countDownPopup = new Popup();
        Label label = new Label("3");
        label.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-padding: 150px 0px 0px 0px;" +
                        "-fx-font: bold 150px \"Cooper Black\";" +
                        "-fx-text-fill: #44AAFF;" +
                        "-fx-background-radius: 2px;"
        );
        VBox root = new VBox(label);
        countDownPopup.getContent().add(root);
        countDownPopup.show(main.getPrimaryStage());
        Timeline countDown = new Timeline(new KeyFrame(Duration.seconds(1), event ->
                label.setText(((Character) (char) ((int) label.getText().charAt(0) - 1)).toString())));
        countDown.setCycleCount(3);
        countDown.play();
        countDown.setOnFinished(event -> {
            countDownPopup.hide();
            ui.startSending();
            scoreCounter.play();
            runningAnimations.add(scoreCounter);
        });
        main.getMediaPlayer().stop();
        main.setMediaPlayer(gameMediaPlayer);
        main.getMediaPlayer().play();*/
            System.out.println("new game.");
        }

        private void freezeGame() {
            TimeLine.getInstance().pause();
            primaryStage.getScene().getRoot().setEffect(new GaussianBlur());
            primaryStage.getScene().getRoot().setDisable(true);
        }

    }

    public GameGraphics getGameGraphics() {
        return gameGraphics;
    }
}
