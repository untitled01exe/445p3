package com.company;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class Crypt {
    public Cipher c;
    public KeyPair keyPair;

    public Crypt() throws NoSuchPaddingException, NoSuchAlgorithmException {
        c = Cipher.getInstance("RSA");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.genKeyPair();
    }

    public PublicKey genPublic() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey initPublic = this.keyPair.getPublic();
        KeyFactory k = KeyFactory.getInstance("RSA");
        byte[] b = initPublic.getEncoded();
        X509EncodedKeySpec s = new X509EncodedKeySpec(b);
        PublicKey pk = k.generatePublic(s);
        return pk;
    }

    public PrivateKey genSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey initPrivate = this.keyPair.getPrivate();
        KeyFactory k = KeyFactory.getInstance("RSA");
        byte[] b = initPrivate.getEncoded();
        PKCS8EncodedKeySpec s = new PKCS8EncodedKeySpec(b);
        PrivateKey pk = k.generatePrivate(s);
        return pk;
    }

    public byte[] encrypt(PrivateKey key, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = c.doFinal(data);
        return b;
    }

    public byte[] decrypt (PublicKey key, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] b = c.doFinal(data);
        return b;
    }

    public String decryptTransaction (User recipient, String transaction) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        //Convert bytes to AES SecertKey
        byte[] data = transaction.getBytes();
        byte[] encryptedKey = Arrays.copyOfRange(data, 0, 16);
        byte[] b = new byte[256];
        ByteBuffer byteBuffer = ByteBuffer.wrap(b);
        byteBuffer.put(encryptedKey);
        byte[] paddedEncryptedKey = byteBuffer.array();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, recipient.pk);
        byte[] decryptedKey = cipher.doFinal(paddedEncryptedKey);
        SecretKey originalKey = new SecretKeySpec(decryptedKey, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
        byte[] bytePlainText = aesCipher.doFinal(transaction.getBytes());
        String decrypted = new String(bytePlainText);

        return decrypted;
    }

    public String encryptTransaction(User recipient, String transaction) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        //Generate Symmetric AES key
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey secretKey = generator.generateKey();

        //Encrypt transaction using AES
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] byteCipherText = aesCipher.doFinal(transaction.getBytes());

        //Encrypt this using the RSA Public Key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PublicKey key = recipient.pk;
        cipher.init(Cipher.PUBLIC_KEY, key);
        byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());

        String encrypted = new String(encryptedKey) + new String(byteCipherText);
        return encrypted;
    }

    public String encryptStr(PrivateKey key, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = c.doFinal(data);
        String es = b.toString();

        return es;
    }

    //encryptedString = encryptedString.replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "")

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
        Crypt c = new Crypt();
        byte[] b;

        PrivateKey sk = c.genSecret();
        PublicKey pk = c.genPublic();
        System.out.println();
        System.out.println(Arrays.toString(sk.getEncoded()));
        System.out.println(Arrays.toString(pk.getEncoded()));

        b = c.encrypt(sk, "WOAH LOLOL I GOT A LONG STRING HERE WOOOOO WTF OMFG LOLOLOLOLOLOL".getBytes());
        System.out.println(b.length);
        b = c.decrypt(pk, b);
        System.out.println(new String(b));
    }
}

//https://mkyong.com/java/java-asymmetric-cryptography-example/
/*
//-------------
        File f = new File("dec.txt");
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(b);
        fo.flush();
        fo.close();
        //-------------
 */