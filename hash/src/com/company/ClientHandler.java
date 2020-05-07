package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.StringTokenizer;

class ClientHandler implements Runnable
{
    Scanner stdin = new Scanner(System.in);
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket socket;
    public final PublicKey publicKey;
    private final PrivateKey privateKey;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos, PublicKey puKey, PrivateKey prKey){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.publicKey = puKey;
        this.privateKey = prKey;
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

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.socket.close();
                    break;
                }

                System.out.println(received);


                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.clients.values())
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient))
                    {
                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {

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
}