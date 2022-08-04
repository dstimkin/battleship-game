package BattleshipGame.game;

public class Destroyer extends Ship {

    public Destroyer(){
        length = 2;
        hit = new boolean[2];
    }

    /**
     * Returns ship type
     * @return
     */
    @Override
    public ShipType getShipType(){return ShipType.Destroyer;}
}
