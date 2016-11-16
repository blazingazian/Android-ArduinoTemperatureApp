package com.example.chrislemelin.bluetoothtest;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Permission;
import java.util.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.content.ContextCompat;
import android.Manifest;

public class MainActivity extends AppCompatActivity
{

    private final static int REQUEST_ENABLE_BT = 1;
    private ArrayList<String> mArrayAdapter = new ArrayList<String>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;

    private ArrayList<ConnectThread> threads = new ArrayList<ConnectThread>();

    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    /*
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d("lit",device.getName());
            Log.d("lit",intent.getAction());
            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            devices.add(device);
            Log.d("found_bd", device.getName());
            ConnectThread tr = new ConnectThread(device, MainActivity.this);
            //tr.start();
            //threads.add(tr);

        }
    };
    */


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.d("error","no bluetooth");
            return;
            // Device does not support Bluetooth
        }

        findPairs(mBluetoothAdapter);
        deviceFoundTest();
    }

    public void refreshClick(View v)
    {
        LinearLayout list = (LinearLayout)findViewById(R.id.weather_devices);
        list.removeAllViews();

        list = (LinearLayout)findViewById(R.id.paired_devices);
        list.removeAllViews();

        for(ConnectThread tr : threads)
        {
            tr.cancel();
        }
        threads.clear();

        findPairs(mBluetoothAdapter);

    }

    private void pairedDeviceFound(BluetoothDevice device)
    {
        int id = R.id.paired_devices;
        final BluetoothDevice dev = device;
        final Button newButton = new Button(getApplicationContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

        newButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConnectThread tr = new ConnectThread(dev,MainActivity.this,newButton);
                tr.start();
            }
        });

        newButton.setHeight(75);
        newButton.setLayoutParams(lp);
        newButton.setText(device.getName());

        addButton tr = new addButton(newButton,id);
        runOnUiThread(tr);
    }

    private void deviceFound(BluetoothDevice device, BluetoothSocket sock)
    {
        int modId = SocketHolder.setSocket(sock);
        String name = device.getName();

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        Button newButton = new Button(getApplicationContext());
        newButton.setOnClickListener(new WeatherModuleButtonListener(MainActivity.this,modId,name));
        newButton.setHeight(75);

        newButton.setLayoutParams(lp);
        newButton.setText(device.getName());

        addButton tr = new addButton(newButton, R.id.weather_devices);
        runOnUiThread(tr);
    }

    // adds a test device to test if no bluetooth devices are around
    private void deviceFoundTest()
    {

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        Button newButton = new Button(getApplicationContext());
        newButton.setOnClickListener(new WeatherModuleButtonListener(MainActivity.this,-1,"Test"));
        newButton.setHeight(75);

        newButton.setLayoutParams(lp);
        newButton.setText("Test");

        addButton tr = new addButton(newButton, R.id.weather_devices);
        runOnUiThread(tr);
    }

    private void findPairs(BluetoothAdapter adapter)
    {
        Set<BluetoothDevice>  set = adapter.getBondedDevices();
        for (BluetoothDevice device : set)
        {
            pairedDeviceFound(device);
        }
    }

    private class ValidationThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final MainActivity caller;

        public ValidationThread(BluetoothDevice device,BluetoothSocket sock, MainActivity caller)
        {
            this.mmDevice = device;
            this.mmSocket = sock;
            this.caller = caller;
        }

        public void run()
        {
            try
            {
                OutputStream out =  mmSocket.getOutputStream();
            }
            catch (IOException e)
            {

            }
        }
        public void cancel()
        {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }

    private  class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final MainActivity caller;
        private final Button oldButton;

        UUID id = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device, MainActivity caller,Button oldButton)
        {
            Log.d("startConnect", "ConnectThread: ");
            BluetoothSocket tmp = null;
            mmDevice = device;
            this.caller = caller;
            this.oldButton = oldButton;

            removeButton tr = new removeButton(oldButton,R.id.paired_devices);
            runOnUiThread(tr);

            try
            {
                tmp = device.createRfcommSocketToServiceRecord(id);
            }
            catch (IOException e)
            {
            }
            mmSocket = tmp;
            Log.d("endConnect", "ConnectThread: ");

        }

        public void run()
        {
            mBluetoothAdapter.cancelDiscovery();

            try
            {
                mmSocket.connect();
                Log.d("connect", "connected");
            }
            catch (IOException connectException)
            {
                Log.d("connect", connectException.toString());
                try
                {
                    addButton thread = new addButton(oldButton,R.id.paired_devices);
                    runOnUiThread(thread);

                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            
            Log.d("connected","done dudo");
            caller.deviceFound(mmDevice,mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class addButton extends Thread
    {
        private final Button b;
        private final int id;

        public addButton(Button b , int id)
        {
            this.b = b;
            this.id = id;

        }

        public void run()
        {
            LinearLayout list = (LinearLayout)findViewById(id);
            list.addView(b);
        }
    }

    private class removeButton extends Thread
    {
        private final Button b;
        private final int id;

        public removeButton(Button b , int id)
        {
            this.b = b;
            this.id = id;

        }

        public void run()
        {
            LinearLayout list = (LinearLayout)findViewById(id);
            list.removeView(b);
        }
    }



    /*

    private class drawThread extends Thread
    {
        private final WeatherModule mod;
        private final String name;

        public drawThread(WeatherModule mod,String name)
        {
            this.mod = mod;
            this.name = name;
        }

        public void run()
        {
            LinearLayout list = (LinearLayout)findViewById(R.id.weather_devices);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

            Button newButton = new Button(getApplicationContext());
            newButton.setOnClickListener(new WeatherModuleButtonListener(MainActivity.this,mod));
            newButton.setHeight(75);

            newButton.setLayoutParams(lp);
            newButton.setText(name);

            list.addView(newButton);
        }

    }

    private class drawPairedThread extends Thread
    {
        private final BluetoothDevice device;
        private final Button newButton;

        public drawPairedThread(BluetoothDevice device)
        {
            this.device = device;
            this.newButton = new Button(getApplicationContext());
        }

        public void run()
        {
            LinearLayout list = (LinearLayout)findViewById(R.id.paired_devices);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);


            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ConnectThread tr = new ConnectThread(device,MainActivity.this,newButton);
                    tr.run();
                }
            });

            newButton.setHeight(75);
            newButton.setLayoutParams(lp);
            newButton.setText(device.getName());

            list.addView(newButton);
        }

    }

    */
}
