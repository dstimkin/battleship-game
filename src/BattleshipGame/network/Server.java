package BattleshipGame.network;

import BattleshipGame.GUI.MainController;

import java.io.*;
import java.net.ServerSocket;

public class Server extends NetworkAccess{

    public Server(int port, MainController controller){
        this.controller = controller;
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Connection created");

            if(!Thread.interrupted())
                createdSuccessfully = true;
        }
        catch(IOException e){
            createdSuccessfully = false;
            stopGame();
        }
    }
}
