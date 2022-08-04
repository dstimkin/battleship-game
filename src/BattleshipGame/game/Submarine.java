package BattleshipGame.game;

public class Submarine  extends Ship {
    public Submarine(){
        length = 1;
        hit = new boolean[1];
    }

    @Override
    public ShipType getShipType(){return ShipType.Submarine;}
}
