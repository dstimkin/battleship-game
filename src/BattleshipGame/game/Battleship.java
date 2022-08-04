package BattleshipGame.game;

public class Battleship extends Ship{

    public Battleship(){
        length = 4;
        hit = new boolean[4];
    }

    @Override
    public ShipType getShipType(){return ShipType.Battleship;}
}
