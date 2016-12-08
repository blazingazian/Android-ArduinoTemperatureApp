package com.example.chrislemelin.bluetoothtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;

/**
 * Created by chrislemelin on 11/4/16.
 */

public class WeatherModuleButtonListener implements View.OnClickListener
{
    int id;
    MainActivity main;
    String name;

    public WeatherModuleButtonListener(MainActivity main,int id,String name)
    {
        this.id = id;
        this.main = main;
        this.name = name;
    }

    public void onClick(View v)
    {
        Intent i = new Intent(main,WeatherModuleActivity.class);
        Bundle b = new Bundle();
        b.putInt(main.getString(R.string.module_key),id);
        b.putString(main.getString(R.string.module_name),name);
        i.putExtras(b);

        main.startActivity(i);
    }

}
