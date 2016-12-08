package com.example.chrislemelin.bluetoothtest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import com.example.chrislemelin.bluetoothtest.Fragments.WeatherGraphFrag;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;



import org.joda.time.DateTime;

import java.util.ArrayList;

import static android.R.attr.data;

public class WeatherModuleActivity extends AppCompatActivity implements WeatherGraphFrag.LoadListener
{

    private WeatherModule module;

    private WeatherGraphFrag graphFrag1 = new WeatherGraphFrag();
    private WeatherGraphFrag graphFrag2 = new WeatherGraphFrag();
    private WeatherGraphFrag graphFrag3 = new WeatherGraphFrag();

    private ArrayList<Integer> weatherList1 = new ArrayList<Integer>();
    private ArrayList<Integer> weatherList2 = new ArrayList<Integer>();
    private ArrayList<Integer> weatherList3 = new ArrayList<Integer>();

    private FragmentManager man;

    int selected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_module);


        Bundle b = getIntent().getExtras();
        int id = b.getInt(getString(R.string.module_key));

        man = getSupportFragmentManager();
        String name = b.getString(getString(R.string.module_name));
        ((TextView)findViewById(R.id.module_name)).setText(name);

        if(id == -1)
        {

            module = WeatherModule.testModule;
            selectGraph1(findViewById(R.id.graphTab1));
            module.setCaller(WeatherModuleActivity.this);
            return;
        }

        module = new WeatherModule(SocketHolder.getSocket(id),name,WeatherModuleActivity.this);
        module.start();

        if (findViewById(R.id.weather_graph_frag) != null)
        {
            Log.d("frags", "making frag");
            man.beginTransaction()
                    .add(R.id.weather_graph_frag, graphFrag1).commit();
        }
        man.beginTransaction();
    }

    public void selectGraph1(View v)
    {
        clearTabSelects();
        v.setBackground(getDrawable(R.drawable.tap_shape_selected));

        graphFrag1.updateData(weatherList1);
        if(selected != 1)
        {
            if (findViewById(R.id.weather_graph_frag) != null) {
                man.beginTransaction()
                        .replace(R.id.weather_graph_frag, graphFrag1).commit();
            }
            selected = 1;
        }
    }

    public void selectGraph2(View v)
    {
        clearTabSelects();
        v.setBackground(getDrawable(R.drawable.tap_shape_selected));
        graphFrag2.updateData(weatherList2);
        if(selected != 2)
        {
            if (findViewById(R.id.weather_graph_frag) != null) {
                man.beginTransaction()
                        .replace(R.id.weather_graph_frag, graphFrag2).commit();
            }
            selected = 2;
        }
    }

    public void selectGraph3(View v)
    {
        clearTabSelects();
        v.setBackground(getDrawable(R.drawable.tap_shape_selected));
        graphFrag3.updateData(weatherList3);
        if(selected != 3) {
            if (findViewById(R.id.weather_graph_frag) != null) {
                man.beginTransaction()
                        .replace(R.id.weather_graph_frag, graphFrag3).commit();
            }
            selected = 3;
        }
    }



    public void updateGraphData(final ArrayList<Integer> temps, int id)
    {
        weatherList1 = temps;
        if(id == 1) {
            if (selected == 1) {
                TextView temp = (TextView)findViewById(R.id.current_temp);
                if(temp != null)
                    temp.setText(temps.get(temps.size()-1).toString());
                graphFrag1.updateData(temps);
            }
        }

    }

    public void LoadedGraph(ArrayList<Integer> data)
    {
        updateGraph(data);
    }

    private void updateGraph(final ArrayList<Integer> temps)
    {
        DataPoint[] points = new DataPoint[temps.size()];
        for(int a = 0; a < temps.size(); a++)
        {
            points[a] = new DataPoint(a,temps.get(a));
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(32);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (!isValueX)
                {
                    // show degree for y value
                    return super.formatLabel(value, isValueX) + "\u00b0 F";
                } else {
                    // show time for x values
                    DateTime time = new DateTime();
                    int t = time.getHourOfDay();

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

    private void clearTabSelects()
    {
        findViewById(R.id.graphTab1).setBackground(getDrawable(R.drawable.tab_shape_not_selected));
        findViewById(R.id.graphTab2).setBackground(getDrawable(R.drawable.tab_shape_not_selected));
        findViewById(R.id.graphTab3).setBackground(getDrawable(R.drawable.tab_shape_not_selected));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
