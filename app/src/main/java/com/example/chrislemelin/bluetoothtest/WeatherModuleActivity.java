package com.example.chrislemelin.bluetoothtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class WeatherModuleActivity extends AppCompatActivity {

    WeatherModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_module);


        Bundle b = getIntent().getExtras();
        int id = b.getInt(getString(R.string.module_key));
        String name = b.getString(getString(R.string.module_name));

        ((TextView)findViewById(R.id.module_name)).setText(name);


        module = new WeatherModule(SocketHolder.getSocket(id),name,WeatherModuleActivity.this);
        module.start();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        graph.addSeries(series);

    }


}
