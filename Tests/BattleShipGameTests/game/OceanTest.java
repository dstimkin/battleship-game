package BattleShipGameTests.game;

import BattleshipGame.game.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class OceanTest
{

    @Test
    void testShipsCreation()
    {
        Ocean ocean = new Ocean(false);

        ArrayList<Ship> ships = new ArrayList<>();

        int battleshipCounter = 0;
        int cruiserCounter = 0;
        int destroyerCounter = 0;
        int submarineCounter = 0;
        for(Ship[] line: ocean.getShipArray())
            for(Ship ship: line)
                if(!ships.contains(ship))
                {
                    ships.add(ship);
                    if(ship instanceof Battleship)
                        battleshipCounter++;
                    else if(ship instanceof Cruiser)
                        cruiserCounter++;
                    else if(ship instanceof Destroyer)
                        destroyerCounter++;
                    else if(ship instanceof Submarine)
                        submarineCounter++;
                }

        assertEquals(Ocean.NUMBER_OF_BATTLESHIPS,battleshipCounter);
        assertEquals(Ocean.NUMBER_OF_CRUISERS,cruiserCounter);
        assertEquals(Ocean.NUMBER_OF_DESTROYERS,destroyerCounter);
        assertEquals(Ocean.NUMBER_OF_SUBMARINES,submarineCounter);
    }

    @Test
    void shootTest()
    {
        Ocean ocean = new Ocean(false);

        for(int i = 0; i < Ocean.ROW_SIZE; i++)
        {
            for(int j = 0; j < Ocean.COLUMN_SIZE; j++)
            {
                ocean.shootAt(i, j);
            }
        }

        for(int i = 0; i < Ocean.ROW_SIZE; i++)
            for(int j = 0; j < Ocean.COLUMN_SIZE; j++)
                if(!(ocean.getShipArray()[i][j] instanceof EmptySea) && !ocean.getShipArray()[i][j].isSunk())
                    Assert.fail();

        assertEquals(Ocean.ROW_SIZE*Ocean.COLUMN_SIZE, ocean.getShotsFired());
    }


}
