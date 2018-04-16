
/*
ShowGraphActivity -- Routes to a new window and displays a Graphical Representaion of Tracked Pressure Values
                        Automatically orients to Horizontal View for a better quality display
 */
package com.example.swrp.trackmountaineer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import static com.example.swrp.trackmountaineer.DownloadHandler.pressureData;
import static com.example.swrp.trackmountaineer.MainActivity.chart;

public class ShowGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graph);

        chart = findViewById(R.id.dataChart);
        initializeChart();
        chart.invalidate();
        chart.setDescription(null);

        ILineDataSet dataSet = new LineDataSet(pressureData , "Tracked Pressure Values");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    public void initializeChart() {
        YAxis leftAxis = chart.getAxisLeft();
    }
}
