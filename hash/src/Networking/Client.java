package Networking;

import Blockchain.Block;
import Blockchain.Blockchain;
import Blockchain.Transaction;
import com.company.Crypt;
import com.company.TransactionBuilder;
import com.company.USERS;
import com.company.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static Networking.Server.BLOCKCHAIN;

public class Client {

    static int ServerPort;
    static Crypt crypt;
    static User user;
    private static User[] usersCopy = Arrays.copyOf(USERS.users, USERS.users.length);
    private static Blockchain blockchain;

    public static void main(String args[]) throws UnknownHostException, IOException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException, ClassNotFoundException {
        /*
        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter Server Port: ");
        ServerPort = Integer.parseInt(stdin.nextLine());

        //We really should do something better here but it works for our purposes
        System.out.println("Enter your username: ");
        String username = stdin.nextLine();
        User sender = USERS.getUser(username);
        User user = sender;

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

        blockchain = new Blockchain();

        while (true) {
            try {
                // build transaction for broadcast
                System.out.println("Enter username to transfer coins to:");
                User recipient = USERS.getUser(stdin.nextLine());
                System.out.printf("Enter desired transfer amount");
                int transactionAmount = Integer.parseInt(stdin.nextLine());
                Transaction t = new Transaction(sender, recipient, transactionAmount);
                String transaction = t.transaction;
                // write on the output stream
                //new Thread(new CatchupRunnable(s.getLocalPort(), username, user, oos, ois, blockchain));
                new Thread(new SenderRunnable(recipient, transaction, s)).start();
                new Thread(new ClientRunnable(s, username, ois, oos, user, blockchain)).start();
                //dos.writeUTF(transaction);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
                e.printStackTrace();
            }
        } */


        TransactionBuilder transactionBuilder = new TransactionBuilder();
        crypt = new Crypt();

        Blockchain blockchain = new Blockchain();
        ArrayList<Block> blocks = blockchain.blockchain;
        Block block = new Block(blocks.get(blocks.size()-1).hash);

        Scanner stdin = new Scanner(System.in);
        System.out.println("Enter Server Port: ");
        ServerPort = Integer.parseInt(stdin.nextLine());

        //We really should do something better here but it works for our purposes
        System.out.println("Enter your username: ");
        String username = stdin.nextLine();
        User sender = USERS.getUser(username);
        User user = sender;

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
                    Transaction t = new Transaction(sender, recipient, transactionAmount);
                    String transaction = t.transaction;
                    // write on the output stream
                    dos.writeUTF(transaction);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread readTransaction = new Thread(() -> {

            while (true) {
                try {

                    // read the message sent to this client
                    // Decrypt based on private key
                    String msg = dis.readUTF();
                    //String decrpyted = crypt.decryptTransaction(user, msg);
                    System.out.println(msg);
                    //validate the transaction
                    if(validateTransaction(msg) && !block.addTransaction(msg)){
                        //Try to validate block if it is full
                        boolean validated = validateBlock(block, msg, blocks, blockchain);
                        System.out.println("Block valid? " + validated);
                        //If the block can be validated, then try to publish it to the blockchain
                        if(validated) {
                            boolean published = blockchain.updateChain(block);
                            System.out.println("Published? " + published);
                            //If publishing fails, then the local blockchain is out of date and needs an update
                            if(!published){
                                updateLocalChain();
                        }

                        } else {
                            String prevHash = block.hash;
                        }
                    }




                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        });

        sendTransaction.start();
        readTransaction.start();

    }

    private static void updateLocalChain() throws IOException {
        blockchain = new Blockchain();
    }

    public static boolean validateTransaction(String msg){
        String[] splitMessage = msg.split(" ");
        User sent = USERS.getUser(splitMessage[0]);
        int transAmount = Integer.parseInt(splitMessage[3]);
        User receive = USERS.getUser(splitMessage[5]);
        boolean validateTransaction = false;

        for(User u : usersCopy){
            if(u == sent && (u.coin - transAmount) > 0){
                u.removeFunds(transAmount);
                receive.addFunds(transAmount);
                validateTransaction = true;
            }
        }
        return validateTransaction;
    }

    public static boolean validateBlock(Block block, String msg, ArrayList<Block> blocks, Blockchain blockchain){
            // try to mine block and broadcast
            block.hash = block.mineBlock(3);
            blocks.add(block);
            boolean validated = blockchain.validateChain(blocks);
            return validated;
        }

}
