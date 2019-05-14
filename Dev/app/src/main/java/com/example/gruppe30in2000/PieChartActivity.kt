package com.example.gruppe30in2000

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.city_element.view.*
import kotlinx.android.synthetic.main.city_element.view.description_text
import kotlinx.android.synthetic.main.maptaginfoview.view.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.Legend




class PieChartActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.piechart_layout)

        val intent = intent

        val chart = findViewById<LineChart>(R.id.piechart) as PieChart
        //val title = findViewById<TextView>(R.id.title)

        val index = intent.getIntExtra("index", 0)
        val chartNr = intent.getIntExtra("chartNr", 0)
        val timeIndex = intent.getIntExtra("timeIndex", 0)

        val station = MainActivity.staticAirQualityStationsList[index]
        val timestring = station.data.time[timeIndex].from.split("T")
        val date = timestring[0]
        val hour = timestring[1].take(5)
        val variables = station.data.time[timeIndex].variables

        val desc = Description()
        desc.text = station.meta.location.name + ", " + station.meta.superlocation.name + "  " + date + " - Kl:" + hour
        chart.description = desc
        chart.description.textSize = 16f

        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 95f
        chart.isDrawHoleEnabled = false

        val values = ArrayList<PieEntry>()

        if(chartNr == 0){
            if(variables.pm10_local_fraction_heating.value > 0)
                values.add(PieEntry(variables.pm10_local_fraction_heating.value.toFloat(), "vedfyring"))
            if(variables.pm10_local_fraction_shipping.value > 0)
                values.add(PieEntry(variables.pm10_local_fraction_shipping.value.toFloat(), "skip"))
            if(variables.pm10_local_fraction_traffic_exhaust.value > 0)
                values.add(PieEntry(variables.pm10_local_fraction_traffic_exhaust.value.toFloat(), "eksos"))
            if(variables.pm10_local_fraction_traffic_nonexhaust.value > 0)
                values.add(PieEntry(variables.pm10_local_fraction_traffic_nonexhaust.value.toFloat(), "veistøv"))
            if(variables.pm10_nonlocal_fraction.value > 0)
                values.add(PieEntry(variables.pm10_nonlocal_fraction.value.toFloat(), "langtransportert"))
            if(variables.pm10_local_fraction_industry.value > 0)
                values.add(PieEntry(variables.pm10_local_fraction_industry.value.toFloat(), "industri"))

            val andreKilder = 100f -
                    (variables.pm10_local_fraction_heating.value.toFloat() +
                    variables.pm10_local_fraction_shipping.value.toFloat() +
                    variables.pm10_local_fraction_traffic_exhaust.value.toFloat() +
                    variables.pm10_local_fraction_traffic_nonexhaust.value.toFloat() +
                    variables.pm10_nonlocal_fraction.value.toFloat() +
                    variables.pm10_local_fraction_industry.value.toFloat())

            if(andreKilder > 0){
                values.add(PieEntry(andreKilder, "andre kilder"))
            }

        } else if(chartNr == 1){
            if(variables.pm25_local_fraction_heating.value > 0)
                values.add(PieEntry(variables.pm25_local_fraction_heating.value.toFloat(), "vedfyring"))
            if(variables.pm25_local_fraction_shipping.value > 0)
                values.add(PieEntry(variables.pm25_local_fraction_shipping.value.toFloat(), "skip"))
            if(variables.pm25_local_fraction_traffic_exhaust.value > 0)
                values.add(PieEntry(variables.pm25_local_fraction_traffic_exhaust.value.toFloat(), "eksos"))
            if(variables.pm25_local_fraction_traffic_nonexhaust.value > 0)
                values.add(PieEntry(variables.pm25_local_fraction_traffic_nonexhaust.value.toFloat(), "veistøv"))
            if(variables.pm25_nonlocal_fraction.value > 0)
                values.add(PieEntry(variables.pm25_nonlocal_fraction.value.toFloat(), "langtransportert"))
            if(variables.pm25_local_fraction_industry.value > 0)
                values.add(PieEntry(variables.pm25_local_fraction_industry.value.toFloat(), "industri"))

            val andreKilder = 100f -
                    (variables.pm25_local_fraction_heating.value.toFloat() +
                    variables.pm25_local_fraction_shipping.value.toFloat() +
                    variables.pm25_local_fraction_traffic_exhaust.value.toFloat() +
                    variables.pm25_local_fraction_traffic_nonexhaust.value.toFloat() +
                    variables.pm25_nonlocal_fraction.value.toFloat() +
                    variables.pm25_local_fraction_industry.value.toFloat())

            if(andreKilder > 0){
                values.add(PieEntry(andreKilder, "andre kilder"))
            }

        } else if(chartNr == 2){
            if(variables.no2_local_fraction_heating.value > 0)
                values.add(PieEntry(variables.no2_local_fraction_heating.value.toFloat(), "vedfyring"))
            if(variables.no2_local_fraction_shipping.value > 0)
                values.add(PieEntry(variables.no2_local_fraction_shipping.value.toFloat(), "skip"))
            if(variables.no2_local_fraction_traffic_exhaust.value > 0)
                values.add(PieEntry(variables.no2_local_fraction_traffic_exhaust.value.toFloat(), "eksos"))
            if(variables.no2_nonlocal_fraction.value > 0)
                values.add(PieEntry(variables.no2_nonlocal_fraction.value.toFloat(), "langtransportert"))
            if(variables.no2_local_fraction_industry.value > 0)
                values.add(PieEntry(variables.no2_local_fraction_industry.value.toFloat(), "industri"))

            val andreKilder = 100f -
                    (variables.no2_local_fraction_heating.value.toFloat() +
                    variables.no2_local_fraction_shipping.value.toFloat() +
                    variables.no2_local_fraction_traffic_exhaust.value.toFloat() +
                    variables.no2_nonlocal_fraction.value.toFloat() +
                    variables.no2_local_fraction_industry.value.toFloat())

            if(andreKilder > 0){
                values.add(PieEntry(andreKilder, "andre kilder"))
            }
        }

        chart.animateY(1000, Easing.EaseInOutCubic)

        val dataset = PieDataSet(values, "")
        dataset.sliceSpace = 1f
        dataset.selectionShift = 5f
        var colorList = ArrayList<Int>()
        colorList.add(Color.RED)
        colorList.add(Color.BLUE)
        colorList.add(Color.GREEN)
        colorList.add(Color.YELLOW)
        colorList.add(Color.CYAN)
        colorList.add(Color.GRAY)

        dataset.colors = colorList
        dataset.valueTextSize = 16f

        val data = PieData(dataset)

        chart.setDrawSliceText(false)

        chart.data = data

    }
}