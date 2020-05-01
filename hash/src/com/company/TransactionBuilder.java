package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class TransactionBuilder {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
        //add something for users with stored keys here!
        String tran = "user: 1 gave user: 2 30 DC";
        System.out.println(tran);
        Crypt c  = new Crypt();
        PublicKey pk = c.genPublic();
        PrivateKey sk = c.genSecret();


        byte[] encripted = c.encrypt(sk, tran.getBytes());
        System.out.println(new String(encripted));

        byte[] decrypted = c.decrypt(pk, encripted);
        String result = new String(decrypted);
        System.out.println(result);
        System.out.println("is is " + result.equals(tran) + " the decrypted string is equal to the original");

        Hash h = new Hash();

        h.mineTrialwString(new String(encripted));
        System.out.println("Key was found for this data, block award given to publishing user");
        System.out.println("Everything worked!");
    }

}
