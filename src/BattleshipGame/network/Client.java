package BattleshipGame.network;

import BattleshipGame.GUI.MainController;

import java.io.*;
import java.net.Socket;

public class Client extends NetworkAccess
{
    public Client(String ip, int port, MainController controller){
        this.controller = controller;
        try{
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            if(clientSocket.isConnected() && in.readLine().equals("Connection created") && !Thread.interrupted())
                createdSuccessfully = true;
        }
        catch(IOException e){
            createdSuccessfully = false;
            stopGame();
        }
    }
}
