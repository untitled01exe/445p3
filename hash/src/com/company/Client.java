package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Client {

    static int ServerPort;

    public static void main(String args[]) throws UnknownHostException, IOException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException {
        TransactionBuilder transactionBuilder = new TransactionBuilder();

        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter Server Port: ");
        ServerPort = Integer.parseInt(stdin.nextLine());

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        Thread sendTransaction = new Thread(() -> {
            while (true) {
                try {
                    // build transaction for broadcast
                    System.out.println("Enter username to transfer coins to:");
                    User recipient = USERS.getUser(stdin.nextLine());
                    System.out.printf("Enter desired transfer amount");
                    int transactionAmount = Integer.parseInt(stdin.nextLine());
                    //String transaction = transactionBuilder.buildTransaction(InetAddress.getLocalHost().getHostAddress(), recipient, transactionAmount);
                    // write on the output stream
                    //dos.writeUTF(transaction);
                    dos.writeUTF("");
                } catch (IOException e) {
                    e.printStackTrace();
                } /*catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }*/
            }
        });

        Thread readTransaction = new Thread(() -> {

            while (true) {
                try {

                    // read the message sent to this client
                    //TODO: Decrypt based on private key
                    String msg = dis.readUTF();
                    System.out.println(msg);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });

        sendTransaction.start();
        readTransaction.start();
    }

}
