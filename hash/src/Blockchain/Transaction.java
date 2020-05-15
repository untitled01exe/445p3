package Blockchain;

import com.company.Crypt;
import com.company.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;

import static java.security.MessageDigest.getInstance;

public class Transaction {
    public String transaction;

    public Transaction(User sender, User recipient, int amount) throws NoSuchAlgorithmException, NoSuchPaddingException {
        transaction = sender.getUsername() + " " + amount + " -> " + recipient.getUsername();
    }

}
