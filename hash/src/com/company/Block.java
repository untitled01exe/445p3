package com.company;

import java.security.NoSuchAlgorithmException;

public class Block {

    String space = "\n\n";
    Hash h = new Hash();
    String prevKey;

    public Block (String t1, String t2, String t3, String t4, String t5, String previousBlockKey, String blockNum) throws NoSuchAlgorithmException {
        int nBL = blockNum.charAt(7);
        String block = "BLOCK: " + nBL + space + t1 + space + t2 + space + t3 + space + t4 + space + t5 + space + previousBlockKey;
        this.prevKey = previousBlockKey;
        h.mineTrialwString(block);
    }
}
