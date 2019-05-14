package com.example.gruppe30in2000

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.city_element.view.*
import kotlinx.android.synthetic.main.city_element.view.description_text
import kotlinx.android.synthetic.main.maptaginfoview.view.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter



class GraphActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphlayout)

        val intent = intent

        val chart = findViewById<LineChart>(R.id.line_chart)

        val xAxis = chart.xAxis

        xAxis.labelRotationAngle = -45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.textSize = 13f

        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.isScrollContainer = true
        chart.isDoubleTapToZoomEnabled = true
        chart.setPinchZoom(true)


        val index = intent.getIntExtra("index", 0)

        val station = MainActivity.staticAirQualityStationsList[index]

        //chart = station.meta.location.name + ", " + station.meta.superlocation.name

        val desc = Description()
        desc.text = station.meta.location.name + ", " + station.meta.superlocation.name
        chart.description = desc
        chart.description.textSize = 16f


        val xLabels = ArrayList<String>()
        //val aqiValues = ArrayList<Entry>()
        val svevestovValues = ArrayList<Entry>()
        val nitrogenValues = ArrayList<Entry>()
        val ozonValues = ArrayList<Entry>()

        var c = 0
        station.data.time.forEach {
            val datetime = it.from.replace("T", " ").dropLast(4).drop(5).split(" ")
            val dateSplit = datetime[0].split("-")
            val date = dateSplit[1] + "-" + dateSplit[0]
            val finalDateTime = datetime[1] + " " + date
            xLabels.add(finalDateTime)
            //aqiValues.add(Entry(c.toFloat() ,it.variables.AQI.value.toFloat()))
            svevestovValues.add(Entry(c.toFloat() ,it.variables.pm10_concentration.value.toFloat()))
            Log.e("test", it.variables.no2_concentration.value.toFloat().toString())
            nitrogenValues.add(Entry(c.toFloat() ,it.variables.no2_concentration.value.toFloat()))
            ozonValues.add(Entry(c.toFloat() ,it.variables.o3_concentration.value.toFloat()))
            c++
        }

        xAxis.valueFormatter = object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                return xLabels[value.toInt()]
            }
        }

        val right = chart.axisRight

        right.isEnabled = false

        val left = chart.axisLeft

        left.valueFormatter = object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                return value.toString() + "ug/m3"
            }
        }


        //val setAQI = LineDataSet(aqiValues, "AQI")
        val setSvevestov = LineDataSet(svevestovValues, "Svevestøv")
        val setNitrogen = LineDataSet(nitrogenValues, "Nitrogen")
        val setOzon = LineDataSet(ozonValues, "Ozon")

        //setAQI.setDrawValues(false)
        setSvevestov.setDrawValues(false)
        setNitrogen.setDrawValues(false)
        setOzon.setDrawValues(false)

        //setAQI.color = Color.MAGENTA
        setSvevestov.color = Color.BLUE
        setNitrogen.color = Color.GREEN
        setOzon.color = Color.RED

        //setAQI.setCircleColor(Color.MAGENTA)
        setSvevestov.setCircleColor(Color.BLUE)
        setNitrogen.setCircleColor(Color.GREEN)
        setOzon.setCircleColor(Color.RED)

        val dataSets = ArrayList<ILineDataSet>()
        //dataSets.add(setAQI)
        dataSets.add(setSvevestov)
        dataSets.add(setNitrogen)
        dataSets.add(setOzon)

        val lineData = LineData(dataSets)

        chart.data = lineData
        chart.setVisibleXRangeMaximum(20f)
        chart.moveViewToX(10f)
    }
}