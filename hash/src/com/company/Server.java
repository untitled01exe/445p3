package com.company;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    static HashMap<PublicKey, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args){
     try{
        BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
        int port;
        System.out.println("Enter Port:");
        port = Integer.parseInt(stdin.readLine());
        ServerSocket server=new ServerSocket(port);
        int counter=0;
        System.out.println("Server Started ....");
        while(true){
            counter++;
            Socket socket = server.accept();  //server accept the client connection request
            System.out.println(" >> " + "Client No:" + counter + " started");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            Crypt crypt = new Crypt();
            PublicKey publicKey = crypt.genPublic();
            PrivateKey privateKey = crypt.genSecret();

            ClientHandler clientHandler = new ClientHandler(socket, String.valueOf(counter), dis, dos, publicKey, privateKey);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
            clients.put(publicKey, clientHandler);
        }
    }catch(Exception e){
        System.out.println(e);
    }
}


}
