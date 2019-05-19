package com.example.gruppe30in2000.Map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.gruppe30in2000.*
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.StationUtil.AQILevel.Companion.getAQILevel
import com.example.gruppe30in2000.StationUtil.AQILevel
import com.example.gruppe30in2000.StationUtil.GraphActivity
import com.example.gruppe30in2000.StationUtil.PieChartActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.*

class MapStationsHandler(googleMap: GoogleMap, context: Context) : GoogleMap.OnMarkerClickListener {
    val mMap : GoogleMap
    lateinit var mMarker : Marker

    val parentContext = context
    val greenHeat = arrayListOf<LatLng>()
    val yellowHeat = arrayListOf<LatLng>()
    val redHeat = arrayListOf<LatLng>()

    val calendar = Calendar.getInstance()

    val time = calendar.get(Calendar.HOUR_OF_DAY)
    val date = calendar.get(Calendar.DATE)

    val timeIndex = getTimeIndex(time, date)

    init {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
    }


    //Adds all stations to map
    fun addAllStations(stationlist: ArrayList<AirQualityStation>){
        var lat : Double
        var lng : Double
        var name : String
        var aqi : Double


        for (station in stationlist){
            lat = station.meta.location.latitude.toDouble()
            lng = station.meta.location.longitude.toDouble()
            name = station.meta.location.name
            aqi = station.data.time[timeIndex].variables.AQI.value


            addStation(lat, lng, name, aqi)
        }

        createHeatMap()
    }


    //Add a pin to the map with the position and name
    // TODO: Endre til å skille på riktig luftverdier
    fun addStation(lat : Double, lng : Double, name : String, aqiValue : Double){

        //Det skal egentlig være lat først, blir feil om man ikke bytter på
        //Lurer på om api'et bytter på disse
        val pos = LatLng(lng, lat)
        val iconColor : BitmapDescriptor
        val aqiLevel = getAQILevel(aqiValue)

        when (aqiLevel) {
            1 ->{
                iconColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                greenHeat.add(pos)
            } 2 ->{
            iconColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            yellowHeat.add(pos)
        }else ->{
            iconColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            redHeat.add(pos)
        }
        }
        mMarker = mMap.addMarker(MarkerOptions().position(pos).title("Station: " + name).icon(iconColor))

    }


    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.e("MapstationHandler", "Marker at location ${marker?.title} clicked")

        val tempLocation = marker?.title?.removeRange(0,9)

        val dialogBuilder = AlertDialog.Builder(parentContext) // make a dialog builder

        val dialogView = LayoutInflater.from(parentContext).inflate(R.layout.maptaginfoview, null)
        dialogBuilder.setView(dialogView) // set the view into the builder
        val alertDialog = dialogBuilder.create()
        alertDialog.show()


        val addbutton = dialogView.findViewById<ImageButton>(R.id.add_button)
        val riskDisplay = dialogView.findViewById<ImageView>(R.id.risk_display)
        val tittel = dialogView.findViewById<TextView>(R.id.title_text)
        val nivaTxt = dialogView.findViewById<TextView>(R.id.description_text)
        val svevestovpm10 = dialogView.findViewById<TextView>(R.id.pollution1)
        val svevestovpm25 = dialogView.findViewById<TextView>(R.id.pollution)
        val nitrogen = dialogView.findViewById<TextView>(R.id.pollution2)
        val ozone = dialogView.findViewById<TextView>(R.id.pollution3)
        val aqiLevel = dialogView.findViewById<TextView>(R.id.pollution4)
        val linechartButton = dialogView.findViewById<Button>(R.id.linechart)
        val pm10Button = dialogView.findViewById<Button>(R.id.piechart_pm10)
        val pm25Button = dialogView.findViewById<Button>(R.id.piechart_pm25)
        val no2Button = dialogView.findViewById<Button>(R.id.piechart_no2)

        val location = tempLocation.toString()

        val calendar = Calendar.getInstance()
        val currentHour = getTimeIndex(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DATE))
        var index = 0
        for (station in MainActivity.staticAirQualityStationsList) {

            if (station.meta.location.name.equals(location)) {
                Log.e("TESTE MAP ADD", "KOM INN :)")
                index = station.index
                val aqiValue = station.data.time[currentHour].variables.AQI.value
                val lvl = AQILevel.getAQILevelString(aqiValue)
                val ozonUnit = station.data.time[currentHour].variables.o3_concentration.units
                val ozvalue = station.data.time[currentHour].variables.o3_concentration.value
                val nOVal = station.data.time[currentHour].variables.no2_concentration.value
                val nOunit = station.data.time[currentHour].variables.no2_concentration.units
                val pm10val = station.data.time[currentHour].variables.pm10_concentration.value
                val pm10Unit  = station.data.time[currentHour].variables.pm10_concentration.units
                val pm25val = station.data.time[currentHour].variables.pm25_concentration.value
                val pm25Unit  = station.data.time[currentHour].variables.pm25_concentration.units

                val sS10Text = "Svevestøv PM10 nivå: " + String.format("%.2f", pm10val) + pm10Unit
                val sS25Text = "Svevestøv PM2.5 nivå: " + String.format("%.2f", pm25val) + pm25Unit
                val nitText = "Nitrogeninnhold: " + String.format("%.2f", nOVal) + nOunit
                val ozText = "Ozon nivå: " + String.format("%.2f", ozvalue) + ozonUnit
                val aqiText = "AQI nivå: " + String.format("%.2f", aqiValue) + "\n"


                svevestovpm10.text = sS10Text
                svevestovpm25.text = sS25Text
                nitrogen.text = nitText
                ozone.text = ozText
                tittel.text = location
                aqiLevel.text = aqiText

                if (lvl.equals("Hoy")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_sad_svgrepo_com
                        ))
                    nivaTxt.setBackgroundResource(R.drawable.rounded_bad)
                    nivaTxt.text = " Dårlig "
                }

                if (lvl.equals("Moderat")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_straight_svgrepo_com
                        ))
                    nivaTxt.setBackgroundResource(R.drawable.rounded_moderate)
                    nivaTxt.text = " Moderat "
                }

                if (lvl.equals("Lav")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_smile_svgrepo_com
                        ))
                    nivaTxt.setBackgroundResource(R.drawable.rounded_good)
                    nivaTxt.text = " God "
                }
            }
        }

        linechartButton.setOnClickListener{
            val i = Intent(this.parentContext, GraphActivity::class.java)
            i.putExtra("index",index)
            ContextCompat.startActivity(this.parentContext, i, null)
        }
        pm10Button.setOnClickListener{
            val i = Intent(this.parentContext, PieChartActivity::class.java)
            i.putExtra("index",index)
            i.putExtra("chartNr", 0)
            i.putExtra("timeIndex", currentHour)
            ContextCompat.startActivity(this.parentContext, i, null)
        }
        pm25Button.setOnClickListener{
            val i = Intent(this.parentContext, PieChartActivity::class.java)
            i.putExtra("index",index)
            i.putExtra("chartNr", 1)
            i.putExtra("timeIndex", currentHour)
            ContextCompat.startActivity(this.parentContext, i, null)
        }
        no2Button.setOnClickListener{
            val i = Intent(this.parentContext, PieChartActivity::class.java)
            i.putExtra("index",index)
            i.putExtra("chartNr", 2)
            i.putExtra("timeIndex", currentHour)
            ContextCompat.startActivity(this.parentContext, i, null)
        }


        addbutton.setOnClickListener {

            val intent = Intent("from-mapstationhandler")
            intent.putExtra("location", location)
            LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent)
            alertDialog.hide()
        }

        return true
    }


    fun createHeatMap(){
        val heatmap = Heatmap(mMap, greenHeat, yellowHeat, redHeat)
    }

    fun getTimeIndex(time : Int, date : Int) : Int{
        var c = 0
        MainActivity.staticAirQualityStationsList[0].data.time.forEach{
            val datetime = it.from.split("T")
            var d = datetime[0].takeLast(2).toInt()
            var t = datetime[1].take(2).toInt()

            if(date == d && time == t){
                return c;
            }
            c++
        }
        return 0
    }
}