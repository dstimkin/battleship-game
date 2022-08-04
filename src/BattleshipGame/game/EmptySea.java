package BattleshipGame.game;

public class EmptySea extends Ship {

    public EmptySea()
    {
        length = 1;
    }

    /**
     * Flag to know was there a shot into this cell or not
     */
    boolean hasBeenShooted = false;

    @Override
    public ShipType getShipType(){return ShipType.Empty;}

    @Override
    public boolean shootAt(int row, int column){
        hasBeenShooted = true;
        return false;
    }

    @Override
    public boolean isSunk(){return false;}

    @Override
    public boolean isHitted(int i, int j)
    {
        return hasBeenShooted;
    }

    /**
     * If this cell has been shooted returns "-" else returns "."
     * @return
     */
    @Override
    public String toString()
    {
        return "";
    }
}
