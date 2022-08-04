package BattleshipGame.network;

import BattleshipGame.GUI.MainController;
import BattleshipGame.game.Ship;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

/**
 * Class implements network communication between players
 */
public abstract class NetworkAccess
{
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    MainController controller;

    boolean createdSuccessfully = false;
    public boolean isCreatedSuccessfully(){ return createdSuccessfully;}

    boolean gameStopped = false;
    public boolean isGameStopped(){ return gameStopped;}

    /**
     * Start to listen signals from second player
     */
    public void startGame(){
        new Thread( () -> {
            while(!gameStopped)
            {
                try{
                    int x = Integer.parseInt(in.readLine());
                    int y = Integer.parseInt(in.readLine());
                    Platform.runLater(() -> controller.enemyShoot(y, x));
                } catch(IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    /**
     * Sends information about completed shot to second player
     * @param x
     * @param y
     */
    public void makeShoot(int x, int y){
        out.println(x);
        out.println(y);
    }

    /**
     * Closes socket and ends current multiplayer session
     */
    public void stopGame(){
        gameStopped = true;

        if(out != null)
            out.close();
        try{
            if(in != null)
                in.close();
            if(clientSocket != null)
                clientSocket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * send created matrix of ships tp the second player
     * @param ships
     */
    public void sendShips(Ship[][] ships){
        try{
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(ships);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * get created by second player matrix of ships
     * @return
     */
    public Ship[][] getShips(){
        try{
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            Ship[][] ships = (Ship[][])objectIn.readObject();
            return ships;
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public void sendName(String name){
        out.println(name);
    }

    public String getName(){
        try{
            return in.readLine();
        }
        catch(IOException e){
            e.printStackTrace();
            return "";
        }
    }
}
