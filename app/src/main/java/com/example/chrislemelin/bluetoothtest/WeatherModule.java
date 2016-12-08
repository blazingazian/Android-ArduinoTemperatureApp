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
import java.util.Random;

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
    private final int LASTEIGHTHOURSLENGTH = 8;
    private ArrayList<Integer> lastSevenDays = new ArrayList<Integer>();
    private final int LASTSEVENDAYSLENGTH = 7;


    public static WeatherModule testModule;
    static
    {
        testModule = new WeatherModule(null,"Test Name",null);
        testModule.insertToList1(0,5);
        testModule.insertToList1(1,2);
        testModule.insertToList1(2,8);
        testModule.insertToList1(3,1);
        testModule.insertToList1(4,4);
        testModule.insertToList1(5,3);
        testModule.insertToList1(6,14);
        testModule.insertToList1(7,9);
        Thread tr = new inThreadTest(testModule);
        tr.start();


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
        caller.updateGraphData(lastEightHours,1);
    }


    public void addToList(int value, int id)
    {
        Log.d("added to weather List",value+":"+id);
        ArrayList<Integer> list = new ArrayList<Integer>();
        int length = 0;
        if(id == 1)
        {
            list = lastEightHours;
            length = LASTEIGHTHOURSLENGTH;
        }
        // add other 2 lists

        if(list.size() == length)
        {
            list.remove(0);
            list.add(value);
        }
        else
        {
            list.add(value);
        }
        updateView();
    }

    private void insertToList1(int index, int value)
    {
        lastEightHours.add(index,value);
    }

    private void insertToList2(int index, int value)
    {
        lastEightHours.add(index,value);
    }

    private void updateView()
    {
        caller.updateGraphData(lastEightHours,1);
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


    static private class inThreadTest extends Thread
    {
        Random r = new Random();
        WeatherModule mod;
        public inThreadTest(WeatherModule mod)
        {
            this.mod = mod;
        }

        public void run()
        {
            int a = 40;
            Log.d("start","thread");
            while(true)
            {
                try {
                    this.sleep(1000);
                }
                catch (Exception e)
                {

                }
                mod.addToList(r.nextInt(100),1);


            }
        }
    }
}

