package com.example.davidschicaiza.myapplication;

/**
 * Created by DavidSchicaiza on 3/8/16.
 */
import android.util.Log;

import java.io.*;
import java.net.*;
class TCPClient {

    private DataOutputStream out;
    private Socket clientSocket;

    public TCPClient() throws Exception {
        clientSocket = new Socket("192.168.0.4", 1234);
        out = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void send( String mensaje ) throws Exception {
        out.writeBytes(mensaje + '\n');
    }

    public void close() throws Exception {
        clientSocket.close();
    }

//    public void exe() throws Exception
//    {
//        String sentence;
//        String modifiedSentence;
//        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
//        Socket clientSocket = new Socket("192.168.0.4", 1234);
//        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        sentence = "mensaje";
//        outToServer.writeBytes(sentence + '\n');
//        clientSocket.close();
//    }
}