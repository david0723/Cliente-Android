package com.example.davidschicaiza.myapplication;

/**
 * Created by DavidSchicaiza on 3/8/16.
 */
import android.util.Log;

import java.io.*;
import java.net.*;
class TCPClient
{
    public TCPClient()
    {

    }
    public void exe() throws Exception
    {
        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = new Socket("157.253.217.94", 1234);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        sentence = "mensaje";
        outToServer.writeBytes(sentence + '\n');
        //modifiedSentence = inFromServer.readLine();
        //Log.i("Cliente TCP","From server: "+modifiedSentence);
        //System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}