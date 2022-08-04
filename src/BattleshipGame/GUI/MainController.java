package BattleshipGame.GUI;

import BattleshipGame.network.Client;
import BattleshipGame.network.NetworkAccess;
import BattleshipGame.network.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import BattleshipGame.game.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;


public class MainController implements Initializable{

    final int FIELD_HEIGHT = 10;
    final int FIELD_WIDTH = 10;

    public Button[][] cellButtons = new Button[FIELD_HEIGHT][FIELD_WIDTH];
    @FXML private TextArea textGameInfo;
    @FXML private VBox enemyField;
    @FXML private GridPane myField;
    @FXML private TextField xTextField;
    @FXML private TextField yTextField;
    @FXML private TextField ipTextField;
    @FXML private TextField portTextField;
    @FXML private TextField nameTextField;
    @FXML private Button attackButton;
    @FXML private Label battleshipsLabel;
    @FXML private Label cruisersLabel;
    @FXML private Label destroyersLabel;
    @FXML private Label submarinesLabel;
    @FXML private RadioButton clientRadioButton;
    @FXML private RadioButton serverRadioButton;
    @FXML private RadioButton easyModeRadioButton;


    boolean multiplayer;
    boolean isServer = false;
    boolean playersTurn = true;

    NetworkAccess network;

    Ocean enemyOcean;
    Ocean myOcean;

    String playerName;
    String enemyName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        enemyField.setSpacing(1);
    }


    /**
     * Create enemy game field
     */
    private void createCellButtons(){
        
        HBox topBox = new HBox();
        for(int i = 0; i < FIELD_WIDTH + 1; i++){
            Button button = createCellButton();
            button.setDisable(true);
            if(i != 0)
                button.setText(String.valueOf(i));
            topBox.getChildren().add(button);
        }
        enemyField.getChildren().add(topBox);

        for(int i = 0; i < FIELD_HEIGHT; i++){
            HBox box = new HBox();
            for(int j = 0; j < FIELD_WIDTH + 1; j++){

                Button button = createCellButton();
                if(j == 0){
                    button.setDisable(true);
                    button.setText(String.valueOf(i + 1));
                }
                else{
                    button.setOnAction(returnButtonMethod(i,j - 1));
                    cellButtons[i][j-1] = button;
                }
                box.getChildren().add(button);
            }

            enemyField.getChildren().add(box);
        }
    }


    /**
     * Create cell button
     * @return
     */
    private Button createCellButton(){
        Button button = new Button();
        button.setPrefHeight(1000);
        button.setPrefWidth(1000);
        button.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
        button.setFocusTraversable(false);

        return button;
    }


    /**
     * Returns event handler for button at (row, column)
     * @param row
     * @param column
     * @return
     */
    private EventHandler<ActionEvent> returnButtonMethod(final int row, final  int column){

        EventHandler<ActionEvent> consumer = actionEvent -> {
            if(!playersTurn)
                return;

            shootAtOcean(enemyOcean, row, column, false);

            if(multiplayer)
                network.makeShoot(row, column);
            else{
                Random rnd = new Random();
                int x,y;

                do
                {
                    x = rnd.nextInt(FIELD_WIDTH);
                    y = rnd.nextInt(FIELD_HEIGHT);
                }
                while(myOcean.getShipArray()[x][y].isHitted(x,y));
                enemyShoot(x,y);
            }

            updateEnemyField();
            updateGameInfo();
        };

        return consumer;
    }


    /**
     * Updates enemy game field
     */
    private void updateEnemyField(){
        for (int i = 0; i < FIELD_HEIGHT; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (enemyOcean.isOccupied(i, j))
                {
                    if (enemyOcean.getShipArray()[i][j].isHitted(i,j)){
                        cellButtons[i][j].setText(enemyOcean.getShipArray()[i][j].toString());
                        cellButtons[i][j].setDisable(true);
                        cellButtons[i][j].setStyle("-fx-background-color: red;-fx-text-fill: white;");
                    }
                    else
                        cellButtons[i][j].setText("");
                } else{
                    cellButtons[i][j].setText(enemyOcean.getShipArray()[i][j].toString());
                    if((enemyOcean.getShipArray()[i][j]).isHitted(i,j)){
                        cellButtons[i][j].setStyle("-fx-background-color: darkblue;");
                        cellButtons[i][j].setDisable(true);
                    }
                }
    }


    /**
     * Updates game info labels and checks if game is ended
     */
    private void updateGameInfo(){
        battleshipsLabel.setText(String.valueOf(enemyOcean.getAfloatBattleships()));
        cruisersLabel.setText(String.valueOf(enemyOcean.getAfloatCruisers()));
        destroyersLabel.setText(String.valueOf(enemyOcean.getAfloatDestroyers()));
        submarinesLabel.setText(String.valueOf(enemyOcean.getAfloatSubmarines()));

        if(enemyOcean.isGameOver()){
            if(network!=null)
                network.stopGame();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Congratulations!");
            alert.setContentText("You successfully win the game with " + enemyOcean.getShotsFired() + " shoots!");
            alert.showAndWait();

            textGameInfo.appendText("\nCongratulations! You successfully win the game with " + enemyOcean.getShotsFired() + " shoots!");
            for(Button[] line : cellButtons)
                for(Button button : line)
                    button.setDisable(true);
        }

        if(myOcean.isGameOver()){
            if(network!=null)
                network.stopGame();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Game over");
            alert.setContentText("Your enemy sunk all your ships with " + myOcean.getShotsFired() + " shoots...");
            alert.showAndWait();

            textGameInfo.appendText("\nYour enemy sunk all your ships with " + myOcean.getShotsFired() + " shoots...");
            for(Button[] line : cellButtons)
                for(Button button : line)
                    button.setDisable(true);
        }
    }


    /**
     * Updates player game field
     */
    private void updateMyField(){
        Ship[][] ships = myOcean.getShipArray();

        for(int i = 0; i <ships.length; i++)
            for(int j = 0; j < ships[i].length; j++){
                if(!ships[i][j].getShipType().equals(ShipType.Empty)){
                    Pane pane = new Pane();
                    if(ships[i][j].isHitted(i,j))
                        pane.setStyle("-fx-background-color: red;");
                    else
                        pane.setStyle("-fx-background-color: Green;");

                    myField.add(pane, i, j);
                }
                else if(ships[i][j].isHitted(i,j)){
                    Pane pane = new Pane();
                    pane.setStyle("-fx-background-color: darkblue;");
                    myField.add(pane, i, j);
                }
            }
    }


    /**
     *  Check input in xTextField and yTextField and make shoot
     */
    @FXML
    private void attackButtonTap(){
        if(enemyOcean == null || enemyOcean.isGameOver())
            return;

        int x,y;
        try{
            y = parseInt(xTextField.getText());
            x = parseInt(yTextField.getText());
            if(x<=0 || x>FIELD_WIDTH || y<=0 || y> FIELD_HEIGHT)
                throw new NumberFormatException();
        }
        catch(NumberFormatException e){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Invalid input...");
            alert.setContentText("Check your coordinates");
            alert.showAndWait();
            return;
        }

        if(enemyOcean.getShipArray()[y - 1][x - 1].isHitted(y - 1, x - 1)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Invalid input...");
            alert.setContentText("This cell already has been hit");
            alert.showAndWait();
            return;
        }

        xTextField.clear();
        yTextField.clear();
        cellButtons[y - 1][x - 1].fire();
    }


    /**
     * Start new single player game when newSingleGameButton tap
     */
    @FXML
    private void newSingleGameButtonTap(){
        multiplayer = false;
        openCreateFieldWindow();
    }


    /**
     * Start new multiplayer when newMultiGameButton tap
     */
    @FXML
    private void newMultiGameButtonTap(){
        multiplayer = true;
        int port;
        String ip = "";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.CANCEL);
        alert.setHeaderText("Waiting for connection");

        try{
            port = parseInt(portTextField.getText());

            if(isServer){
                if(port < 0 || nameTextField.getText().isEmpty())
                    throw new NumberFormatException();
            }
            else{
                ip = ipTextField.getText();
                if(port < 0 || !checkIP(ip) || nameTextField.getText().isEmpty())
                    throw new NumberFormatException();
            }
            playerName = nameTextField.getText();
        }
        catch(NumberFormatException e){
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
            errorAlert.setHeaderText("Invalid input...");
            errorAlert.setContentText("Check your IP, port or name");
            errorAlert.showAndWait();
            return;
        }

        MainController thisController = this;
        final int finalPort = port;
        final String finalIp = ip;
        Thread thread = new Thread(() -> {
            NetworkAccess tempNetwork;
            if(isServer)
                tempNetwork = new Server(finalPort, thisController);
            else
                tempNetwork = new Client(finalIp, finalPort, thisController);

            if(tempNetwork.isCreatedSuccessfully())
                Platform.runLater(() -> {
                network = tempNetwork;
                alert.close();
                openCreateFieldWindow();
                });
            else{
                Platform.runLater(() -> {
                    alert.close();
                    tempNetwork.stopGame();
                });
            }
        });

        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(event -> thread.interrupt());
        alert.show();
        thread.start();
    }

    /**
     * Opens CreateFieldStage
     *
     */
    private void openCreateFieldWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateFieldStage.fxml"));
            Parent root1 = loader.load();
            CreateFieldController createFieldController = loader.getController();

            if(multiplayer){
                createFieldController.doBeforeExit = this::getShipsFromUsersMulti;
                createFieldController.doBeforeCancel = this::endGame;
            }
            else{
                createFieldController.doBeforeExit = this::getShipsFromUsersSingle;
                createFieldController.doBeforeCancel = null;
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Place your ships...");

            stage.setResizable(false);
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Checks if string is IP
     * @param ip
     * @return
     */
    private boolean checkIP(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }


    /**
     * Pass this method to CreateFieldController to grab created by user ships
     * this is single player version
     * @param ships
     */
    private void getShipsFromUsersSingle(Ship[][] ships){
        myOcean = new Ocean(ships, easyModeRadioButton.isSelected());
        enemyOcean = new Ocean(easyModeRadioButton.isSelected());
        ((Stage)((textGameInfo).getScene().getWindow())).setTitle("Battleships the game");
        startNewGame();
    }


    /**
     * Pass this method to CreateFieldController to grab created by user ships
     * this is multiplayer version
     * @param ships
     */
    private void getShipsFromUsersMulti(Ship[][] ships){
        myOcean = new Ocean(ships, false);
        network.sendShips(ships);

        System.out.println("showing");


        Ship[][] opponentShips;
        opponentShips = network.getShips();
        transposeShipMatrix(opponentShips);
        enemyOcean = new Ocean(opponentShips, false);

        network.sendName(playerName);
        enemyName = network.getName();

        System.out.println("closing");

        if(!isServer){
            playersTurn = true;
            ((Stage)((textGameInfo).getScene().getWindow())).setTitle("Your turn");
        }
        else{
            playersTurn = false;
            ((Stage)((textGameInfo).getScene().getWindow())).setTitle("Opponent's turn");
        }

        network.startGame();
        startNewGame();
    }


    /**
     * Because enemy field and player field are transposed we need to
     * transpose matrix of ships to correctly visualize them
     * @param ships
     */
    private void transposeShipMatrix(Ship[][] ships){
        for(int i = 0; i < ships.length; i++)
            for(int j = 0; j < ships[i].length; j++)
                if(i > j){
                    if(!ships[i][j].changed){
                        int tempBow = ships[i][j].getBowColumn();
                        ships[i][j].setBowColumn(ships[i][j].getBowRow());
                        ships[i][j].setBowRow(tempBow);
                        ships[i][j].setHorizontal(!ships[i][j].isHorizontal());
                        ships[i][j].changed = true;
                    }

                    if(!ships[j][i].changed){
                        int tempBow2 = ships[j][i].getBowColumn();
                        ships[j][i].setBowColumn(ships[j][i].getBowRow());
                        ships[j][i].setBowRow(tempBow2);
                        ships[j][i].setHorizontal(!ships[j][i].isHorizontal());
                        ships[j][i].changed = true;
                    }

                    Ship temp = ships[i][j];
                    ships[i][j] = ships[j][i];
                    ships[j][i] = temp;
                }
    }


    /**
     * Clears all fields and prepares controller to the new game
     */
    private void startNewGame(){
        enemyField.getChildren().clear();
        xTextField.clear();
        yTextField.clear();
        textGameInfo.clear();
        textGameInfo.appendText("New game just has started...");

        for(Node node:myField.getChildren())
            node.setStyle("");

        createCellButtons();
        updateEnemyField();
        updateMyField();
        updateGameInfo();
    }


    /**
     * Handles server and client radio buttons switch
     */
    @FXML
    private void multiRadioButtonSwitched(){

        if(serverRadioButton.isSelected()){
            ipTextField.setDisable(true);
            isServer = true;
        }
        else{
            ipTextField.setDisable(false);
            isServer = false;
        }
    }


    /**
     * Handles enemy's shot
     * @param x
     * @param y
     */
    public void enemyShoot(int x, int y){
        shootAtOcean(myOcean, x, y, true);
        updateMyField();
        updateGameInfo();
    }

    /**
     * Handles shot result
     * @param ocean
     * @param x
     * @param y
     * @param enemy
     */
    private void shootAtOcean(Ocean ocean, int x, int y, boolean enemy){
        if(multiplayer){
            String name;
            name = enemy? enemyName : playerName;

            int tempy = y;
            int tempx = x;
            if(enemy){
               tempy = x;
                tempx = y;
            }

            if(ocean.shootAt(x, y)){
                if(ocean.getShipArray()[x][y].isSunk())
                    textGameInfo.appendText("\n" + name + ":(" + (tempx + 1) + "," + (tempy + 1) + ")=ship destroyed");
                else
                    textGameInfo.appendText("\n" + name + ":(" + (tempx + 1) + "," + (tempy + 1) + ")=hit");

            } else
                textGameInfo.appendText("\n" + name + ":(" + (tempx + 1) + "," + (tempy + 1) + ")=miss");
        }
        else {
            if(!enemy){
                if(ocean.shootAt(x, y)){
                    if(ocean.getShipArray()[x][y].isSunk())
                        textGameInfo.appendText("\nHo-ho, you just sank a " +
                            ocean.getShipArray()[x][y].getShipType() + "!");
                    else
                        textGameInfo.appendText("\nNice shot at (" + (x + 1) + "," + (y + 1) + ")!");

                } else
                    textGameInfo.appendText("\nNothing at (" + (x + 1) + "," + (y + 1) + "). Better luck next time...");
            }
            else{
                if(ocean.shootAt(x, y)){

                    if(ocean.getShipArray()[y][x].isSunk())
                        textGameInfo.appendText("\nLooks like enemy sank your " + ocean.getShipArray()[y][x].getShipType() + "...");
                    else
                        textGameInfo.appendText("\nEnemy hit your ship at (" + (y+1) + "," + (x+1) +")");

                } else
                    textGameInfo.appendText("\nToday is your day... Enemy missed at (" + (y+1) + "," + (x+1) +")");
            }
        }

        playersTurn = enemy;
        if(enemy && multiplayer)
            ((Stage)((textGameInfo).getScene().getWindow())).setTitle("Your turn");
        else if (!enemy && multiplayer){
            ((Stage)((textGameInfo).getScene().getWindow())).setTitle("Opponent's turn");
        }
    }


    /**
     * Clears all fields and stops connection
     */
    public void endGame(){
        if(network!= null)
            network.stopGame();
        network = null;
        enemyField.getChildren().clear();
        xTextField.clear();
        yTextField.clear();
        textGameInfo.clear();
        ipTextField.clear();
        portTextField.clear();
        nameTextField.clear();
        ((Stage)((textGameInfo).getScene().getWindow())).setTitle("BattleShips the game");

        for(Node node:myField.getChildren())
            node.setStyle("");
    }
}