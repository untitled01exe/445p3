package Blockchain;

import com.company.USERS;
import com.company.User;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import static java.security.MessageDigest.*;

public class Block implements Serializable{
    public String hash;
    public String previousHash;
    static String transactionData = "";
    public static String totals = "";
    int transactionCounter = 0;
    static final int MAX_TRANS_COUNT = 4;
    private long timeStamp;
    private int nonce;
    private static String space = "\n\n";


    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }

    public Block(String transactionData, String previousHash, Long timeStamp, String totals) {
        this.transactionData = transactionData;
        this.totals = totals;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateHash();
    }

    public Block(String data,String previousHash ) {
        this.transactionData = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String dataToHash = transactionData + previousHash + timeStamp + nonce;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String mineBlock(int difficulty) {
        hash = calculateHash();
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined: " + hash);
        return hash;
    }

    public boolean addTransaction(String transaction, User[] users){
        String transMarker = "Transaction: ";
        if(transactionCounter < MAX_TRANS_COUNT){
            transactionData += space + transMarker + transaction;
            transactionCounter++;
            return true;
        } else{
            transactionData += space + getTotals(users);
        }
        return false;
    }

    public static String getTotals(User[] users){
        totals = "";
        for(User user : users){
            String name = user.getUsername();
            int balance = user.coin;
            String userTotal = name + ":" + balance + " ";
            totals += userTotal;
        }
        return totals;
    }

    public static Block initBlock(){
        String initTrans = "InitTrans: ";
        String t1 = initTrans + "u1:" + " 50";
        String t2 = initTrans + "u2:" + " 50";
        String t3 = initTrans + "u3:" + " 50";
        String t4 = initTrans + "u4:" + " 50";

        //add the totals line to the block string
        String transactionData = space + t1 + space + t2 + space + t3 + space + t4 + space + getTotals(USERS.users);

        return new Block(transactionData, "0");
    }

    public void writeBlock() throws IOException {
        BufferedWriter fw = new BufferedWriter(new FileWriter("chain.txt", true));
        fw.newLine();
        fw.write(this.toString());
        fw.flush();
        fw.close();
    }

    public static boolean isComplete(){
        return (transactionData.length() > 0);
    }

    public Long getTimestamp(){
        return timeStamp;
    }

    public String toString(){
        return "Transactions: " + transactionData + space + "Previous Hash: " + previousHash + space + "Time Stamp: " +  timeStamp;
    }

}
