package Blockchain;

import com.company.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Blockchain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;
    static TransactionBuilder tb;
    ReentrantLock rl;
    FileWriter fw;
    File file;

    public Blockchain() throws IOException {
        file = new File("chain.txt");
        Hash h = new Hash();

        if (file.createNewFile())
        {
            USERS.initUsers();
            //creates new file, writes initial block
            Block initBlock = Block.initBlock();
            initBlock.writeBlock();
        } else {
            USERS.initUsers();
            //Read blocks in from file to ArrayList
            BufferedReader br = new BufferedReader(new FileReader("chain.txt"));
            String line = "";
            String totals = "";
            String prevHash = "";
            Long timestamp = Long.valueOf(0);
            String transData = "";

            Long mostRecentTime = Long.MIN_VALUE;

            while((line=br.readLine())!=null){
                String[] splitLine = line.split(" ");
                if(splitLine.length == 4){
                    if(timestamp > mostRecentTime) {
                        totals = line;
                        for (String s : splitLine) {
                            String[] userBal = s.split(":");
                             for(User u : USERS.users){
                                if(u.getUsername().equals(userBal[0])){
                                    u.coin = Integer.parseInt(userBal[1]);
                                }
                            }
                        }
                        mostRecentTime = timestamp;
                    }
                } else if(splitLine[0].equalsIgnoreCase("previous")){
                    //Last part of a block is the previous hash
                    prevHash = splitLine[2];
                    Block b = new Block(transData, prevHash, timestamp, totals);
                    blockchain.add(b);
                    totals = "";
                    prevHash = "";
                    transData = "";
                } else if(splitLine[0].equalsIgnoreCase("time")){
                    //first part of a block is the timestamp
                    timestamp = Long.parseLong(splitLine[2]);
                } else if(splitLine[0].equalsIgnoreCase("inittrans:") || splitLine[0].equalsIgnoreCase("transaction:")){
                    transData += line;
                }
            }
            br.close();
        }
    }

    public Blockchain(ArrayList<Block> blocks){
        blockchain = blocks;
    }

    public static Boolean validateChain() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    public static synchronized Boolean validateChain(ArrayList<Block> blocks) {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blocks.size(); i++) {
            currentBlock = blocks.get(i);
            previousBlock = blocks.get(i-1);

            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.hash) ){
                System.out.println("Registered: " + currentBlock.hash);
                System.out.println("Actual: " + currentBlock.calculateHash());
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                System.out.println("cur hash " + currentBlock.hash);
                System.out.println("target: " + hashTarget);
                return false;
            }
        }
        return true;
    }

    public static synchronized Boolean validateBlock(Block block) {
        Block previousBlock = blockchain.get(blockchain.indexOf(block)-1);
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        if(!block.hash.substring(0, difficulty).equals(hashTarget)){
            System.out.println("Not mined");
            return false;
        }
        if(!previousBlock.hash.equals(block.previousHash)){
            System.out.println("previous hash does not match: " + previousBlock.hash);
            return false;
        }
            return true;
    }

    public synchronized boolean updateChain(Block block, User[] users) throws IOException {
        if(blockchain.get(blockchain.size()-1).getTimestamp() <= block.getTimestamp()) {
            blockchain.add(block);
            for(User user : users){
                USERS.updateBalance(user.getUsername(), user.coin);
            }
            block.writeBlock();
            return true;
        }
        return false;
    }

}
