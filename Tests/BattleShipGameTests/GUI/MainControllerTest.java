package BattleShipGameTests.GUI;

import BattleshipGame.GUI.MainController;
import BattleshipGame.game.Ocean;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class MainControllerTest
{

    @Test
    void initTest()
    {
        MainController mainController = new MainController();

        assertEquals((Ocean.ROW_SIZE)*(Ocean.COLUMN_SIZE), mainController.cellButtons.length* mainController.cellButtons[0].length);
    }
}
