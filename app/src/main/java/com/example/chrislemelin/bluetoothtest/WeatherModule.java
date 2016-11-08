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
import java.util.ArrayList;

/**
 * Created by chrislemelin on 11/2/16.
 */

public class WeatherModule
{
    private String name;


    private BluetoothSocket sock = null;


    private WeatherModuleActivity caller;
    private DataOutputStream out;
    private DataInputStream in;

    private ArrayList<Integer> lastEightHours = new ArrayList<Integer>();
    private ArrayList<Integer> lastSevenDays = new ArrayList<Integer>();

    public static WeatherModule testModule;
    static
    {
        testModule = new WeatherModule(null,"Test Name",null);
        testModule.addToList1(0,5);
        testModule.addToList1(1,2);
        testModule.addToList1(2,8);
        testModule.addToList1(3,1);
        testModule.addToList1(4,4);
        testModule.addToList1(5,3);
        testModule.addToList1(6,14);
        testModule.addToList1(7,9);

    }



    public WeatherModule(BluetoothSocket sock, String name, WeatherModuleActivity caller)
    {
        if(sock == null)
        {
            return;
        }
        this.sock = sock;
        this.caller = caller;
        this.name = name;

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
        if(sock == null)
        {
            return;
        }

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

    public void setCaller(WeatherModuleActivity caller)
    {
        this.caller = caller;
        caller.updateGraph1(lastEightHours);
    }

    private void addToList1(int index, int value)
    {
        lastEightHours.add(index,value);
    }

    private void addToList2(int index, int value)
    {
        lastEightHours.add(index,value);
    }

    private void updateView()
    {
        //caller.doStuff
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

    private class outThread extends Thread
    {
        final DataOutputStream out;

        public outThread(DataOutputStream out)
        {
            this.out = out;
        }

        public void run()
        {
            while(true)
            {

            }
        }

    }

}

