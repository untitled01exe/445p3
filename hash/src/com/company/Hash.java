package com.company;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Hash {

    public int hexAmt = 5;
    //larger hex makes keys more rare!
    //nothing above 7 for this project

    public byte[] biteHash(String s) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        return m.digest(s.getBytes(StandardCharsets.UTF_8));
    }

    public String hashHex(byte[] b){
        BigInteger intHash = new BigInteger(1, b);
        StringBuilder sb = new StringBuilder(intHash.toString(16));
        return sb.toString();
    }

    public String hash(String s) throws NoSuchAlgorithmException {
        return hashHex(biteHash(s));
    }

    public boolean checkHash(String s){
        StringBuilder check = new StringBuilder();
        for(int i = 0; i < hexAmt; i++){
            check.append("0");
        }
        if(s.substring(s.length()-hexAmt).equals(check.toString()) && s.charAt(s.length()-(hexAmt+1)) != '0'){
            return true;
        }
        return false;
    }

    public String genString(){
        Random r = new Random();
        String ret = r.ints(33, 127)
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return ret;
    }

    public void mineTrial() throws NoSuchAlgorithmException {
        String key;
        int i = 0;
        do{
            i++;
            key = hash(genString());
        } while(!checkHash(key));
        System.out.println(i);
    }

    public String mineTrialwString(String str) throws NoSuchAlgorithmException {
        String temp;
        do{
            temp = str + genString();
            //key = hash(genString());
        } while(!checkHash(hash(temp)));
        return temp;
    }
}
