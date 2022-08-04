package BattleshipGame.game;

import java.io.Serializable;

public class Ship implements Serializable{

    public boolean changed;
    private int bowRow;
    public int getBowRow(){return bowRow;}
    public void setBowRow(int row){bowRow = row;}


    private int bowColumn;
    public int getBowColumn(){return bowColumn;}
    public void setBowColumn(int column){bowColumn = column;}


    /**
     * Length of the ship
     */
    int length;
    public int getLength(){return length;}


    private boolean horizontal;
    public void setHorizontal(boolean horizontal) {this.horizontal = horizontal;}
    public boolean isHorizontal() {return horizontal;}

    /**
     * Hit cells of the ship
     */
    public boolean [] hit = new boolean[4];

    public ShipType getShipType(){return ShipType.SomeShip;}

    /**
     *
     * @param row
     * @param column
     * @param horizontal
     * @param ocean
     * @return
     */
    boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean){
        if(ocean.isOccupied(row, column))
            return false;

        if(horizontal){
            for (int i = 0; i < length; i++)
                if (!ocean.thereIsNoShipsNear(row, column +i))
                    return false;
        }
        else{
            for (int i = 0; i < length; i++)
                if (!ocean.thereIsNoShipsNear(row + i, column))
                    return false;
        }

        return true;
    }

    /**
     * Places ship at selected cell
     * @param row
     * @param column
     * @param horizontal
     * @param ocean
     */
    void placeShipAt(int row, int column, boolean horizontal, Ocean ocean){
        bowRow = row;
        bowColumn = column;

        setHorizontal(horizontal);

        for (int i = 0; i < length; i++){
            if(horizontal)
                ocean.getShipArray()[row][column + i] = this;
            else
                ocean.getShipArray()[row + i][column] = this;
        }
    }

    /**
     * Hit selected cell of the ship
     * @param row
     * @param column
     * @return
     */
    boolean shootAt(int row, int column){
        if (horizontal)
            for (int i = 0; i < length; i++){
                if (row == bowRow && column == bowColumn + i){
                    hit[i] = true;
                    return true;
                }
            }
        else
            for (int i = 0; i < length; i++){
                if (row == bowRow + i && column == bowColumn){
                    hit[i] = true;
                    return true;
                }
            }

        return false;
    }

    /**
     * Checks if ship has been sunk or not
     * @return
     */
    public boolean isSunk(){
        for (int i = 0; i < length; i++){
            if(!hit[i])
                return false;
        }

        return true;
    }

    public boolean isHitted(int i, int j){
        if ((isHorizontal() && hit[j - getBowColumn()]) ||
                (!isHorizontal() && hit[i - getBowRow()]))
           return true;
        else return false;
    }

    /**
     * Returns x if ship has been sunk and "s" if this cell of the ship was hit
     * @return
     */
    @Override
    public String toString() {return isSunk()? "X" : "";}
}