package BattleshipGame.game;

public enum ShipType{
    Battleship(4),
    Cruiser(3),
    Destroyer(2),
    Submarine(1),
    SomeShip(1),
    Empty(1);

    ShipType(int length){
        this.length  = length;
    }

    public final int length;
}