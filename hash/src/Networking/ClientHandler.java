package Networking;

import Blockchain.Transaction;
import com.company.Crypt;
import com.company.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

class ClientHandler implements Runnable
{
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket socket;
    boolean isloggedin;
    User user;
    Crypt crypt;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos, User user) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.user = user;
        this.crypt = new Crypt();
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive input from Server
                received = dis.readUTF();

                if(received.equals("exit")){
                    this.isloggedin=false;
                    this.socket.close();
                    break;
                }

                System.out.println(received);


                // break the string into message and recipient part
                String msgToSend = received;

                // send message to all recipients
                broadcast(msgToSend);

            } catch (IOException | IllegalBlockSizeException e) {

                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void broadcast(String message) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
        for (ClientHandler recipient : Server.clients.values())
        {
            //TODO: fix encryption
            if(recipient.name != this.name) {
                String encrypted = crypt.encryptTransaction(recipient.user, message);
               // recipient.dos.writeUTF(this.name + " : " + encrypted);
                recipient.dos.writeUTF(this.name + " : " + message);
            }
        }
    }

}