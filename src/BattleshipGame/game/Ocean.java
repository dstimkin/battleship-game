package BattleshipGame.game;

import java.io.Serializable;
import java.util.Random;

public class Ocean implements Serializable
{
    /**
     * these constats can be used to change field size
     */
    final public static int ROW_SIZE = 10;
    final public static int COLUMN_SIZE = 10;
    final public static int NUMBER_OF_BATTLESHIPS = 1;
    final public static int NUMBER_OF_CRUISERS = 2;
    final public static int NUMBER_OF_DESTROYERS = 3;
    final public static int NUMBER_OF_SUBMARINES = 4;

    private Ship[][] ships = new Ship[ROW_SIZE][COLUMN_SIZE];
    public Ship[][] getShipArray(){return ships;}

    private int shotsFired = 0;
    public int getShotsFired(){return shotsFired;}

    private int hitCount = 0;
    public int getHitCount(){return hitCount;}

    private int shipsSunk = 0;
    public int getShipsSunk(){return shipsSunk;}

    private int afloatBattleships = NUMBER_OF_BATTLESHIPS;
    public int getAfloatBattleships(){return afloatBattleships;}

    private int afloatCruisers = NUMBER_OF_CRUISERS;
    public int getAfloatCruisers(){return afloatCruisers;}

    private int afloatDestroyers = NUMBER_OF_DESTROYERS;
    public int getAfloatDestroyers(){return afloatDestroyers;}

    private int afloatSubmarines = NUMBER_OF_SUBMARINES;
    public int getAfloatSubmarines(){return afloatSubmarines;}

    private boolean easyModeEnabled;


    public Ocean(boolean easyModeEnabled){
        this.easyModeEnabled = easyModeEnabled;

        for (int i = 0; i < COLUMN_SIZE; i++)
            for (int j = 0; j < ROW_SIZE; j++)
                ships[j][i] = new EmptySea();

        placeAllShipsRandomly();
    }

    public Ocean(Ship[][] ships, boolean easyModeEnabled){
        this.easyModeEnabled = easyModeEnabled;
        this.ships = ships;
    }


    /**
     * Puts BattleshipGame.ships into sea
     */
    private void placeAllShipsRandomly(){
        createShip(NUMBER_OF_BATTLESHIPS, ShipType.Battleship);
        createShip(NUMBER_OF_CRUISERS, ShipType.Cruiser);
        createShip(NUMBER_OF_DESTROYERS, ShipType.Destroyer);
        createShip(NUMBER_OF_SUBMARINES, ShipType.Submarine);
    }


    /**
     * Shoots at selected cell
     * If easy mode is enabled call showNearShips
     * @param row
     * @param column
     * @return
     */
    public boolean shootAt(int row, int column){
        shotsFired++;
        if(!(ships[row][column] instanceof EmptySea) && (ships[row][column].isSunk()))
            return false;


        if (ships[row][column].shootAt(row,column))
        {
            hitCount++;
            if (ships[row][column].isSunk()){

                shipsSunk++;
                if(ships[row][column] instanceof Submarine)
                    afloatSubmarines--;
                else if(ships[row][column] instanceof Destroyer)
                    afloatDestroyers--;
                else if(ships[row][column] instanceof Cruiser)
                    afloatCruisers--;
                else if(ships[row][column] instanceof Battleship)
                    afloatBattleships--;

                if(easyModeEnabled)
                    showNearShips(ships[row][column]);
            }

            return true;
        }

        else return false;
    }


    /**
     * Open cells around sank ship
     * @param ship
     */
    private void showNearShips(Ship ship){
        for(int i = 0; i < ship.length; i++){

            int curRow;
            int curColumn;
            if(ship.isHorizontal()){
                curRow = ship.getBowRow();
                curColumn = ship.getBowColumn() + i;
            }
            else{
                curRow = ship.getBowRow() + i;
                curColumn = ship.getBowColumn();
            }

                if(curRow - 1 >= 0)
                    ships[curRow - 1][curColumn].shootAt(curRow - 1,curColumn);

                if(curRow + 1 < ROW_SIZE)
                    ships[curRow + 1][curColumn].shootAt(curRow + 1,curColumn);

                if(curColumn - 1 >= 0)
                    ships[curRow][curColumn - 1].shootAt(curRow,curColumn - 1);

                if(curColumn + 1 < COLUMN_SIZE)
                    ships[curRow][curColumn + 1].shootAt(curRow,curColumn + 1);


                if(curColumn + 1 < COLUMN_SIZE && curRow + 1 < ROW_SIZE)
                    ships[curRow + 1][curColumn + 1].shootAt(curRow + 1,curColumn + 1);

                if(curColumn + 1 < COLUMN_SIZE && curRow - 1 >= 0)
                    ships[curRow - 1][curColumn + 1].shootAt(curRow + 1,curColumn - 1);

                if(curColumn - 1 >= 0 && curRow + 1 < ROW_SIZE)
                    ships[curRow + 1][curColumn - 1].shootAt(curRow - 1,curColumn + 1);

                if(curColumn - 1 >= 0 && curRow - 1 >= 0)
                    ships[curRow - 1][curColumn - 1].shootAt(curRow - 1,curColumn - 1);
        }
    }


    /**
     * Checks if game is over or not
     * @return
     */
    public boolean isGameOver(){return shipsSunk == 10;}


    /**
     * Checks if cell in the see occupied or not
     * @param row
     * @param column
     * @return
     */
    public boolean isOccupied(int row,int column){
        if ((row >= 0) && (row < ROW_SIZE) && (column >= 0) && (column < COLUMN_SIZE))
            return !(ships[row][column] instanceof EmptySea);
        else return false;
    }


    /**
     * Checks if there are BattleshipGame.ships at the adjacent cells
     * @param row
     * @param column
     * @return
     */
    boolean thereIsNoShipsNear(int row, int column){
        if(row > ROW_SIZE - 1 || column > COLUMN_SIZE - 1)
            return false;

        boolean result = true;

        if(isOccupied(row - 1, column)) result = false;
        if(isOccupied(row + 1,column)) result = false;

        if(isOccupied(row,column - 1)) result = false;
        if(isOccupied(row, column + 1)) result = false;

        if(isOccupied(row - 1,column - 1)) result = false;
        if(isOccupied(row + 1, column + 1)) result = false;
        if(isOccupied(row +1, column - 1)) result = false;
        if(isOccupied(row - 1, column + 1)) result = false;


        return result;
    }


    /**
     * Puts numberOfShips BattleshipGame.ships of shipType in the sea
     * @param numerOfShips
     * @param shipType
     */
    private void createShip(int numerOfShips, ShipType shipType){
        Random rnd = new Random();
        int shipsCount = 0;
        Ship ship = null;

        do {
            switch (shipType){
                case Battleship:
                    ship = new Battleship();
                    break;
                case Cruiser:
                    ship = new Cruiser();
                    break;
                case Destroyer:
                    ship = new Destroyer();
                    break;
                case Submarine:
                    ship = new Submarine();
                    break;
            }

            int row = rnd.nextInt(ROW_SIZE);
            int column = rnd.nextInt(COLUMN_SIZE);
            boolean horizontal = rnd.nextBoolean();

            if(ship.okToPlaceShipAt(row, column, horizontal, this)){
                ship.placeShipAt(row,column,horizontal,this);
                shipsCount++;
            }

        }while(shipsCount < numerOfShips);
    }
}
