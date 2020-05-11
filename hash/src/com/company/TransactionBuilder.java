package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TransactionBuilder {

    String space = "\n\n";

    File f;

    USERS users = new USERS();

    Hash h = new Hash();

    Crypt c  = new Crypt();

    IntCon ic = new IntCon();

    public TransactionBuilder() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
        f = new File("chain.txt");
    }

    public String initBlock() throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        String t1 = "0 S " + users.users[0].username + " 20";
        t1 = ic.arrToBInt(c.encrypt(users.system.sk, t1.getBytes())).toString();
        t1 = finishTransLine(t1, 0, users.system);
        String t2 = "0 S " + users.users[1].username + " 20";
        t2 = ic.arrToBInt(c.encrypt(users.system.sk, t2.getBytes())).toString();
        t2 = finishTransLine(t2, 0, users.system);
        String t3 = "0 S " + users.users[2].username + " 20";
        t3 = ic.arrToBInt(c.encrypt(users.system.sk, t3.getBytes())).toString();
        t3 = finishTransLine(t3, 0, users.system);
        String t4 = "0 S " + users.users[3].username + " 20";
        t4 = ic.arrToBInt(c.encrypt(users.system.sk, t4.getBytes())).toString();
        t4 = finishTransLine(t4, 0, users.system);

        for (User user : users.users) {
            user.addFunds(20);
            }

        //add "coin" to each user instance
        String totals = users.totals();
        //add the totals line to the block string
        String block = "BLOCK: 0" + space + t1 + t2 + t3 + t4 + "*" + space + totals + space + "*" + space;

        String ret = h.mineTrialwString(block);
        //finds an appropriate key to complete the block
        return ret + space;
    }

    public String randomBlock() throws IOException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        File f = new File("chain.txt");
        Scanner s = new Scanner(f);
        s.useDelimiter(Pattern.compile("\nBLOCK: ([0-9])+\n"));
        int index = -1;
        while(s.hasNext()){
            s.next();
            index++;
        }
        //^^ finds current block number

        int transIndex = index*5+1;
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
        transIndex++;

        String totals = users.totals();

        String block = "BLOCK: " + (index+1) + space + t1 + t2 + t3 + t4 + t5 + totals + space + oldHashKey + space;
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

    public String buildTransactionLine(int trans) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
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
        ret = ic.arrToBInt(c.encrypt(u1.sk, ret.getBytes())).toString();
        ret = finishTransLine(ret, trans, u1);
        return ret;
    }

    public String finishTransLine(String s, int i, User u){
        s = s + "\n";
        s = s  + i + " " + u.username + "\n";
        return s;
    }

    public String buildRewardLine(User u, int tranNum) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        u.addFunds(20);
        String ret = tranNum + " S 20 " + u.username;
        ret = ic.arrToBInt(c.encrypt(users.system.sk, ret.getBytes())).toString();
        ret = finishTransLine(ret, tranNum, users.system);
        return ret;
        //another call to increase funds for init block
    }
}
