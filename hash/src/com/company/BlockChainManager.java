package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChainManager {

    ReentrantLock rl;
    FileWriter fw;
    File file;
    TransactionBuilder tb = new TransactionBuilder();

    BlockChainManager () throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException {
        file = new File("chain.txt");
        Hash h = new Hash();

        if (file.createNewFile())
        {
            //creates new file, writes initial block
            fw = new FileWriter(file);
            fw.write(tb.initBlock());
            fw.flush();
            fw.close();
        } else {
            //finds existing file, makes a random block

            BufferedWriter fw = new BufferedWriter(new FileWriter("chain.txt", true));
/*
            fw.newLine();
            fw.write(tb.randomBlock());
            fw.flush();
            fw.close();
*/
            findTransaction(17, file);
        }
    }

    public void update() throws IOException {
        //rl.lock();

        //rl.unlock();
    }

    public boolean findTransaction(int i, File f) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Scanner s = new Scanner(f);
        int div = Math.floorDiv(i,5);

        if(i % 5 != 0) {
            div++;
        }

        div = div * 19;

        int jump;

        if(i % 5 != 0) {
            jump = (i % 5) * 2;
        }else{
            jump = 10;
        }

        div = div + jump;
        System.out.println(div);


        int index = 0;
        while (index < div){
            if(s.hasNextLine()){
                s.nextLine();
            } else {
                return false;
            }
            index++;
        }

        String line1 = s.nextLine();
        String line2 = s.nextLine();

        Scanner sl = new Scanner(line2);
        sl.useDelimiter(" ");
        sl.next();
        String usr = sl.next();
        System.out.println(usr);
        USERS users = new USERS();

        User u = users.getUserPlusSystem(usr);
        Crypt c = new Crypt();

        IntCon ic = new IntCon();

        BigInteger big = new BigInteger(line1);
        System.out.println(big.toString());

        byte[] b = ic.intToArr(new BigInteger(line1));

        byte[] retArr = c.decrypt(u.pk, b);

        String ret = new String(retArr);
        System.out.println(ret);

        return true;

    }



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        BlockChainManager bcm = new BlockChainManager();
    }
}