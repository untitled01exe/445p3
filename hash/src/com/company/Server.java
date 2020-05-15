package com.company;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;

public class Server {

    static HashMap<PublicKey, ClientHandler> clients = new HashMap<>();
    public static InetAddress SERVER_ADDRESS;
    public static final int PORT = 8888;

    public static void main(String[] args){
     try{
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        ServerSocket server = new ServerSocket(PORT);
        SERVER_ADDRESS = InetAddress.getLocalHost();
        int counter=0;
        System.out.println("Server Started ....");
        while(true){
            counter++;
            Socket socket = server.accept();  //server accept the client connection request
            System.out.println(" >> " + "Client No:" + counter + " started");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            String userName = "u" + counter;

            User user = USERS.getUser(userName);

            ClientHandler clientHandler = new ClientHandler(socket, userName, dis, dos, user);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
            clients.put(user.pk, clientHandler);
        }
    }catch(Exception e){
        System.out.println(e);
    }
}


}
