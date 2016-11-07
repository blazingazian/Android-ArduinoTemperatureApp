package com.example.chrislemelin.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.Serializable;

/**
 * Created by chrislemelin on 11/2/16.
 */

public class WeatherModule implements Serializable
{
    private BluetoothSocket sock = null;

    private int id;
    private WeatherModuleActivity caller;
    private DataOutputStream out;
    private DataInputStream in;


    public WeatherModule(BluetoothSocket sock, String name, WeatherModuleActivity caller)
    {
        this.sock = sock;
        this.id = id;
        this.caller = caller;

        try
        {
            OutputStream outStream = sock.getOutputStream();
            InputStream inStream = sock.getInputStream();
            out = new DataOutputStream(outStream);
            in = new DataInputStream(inStream);
        }
        catch (IOException ex)
        {

        }

    }


    public void start()
    {
        //BluetoothSocket sock = SocketHolder.getSocket(id);
        try
        {
            out.writeUTF("switch");
            inThread thread = new inThread(in);
            Log.d("WeatherModule", "start input thread");
            thread.start();
        }
        catch (IOException e)
        {

        }

    }


    private class inThread extends Thread
    {
        final DataInputStream in;

        public inThread(DataInputStream in)
        {
            this.in = in;
        }

        public void run()
        {
            while(true)
            {
                try
                {
                    String a = in.readUTF();
                    Log.d("got message back",a);

                }
                catch (IOException e)
                {

                }
            }


        }





    }

}

