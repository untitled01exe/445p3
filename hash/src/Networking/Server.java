package Networking;

import Blockchain.Blockchain;
import com.company.Block;
import com.company.USERS;
import com.company.User;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;

public class Server {

    static HashMap<PublicKey, ClientHandler> clients = new HashMap<>();
    public static Blockchain BLOCKCHAIN;
    public static InetAddress SERVER_ADDRESS;
    public static final int PORT = 8888;

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        BLOCKCHAIN = new Blockchain();
        USERS.initUsers();

            ServerSocket server = new ServerSocket(PORT);
            int counter = 0;
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
    }
}

