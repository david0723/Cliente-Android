package com.example.davidschicaiza.myapplication;

/**
 * Created by DavidSchicaiza on 3/8/16.
 */
import android.util.Log;

import java.io.*;
import java.net.*;
class UDPClient
{
    public UDPClient()
    {

    }
    public void exe() throws Exception
    {
        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

        //DatagramSocket clientSocket = new DatagramSocket(null);
        //InetSocketAddress add = new InetSocketAddress("157.253.217.18", 2345);
        //clientSocket.bind(add);

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress direccion = InetAddress.getByName("157.253.217.94");

        String mensaje = "Hola";
        int longitud = mensaje.length();
        byte[] bytes = mensaje.getBytes();

        DatagramPacket paquete = new DatagramPacket(bytes, longitud, direccion, 2345);
        clientSocket.send(paquete);


        //DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //sentence = "mensaje";
        //outToServer.writeBytes(sentence + '\n');
        //modifiedSentence = inFromServer.readLine();
        //Log.i("Cliente TCP","From server: "+modifiedSentence);
        //System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}