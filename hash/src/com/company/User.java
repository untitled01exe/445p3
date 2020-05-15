package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class User {

    String username;

    public PrivateKey privateKey;

    public PublicKey pk;

    public int coin = 0;

    InetAddress userIP;

    public User(String username, byte[] privateKey, byte[] pk) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.username  = username;
        this.privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        this.pk = kf.generatePublic(new X509EncodedKeySpec(pk));
        System.out.println();
    }

    public void setIP(InetAddress ip){
        userIP = ip;
    }

    public String getUserIP(){
        return userIP.toString();
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

    public String getUsername(){
        return username;
    }

    public String toString(){
        return this.username + " " + this.coin;
    }
}
