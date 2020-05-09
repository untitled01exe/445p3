package com.company;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class User {

    String username;

    public PrivateKey sk;

    public PublicKey pk;

    public int coin = 0;

    public User(String username, byte[] sk,  byte[] pk) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.username  = username;
        this.sk = kf.generatePrivate(new PKCS8EncodedKeySpec(sk));
        this.pk = kf.generatePublic(new X509EncodedKeySpec(pk));
        System.out.println();
    }

    public void addFunds(int i){
        coin = coin + i;
    }

    public boolean removeFunds(int i){
        if(coin - i >= 0){
            coin = coin - i;
            return true;
        }
        return false;
    }

    public String toString(){
        return this.username + " " + this.coin;
    }
}
