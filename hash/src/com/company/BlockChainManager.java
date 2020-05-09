package com.company;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class BlockChainManager {

    ReentrantLock rl;
    FileWriter fw;
    File file;
    TransactionBuilder tb = new TransactionBuilder();

    BlockChainManager () throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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
            fw.newLine();
            fw.write(tb.randomBlock());
            fw.flush();
            fw.close();
        }
    }

    public void update(Block b) throws IOException {
        //rl.lock();

        //rl.unlock();
    }



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        BlockChainManager bcm = new BlockChainManager();
    }
}