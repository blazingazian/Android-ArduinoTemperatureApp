package com.example.chrislemelin.bluetoothtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class WeatherModuleActivity extends AppCompatActivity
{

    WeatherModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_module);


        Bundle b = getIntent().getExtras();
        int id = b.getInt(getString(R.string.module_key));
        if(id == -1)
        {
            module = WeatherModule.testModule;
            module.setCaller(WeatherModuleActivity.this);
            return;

        }
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

    public void updateGraph1(final ArrayList<Integer> temps)
    {
        DataPoint[] points = new DataPoint[temps.size()];
        for(int a = 0; a < temps.size(); a++)
        {
            points[a] = new DataPoint(a,temps.get(a));
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX) + "\u00b0 F";
                } else {
                    // show currency for y values
                    DateTime time = new DateTime();
                    int t = time.getHourOfDay();
                    String returnString;

                    t -= (temps.size()-1) - (int)value;

                    if(t < 12)
                    {
                        return t+" AM";
                    }
                    else
                    {
                        return (t-12)+" PM";
                    }
                }
            }
        });


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
    }


}
