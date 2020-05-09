package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TransactionBuilder {

    String space = "\n\n";

    File f;

<<<<<<< HEAD
        byte[] encripted = c.encrypt(sk, tran.getBytes());
        System.out.println(new String(encripted));

        byte[] decrypted = c.decrypt(pk, encripted);
        String result = new String(decrypted);
        System.out.println(result);
        System.out.println("is is " + result.equals(tran) + " the decrypted string is equal to the original");
=======
    USERS users = new USERS();

    Hash h = new Hash();
>>>>>>> 0aaa0e90fae095664de1198e8fd77be15131c5dd

    public TransactionBuilder() throws InvalidKeySpecException, NoSuchAlgorithmException {
        f = new File("chain.txt");
    }

    public String initBlock() throws NoSuchAlgorithmException {
        String t1 = "0 S " + users.users[0].username + " 20";
        String t2 = "0 S " + users.users[1].username + " 20";
        String t3 = "0 S " + users.users[2].username + " 20";
        String t4 = "0 S " + users.users[3].username + " 20";
        //init users
        for (User user : users.users) {
            user.addFunds(20);
            }
        //add "coin" to each user instance
        String totals = users.totals();
        //add the totals line to the block string
        String block = "BLOCK: 0" + space + t1 + space + t2 + space + t3 + space + t4 + space + "*t5*" + space + totals + space + "old key" + space;
        System.out.println();

<<<<<<< HEAD
        h.mineTrialwString(new String(encripted));
        System.out.println("Key was found for this data, block award given to publishing user");
        System.out.println("Everything worked!");
=======
        String ret = h.mineTrialwString(block);
        //finds an appropriate key to complete the block
        return ret + space;
>>>>>>> 0aaa0e90fae095664de1198e8fd77be15131c5dd
    }

    public String randomBlock() throws FileNotFoundException, NoSuchAlgorithmException {
        File f = new File("chain.txt");
        Scanner s = new Scanner(f);
        s.useDelimiter(Pattern.compile("\nBLOCK: ([0-9])+\n"));
        int index = -1;
        while(s.hasNext()){
            s.next();
            index++;
        }
        //^^ finds current block number
        int transIndex = index*4+1;
        //finds current transaction number
        int lineIndex = index * 19 + 12;
        //finds the important stuff on the current block
        s = new Scanner(f);

        for(int i = 0; i < lineIndex; i++){
            s.nextLine();
        }
        //gets to current block

        parseTotalsLine(s.nextLine());
        //reconfigures coin data for all users using last totals line

        for(int i = 0; i < 3; i++){
            s.nextLine();
        }

        String oldHashKey = s.nextLine();
        //gets the last block's key

        String t1 = buildTransactionLine(transIndex);
        transIndex++;
        String t2 = buildTransactionLine(transIndex);
        transIndex++;
        String t3 = buildTransactionLine(transIndex);
        transIndex++;
        String t4 = buildTransactionLine(transIndex);
        transIndex++;
        //creates random transactions
        Random r = new Random();
        String t5 = buildRewardLine(users.users[r.nextInt(users.users.length)], transIndex);
        //reward coin transaction

        String totals = users.totals();

        String block = "BLOCK: " + (index+1) + space + t1 + space + t2 + space + t3 + space + t4 + space + t5 + space + totals + space + oldHashKey + space;
        //constructs block string

        String ret = h.mineTrialwString(block);
        //System.out.println(index);
        return ret + space;
    }

    public void parseTotalsLine(String line){
        Scanner s = new Scanner(line);
        s.useDelimiter(" ");
        //uses space delimiter to get each user/coin pair
        while(s.hasNext()) {
            User u = users.getUser(s.next());
            int coin = Integer.parseInt(s.next());
            u.coin = coin;
        }
    }

    public String buildTransactionLine(int trans) {
        User u1 = null;
        User u2 = null;
        Random r = new Random();
        boolean flag = true;
        //finds 2 users to send/receive coins
        while (flag){
             u1 = users.users[r.nextInt(users.users.length)];
             u2 = users.users[r.nextInt(users.users.length)];  //<<A bug
            if(u1.coin > 0){
                flag = false;
            }
        }

        int transferCoin = r.nextInt(r.nextInt(u1.coin)); //<< Bug??
        u1.removeFunds(transferCoin);
        u2.addFunds(transferCoin);
        //replaces the necessary funds for each user

        String ret = trans + " " + u1.username + " " + transferCoin + " " + u2.username;
        return ret;
    }

    public String buildRewardLine(User u, int tranNum){
        u.addFunds(20);
        return tranNum + " S 20 " + u.username;
        //another call to increase funds for init block
    }
}
