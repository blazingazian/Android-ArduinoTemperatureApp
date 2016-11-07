package com.example.chrislemelin.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by chrislemelin on 11/4/16.
 */



public class SocketHolder
{
    private static ArrayList<BluetoothSocket> sockets = new ArrayList<BluetoothSocket>();


    public static BluetoothSocket getSocket(int d)
    {
        return sockets.get(d);
    }

    public static int setSocket(BluetoothSocket s)
    {
        int returnInt = sockets.size();
        sockets.add(s);
        return returnInt;
    }

}
