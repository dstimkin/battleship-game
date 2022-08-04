package BattleshipGame.game;

public class Cruiser extends Ship{

    public Cruiser(){
        length = 3;
        hit = new boolean[3];
    }

    @Override
    public ShipType getShipType(){return ShipType.Cruiser;}
}
