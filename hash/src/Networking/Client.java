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
    private static User[] usersCopy;
    private static Blockchain blockchain;
    private static ArrayList<Block> blocks;

    public static void main(String args[]) throws UnknownHostException, IOException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException, ClassNotFoundException {
        crypt = new Crypt();

        Blockchain blockchain = new Blockchain();
        blocks = blockchain.blockchain;
        Block block = new Block(blocks.get(blocks.size()-1).hash);
        usersCopy = Arrays.copyOf(USERS.users, USERS.users.length);

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

            Block curBlock = block;
            curBlock.previousHash = blocks.get(blocks.size()-1).hash;
            if(block.isComplete()){
                System.out.println("Starting a new block");
                try {
                    updateLocalChain();
                    String prevHash = blocks.get(blocks.size()-1).hash;
                    System.out.println("Previous Hash: " + prevHash);
                    curBlock = new Block(prevHash);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int transactionCount = 0;
            while (true) {
                try {

                    // read the message sent to this client
                    // Decrypt based on private key
                    String msg = dis.readUTF();
                    if(msg.equalsIgnoreCase("update")){
                        updateLocalChain();
                    }     else {
                        //String decrpyted = crypt.decryptTransaction(user, msg);
                        System.out.println(msg);
                        //validate the transaction
                        boolean validTransaction = validateTransaction(msg);
                        if (validTransaction && transactionCount < 4) {
                            makeTransaction(msg);
                            transactionCount++;
                        }
                        if (validTransaction && !curBlock.addTransaction(msg, usersCopy)) {
                            //Try to validate block if it is full
                            System.out.println("Block to be added: " + curBlock.toString());
                            boolean validated = validateBlock(curBlock, msg, blocks, blockchain);
                            System.out.println("Block valid? " + validated);
                            //If the block can be validated, then try to publish it to the blockchain
                            if (validated) {
                                boolean published = blockchain.updateChain(curBlock, usersCopy);
                                System.out.println("Published? " + published);
                                //If publishing fails, then the local blockchain is out of date and needs an update
                                if (!published) {
                                    updateLocalChain();
                                    transactionCount = 0;
                                }  else {
                                     dos.writeUTF("update");
                                     dos.flush();
                                     Thread.sleep(1000);
                                     String prevHash = curBlock.hash;
                                     curBlock = new Block(prevHash);
                                     transactionCount = 0;
                                }

                            } else  {
                                updateLocalChain();
                                String prevHash = blocks.get(blocks.size()-1).hash;
                                curBlock = new Block(prevHash);
                                transactionCount = 0;
                            }
                        }
                    }





                } catch (IOException | InterruptedException e) {

                    e.printStackTrace();
                }

            }
        });

        sendTransaction.start();
        readTransaction.start();

    }

    private static void updateLocalChain() throws IOException {
        blockchain = new Blockchain();
        blocks = blockchain.blockchain;
        String[] newTotals = blocks.get(blocks.size()-1).totals.split(" ");
        for(String total : newTotals){
            String[] userBal = total.split(":");
            String user = userBal[0];
            String bal = userBal[1];
            for(User u: usersCopy){
                if (u.getUsername().equalsIgnoreCase(user)){
                    u.coin = Integer.parseInt(bal);
                }
            }
        }
    }

    public static boolean validateTransaction(String msg){
        boolean validateTransaction = false;

            String[] splitMessage = msg.split(" ");
            if(splitMessage.length > 3) {
                User sent = USERS.getUser(splitMessage[0]);
                int transAmount = Integer.parseInt(splitMessage[3]);

                for (User u : usersCopy) {
                    if (u == sent && (u.coin - transAmount) > 0) {
                        validateTransaction = true;
                    }
                }
            }
        return validateTransaction;
    }

    public static void makeTransaction(String msg){
        String[] splitTransaction = msg.split(" ");
        User sender = USERS.getUser(splitTransaction[0]);
        int transAmount = Integer.parseInt(splitTransaction[3]);
        User recipient = USERS.getUser(splitTransaction[5]);
        for(User u : usersCopy){
            if(u==sender){
                u.removeFunds(transAmount);
                recipient.addFunds(transAmount);
            }
        }
    }

    public static synchronized boolean validateBlock(Block block, String msg, ArrayList<Block> blocks, Blockchain blockchain) throws InterruptedException {
            // try to mine block
            block.hash = block.mineBlock(3);
            blocks.add(block);
            boolean validated = blockchain.validateBlock(block);
            return validated;
        }

}
