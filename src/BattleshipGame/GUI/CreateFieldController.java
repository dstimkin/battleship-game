package BattleshipGame.GUI;

import BattleshipGame.game.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.*;
import java.util.function.Consumer;

import static java.lang.Integer.*;
import static java.lang.Integer.parseInt;

/**
 * Controller for ships allocation
 */
public class CreateFieldController{

    final int FIELD_HEIGHT = 10;
    final int FIELD_WIDTH = 10;

    private boolean locationAllowed = true;
    private boolean locatingShips = false;

    private Point selectedCell;
    private final Pane cursor = new Pane();
    private ShipType curShip = ShipType.Battleship;

    private Pane[][] ocean;
    private final Pane[] possibleSelectedCells = new Pane[4];
    private Ship[][] ships;
    private Text[] labels;
    private final String[] labelTitles = new String[]{"Battleships ", "Cruisers ", "Destroyers ", "Submarines "};

    int numberOfBattleships = 1;
    int numberOfCruisers = 2;
    int numberOfDestroyers = 3;
    int numberOfSubmarines = 4;

    @FXML private GridPane gameField;
    @FXML private Text battleshipLabel;
    @FXML private Text destroyerLabel;
    @FXML private Text cruiserLabel;
    @FXML private Text submarineLabel;
    @FXML private Button okButton;
    @FXML private Button deselectButton;
    @FXML private TextField x1TextField;
    @FXML private TextField x2TextField;
    @FXML private TextField y1TextField;
    @FXML private TextField y2TextField;

    public Consumer<Ship[][]> doBeforeExit;
    public Runnable doBeforeCancel;

    @FXML
    void initialize() {
        ocean = new Pane[FIELD_HEIGHT][FIELD_WIDTH];
        ships = new Ship[FIELD_HEIGHT][FIELD_WIDTH];
        for (int i = 0; i < FIELD_HEIGHT; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                ships[i][j] = new EmptySea();

        cursor.setStyle("-fx-background-color: darkblue;");
        labels = new Text[] { battleshipLabel, cruiserLabel, destroyerLabel, submarineLabel };

        selectLabel(battleshipLabel);
        updateLabels();
    }


    /**
     * Finds current x coordinate depend on cursor position
     * @param position
     * @return
     */
    public int findX(double position) {
        double cellWidth = gameField.getWidth() / FIELD_WIDTH;
        for (int i = 0; i < FIELD_WIDTH; i++)
            if (position > i * cellWidth && position <= cellWidth * (i + 1))
                return i;

        return 0;
    }


    /**
     * Finds current y coordinate depend on cursor position
     * @param position
     * @return
     */
    public int findY(double position) {
        double cellHeight = gameField.getHeight() / FIELD_HEIGHT;
        for (int i = 0; i < FIELD_HEIGHT; i++)
            if (position > i * cellHeight && position <= cellHeight * (i + 1))
                return i;

        return 0;
    }


    /**
     * Updates gameField and put ship into ships
     * @param x
     * @param y
     * @param ship
     */
    private void setShips(int x, int y, Ship ship) {
        Pane pane = new Pane();
        ships[x][y] = ship;

        if (ship.getShipType() == ShipType.Empty)
            pane.setStyle("-fx-background-color: rgba(30,144,255,0.8);");
        else
            pane.setStyle("-fx-background-color: darkblue;");

        gameField.add(pane, x, y);
        ocean[x][y] = pane;
    }


    /**
     * Handles mouse entry in cell
     * @param event
     */
    @FXML
    private void mouseEnteredCell(MouseEvent event) {
        gameField.getChildren().remove(cursor);
        for (Pane possibleSelectedCell : possibleSelectedCells)
            gameField.getChildren().removeAll(possibleSelectedCell);

        int x = findX(event.getX());
        int y = findY(event.getY());

        if (!OkToPlaceShip(x, y) || curShip == ShipType.Empty)
            return;

        if (locatingShips) {
            if (y == selectedCell.getY()) {
                if (x - selectedCell.getX() < 0 && selectedCell.getX() + 1 >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++) {
                        possibleSelectedCells[i - 1] = new Pane();
                        possibleSelectedCells[i - 1].setStyle("-fx-background-color: mediumpurple;");
                        gameField.add(possibleSelectedCells[i - 1], ((int) selectedCell.getX()) - i,
                                (int) selectedCell.getY());
                    }
                } else if (x - selectedCell.getX() > 0 && FIELD_WIDTH - selectedCell.getX() >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++) {
                        possibleSelectedCells[i - 1] = new Pane();
                        possibleSelectedCells[i - 1].setStyle("-fx-background-color: mediumpurple;");
                        gameField.add(possibleSelectedCells[i - 1], ((int) selectedCell.getX()) + i,
                                (int) selectedCell.getY());
                    }
                }
            } else if (x == selectedCell.getX()) {
                if (y - selectedCell.getY() < 0 && selectedCell.getY() + 1 >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++) {
                        possibleSelectedCells[i - 1] = new Pane();
                        possibleSelectedCells[i - 1].setStyle("-fx-background-color: mediumpurple;");
                        gameField.add(possibleSelectedCells[i - 1], (int) selectedCell.getX(),
                                ((int) selectedCell.getY()) - i);
                    }
                } else if (y - selectedCell.getY() > 0 && FIELD_HEIGHT - selectedCell.getY() >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++) {
                        possibleSelectedCells[i - 1] = new Pane();
                        possibleSelectedCells[i - 1].setStyle("-fx-background-color: mediumpurple;");
                        gameField.add(possibleSelectedCells[i - 1], (int) selectedCell.getX(),
                                ((int) selectedCell.getY()) + i);
                    }
                }
            }
        } else if (ocean[x][y] == null){
            gameField.getChildren().remove(cursor);
            gameField.add(cursor, x, y);
        }

    }


    /**
     * Handles mouse exit from gameGrid
     */
    @FXML
    private void mouseExitedFromGrid() {
        gameField.getChildren().remove(cursor);
        for (Pane possibleSelectedCell : possibleSelectedCells)
            gameField.getChildren().removeAll(possibleSelectedCell);
    }


    /**
     * Handles mouse click on cell
     * @param event
     */
    @FXML
    private void mouseClickedOnCell(MouseEvent event) {
        if (!locationAllowed || curShip == ShipType.Empty)
            return;

        int x = findX(event.getX());
        int y = findY(event.getY());

        placeShip(x,y);
    }


    /**
     * Handles click on placeButton
     */
    @FXML
    private void placeButtonClick(){
        if(!okButton.isDisabled())
            return;

        int x1,x2,y1,y2;
        try{
            x1 = parseInt(x1TextField.getText()) - 1;
            y1 = parseInt(y1TextField.getText()) - 1;
            x2 = parseInt(x2TextField.getText()) - 1;
            y2 = parseInt(y2TextField.getText()) - 1;
            if(x1<0 || x1>=FIELD_WIDTH || y1<0 || y1>= FIELD_HEIGHT)
                throw new IllegalArgumentException();

            if(x2<0 || x2>=FIELD_WIDTH || y2<0 || y2>= FIELD_HEIGHT)
                throw new IllegalArgumentException();

            if(x1 > x2){
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if(y1 > y2){
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }

            if(x1 == x2){
                if(Math.abs(y2 - y1) + 1 != curShip.length)
                    throw new IllegalArgumentException();

                for(int i = 0; i < curShip.length; i++)
                    if (ocean[x1][y1 + i] != null)
                        throw new IllegalArgumentException();
            }
            else if(y1 == y2){
                if(Math.abs(x2 - x1) + 1 != curShip.length)
                    throw new IllegalArgumentException();

                for(int i = 0; i < curShip.length; i++)
                    if (ocean[x1 + i][y1] != null)
                        throw new IllegalArgumentException();
            }
            else
                throw new IllegalArgumentException();
        }
        catch(IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Invalid input...");
            alert.setContentText("Check your coordinates");
            alert.showAndWait();
            return;
        }

        if(!deselectButton.isDisabled())
            deselectButtonClick();

        if(curShip == ShipType.Submarine)
            placeShip(x1, y1);
        else {
            placeShip(x1,y1);
            placeShip(x2,y2);
        }
        x1TextField.clear();
        x2TextField.clear();
        y1TextField.clear();
        y2TextField.clear();
    }


    /**
     * Places ship to the given coordinates
     * @param x
     * @param y
     */
    private void placeShip(int x, int y){
        if (!OkToPlaceShip(x, y) || ocean[x][y] != null)
            return;

        if (locatingShips) {
            Ship ship = ships[(int) selectedCell.getX()][(int) selectedCell.getY()];
            if (y == selectedCell.getY()) {
                if (x - selectedCell.getX() < 0 && selectedCell.getX() + 1 >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++)
                        setShips(((int) selectedCell.getX()) - i, (int) selectedCell.getY(), ship);

                    configureShip(((int) selectedCell.getX()) - curShip.length + 1, y, ship);
                } else if (x - selectedCell.getX() > 0 &&
                        FIELD_WIDTH - selectedCell.getX() >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++)
                        setShips(((int) selectedCell.getX()) + i, (int) selectedCell.getY(), ship);

                    configureShip(((int) selectedCell.getX()) + curShip.length - 1, y, ship);
                }
            } else if (x == selectedCell.getX()) {
                if (y - selectedCell.getY() < 0 && selectedCell.getY() + 1 >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++)
                        setShips((int) selectedCell.getX(), ((int) selectedCell.getY()) - i, ship);

                    configureShip(x, ((int) selectedCell.getY()) - curShip.length + 1, ship);
                } else if (y - selectedCell.getY() > 0 &&
                        FIELD_HEIGHT - selectedCell.getY() >= curShip.length) {
                    for (int i = 1; i < curShip.length; i++)
                        setShips((int) selectedCell.getX(), ((int) selectedCell.getY()) + i, ship);

                    configureShip(x, ((int) selectedCell.getY()) + curShip.length - 1, ship);
                }
            }
        } else {
            selectedCell = new Point(x, y);
            locatingShips = true;
            deselectButton.setDisable(false);

            Ship ship;
            if (curShip == ShipType.Submarine){
                ship = new Submarine();
            }
            else if (curShip == ShipType.Cruiser){
                ship = new Cruiser();
            }
            else if (curShip == ShipType.Destroyer){
                ship = new Destroyer();
            }
            else{
                ship = new Battleship();
            }

            setShips(x, y, ship);
            if (curShip == ShipType.Submarine)
                configureShip(x, y, ship);
        }
    }


    /**
     * Checks if it is possible to place ship at cell
     * @param x
     * @param y
     * @return
     */
    private boolean OkToPlaceShip(int x, int y) {
        if (!locatingShips)
            return true;

        if (y == selectedCell.getY()) {
            if (x - selectedCell.getX() < 0 && selectedCell.getX() + 1 >= curShip.length) {
                for (int i = 1; i < curShip.length; i++)
                    if (ocean[((int) selectedCell.getX()) - i][(int) selectedCell.getY()] != null)
                        return false;
            } else if (x - selectedCell.getX() > 0 && FIELD_WIDTH - selectedCell.getX() >= curShip.length) {
                for (int i = 1; i < curShip.length; i++)
                    if (ocean[((int) selectedCell.getX()) + i][(int) selectedCell.getY()] != null)
                        return false;
            }
        } else if (x == selectedCell.getX()) {
            if (y - selectedCell.getY() < 0 && selectedCell.getY() + 1 >= curShip.length) {
                for (int i = 1; i < curShip.length; i++)
                    if (ocean[(int) selectedCell.getX()][((int) selectedCell.getY()) - i] != null)
                        return false;
            } else if (y - selectedCell.getY() > 0 && FIELD_HEIGHT - selectedCell.getY() >= curShip.length) {
                for (int i = 1; i < curShip.length; i++)
                    if (ocean[(int) selectedCell.getX()][((int) selectedCell.getY()) + i] != null)
                        return false;
            }
        }

        return true;
    }


    /**
     * Sets ship's parameters and checks current allocation progress
     * @param x2
     * @param y2
     * @param ship
     */
    private void configureShip(int x2, int y2, Ship ship) {
        locatingShips = false;
        deselectButton.setDisable(true);

        decreaseNumberOfShips();
        updateLabels();
        createBorder((int) selectedCell.getX(), (int) selectedCell.getY(), x2, y2);

        ship.setHorizontal(y2 != selectedCell.getY());
        ship.setBowRow((int) Math.min(selectedCell.getX(), x2));
        ship.setBowColumn((int) Math.min(selectedCell.getY(), y2));

        if (curShip == ShipType.Submarine && numberOfSubmarines == 0){
            submarineLabel.setDisable(true);
            selectNextShip();
        }
        else if (curShip == ShipType.Cruiser && numberOfCruisers == 0){
            cruiserLabel.setDisable(true);
            selectNextShip();
        }
        else if (curShip == ShipType.Destroyer && numberOfDestroyers == 0){
            destroyerLabel.setDisable(true);
            selectNextShip();
        }
        else if (curShip == ShipType.Battleship && numberOfBattleships == 0) {
            battleshipLabel.setDisable(true);
            selectNextShip();
        }

        if(numberOfBattleships == 0 && numberOfDestroyers == 0 && numberOfCruisers == 0 && numberOfSubmarines == 0){
            locationAllowed = false;
            okButton.setDisable(false);
        }
    }


    /**
     * Surrounds ship from every side
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    private void createBorder(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        for (int i = x1; i <= min(x2 + 1, FIELD_HEIGHT - 1); i++)
            disableCell(i, y1 - 1);

        for (int i = y1; i <= min(y2 + 1, FIELD_WIDTH - 1); i++)
            disableCell(x2 + 1, i);

        for (int i = max(x1 - 1, 0); i <= x2; i++)
            disableCell(i, y2 + 1);

        for (int i = max(y1 - 1, 0); i <= y2; i++)
            disableCell(x1 - 1, i);
    }


    /**
     * Disable cell
     * @param x
     * @param y
     */
    private void disableCell(int x, int y) {
        if (x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT && ocean[x][y] == null)
            setShips(x, y, new EmptySea());
    }


    /**
     * Selects next label
     */
    void selectNextShip(){
        if(numberOfBattleships != 0){
            selectLabel(labels[0]);
            curShip = ShipType.Battleship;
        }
        else if(numberOfCruisers != 0){
            selectLabel(labels[1]);
            curShip = ShipType.Cruiser;
        }
        else if(numberOfDestroyers != 0){
            selectLabel(labels[2]);
            curShip = ShipType.Destroyer;
        }
        else if(numberOfSubmarines != 0){
            selectLabel(labels[3]);
            curShip = ShipType.Submarine;
        }
        else{
            selectLabel(null);
            curShip = ShipType.Empty;
        }
    }


    /**
     * Select given label
     * @param text
     */
    private void selectLabel(Text text) {
        for (Text label : labels)
            label.setFont(Font.font("Helvetica", FontWeight.THIN, 18));

        if (text != null)
            text.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 28));
    }


    /**
     * Decreases counter of curShip
     */
    private void decreaseNumberOfShips(){
        if (curShip.equals(ShipType.Submarine)){
            numberOfSubmarines--;
        }
        else if (curShip.equals(ShipType.Cruiser)){
            numberOfCruisers--;
        }
        else if (curShip.equals(ShipType.Destroyer)){
            numberOfDestroyers--;
        }
        else if (curShip.equals(ShipType.Battleship)){
            numberOfBattleships--;
        }
    }


    /**
     * Updates values of labels
     */
    private void updateLabels(){
        battleshipLabel.setText(labelTitles[0] + numberOfBattleships);
        cruiserLabel.setText(labelTitles[1] + numberOfCruisers);
        destroyerLabel.setText(labelTitles[2] + numberOfDestroyers);
        submarineLabel.setText(labelTitles[3] + numberOfSubmarines);
    }


    /**
     * Handles click on labels
     * @param event
     */
    @FXML
    private void shipWasSelected(MouseEvent event) {
        var prevType = curShip;
        if (event.getSource().equals(battleshipLabel)) {
            curShip = ShipType.Battleship;
        } else if (event.getSource().equals(destroyerLabel)) {
            curShip = ShipType.Destroyer;
        } else if (event.getSource().equals(cruiserLabel)) {
            curShip = ShipType.Cruiser;
        } else {
            curShip = ShipType.Submarine;
        }

        if (prevType != curShip && locatingShips) {
            deselectButtonClick();
            curShip = ShipType.Empty;
        }

        selectLabel((Text) event.getSource());
    }


    /**
     * Handles deselectButton click
     */
    @FXML
    private void deselectButtonClick(){
        if(!locatingShips)
            return;

        int x = (int) selectedCell.getX();
        int y = (int) selectedCell.getY();
        locatingShips = false;
        deselectButton.setDisable(true);

        gameField.getChildren().remove(ocean[x][y]);
        ocean[x][y] = null;
        selectedCell = null;

        gameField.getChildren().remove(cursor);
        for (Pane possibleSelectedCell : possibleSelectedCells)
            gameField.getChildren().removeAll(possibleSelectedCell);
    }


    /**
     * Handles click on okButton
     */
    @FXML
    private void okButtonClick() {
        ((Stage)((okButton).getScene().getWindow())).setTitle("Waiting for opponent...");
        Platform.runLater(() -> {
            doBeforeExit.accept(ships);
            ((Stage) ((okButton).getScene().getWindow())).close();
        });
    }


    /**
     * Handles click on cancelButton
     */
    @FXML
    private void cancelButtonClick() {
        if(doBeforeCancel != null)
            doBeforeCancel.run();

        ((Stage)((okButton).getScene().getWindow())).close();
    }
}
