package com.example.gruppe30in2000.Map

import android.app.AlertDialog
import android.app.Dialog
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
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.AQILevel
import com.example.gruppe30in2000.R
import com.example.gruppe30in2000.AQILevel.Companion.getAQILevel
import com.example.gruppe30in2000.FavCity.FavoriteCity
import com.example.gruppe30in2000.MainActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.random.Random
import java.util.*

class MapStationsHandler(googleMap: GoogleMap, context: Context) : GoogleMap.OnMarkerClickListener {
    val mMap : GoogleMap
    lateinit var mMarker : Marker

    val parentContext = context
    val greenHeat = arrayListOf<LatLng>()
    val yellowHeat = arrayListOf<LatLng>()
    val redHeat = arrayListOf<LatLng>()

    val calendar = Calendar.getInstance()
    val currentHour = calendar .get(Calendar.HOUR_OF_DAY)

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
            aqi = station.data.time[currentHour-1].variables.AQI.value


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
        val svevestov = dialogView.findViewById<TextView>(R.id.pollution)
        val nitrogen = dialogView.findViewById<TextView>(R.id.pollution2)
        val ozone = dialogView.findViewById<TextView>(R.id.pollution3)
        val nitlvls = dialogView.findViewById<TextView>(R.id.pollution4)
        val pm10lvls = dialogView.findViewById<TextView>(R.id.pollution5)
        val aqiLevel = dialogView.findViewById<TextView>(R.id.pollution6)

        val location = tempLocation.toString()

        for (station in MainActivity.staticAirQualityStationsList) {
            if (station.meta.location.name.equals(location)) {
                Log.e("TESTE MAP ADD", "KOM INN :)")

                val calendar = Calendar.getInstance()
                val currentHour = calendar .get(Calendar.HOUR_OF_DAY)
                val aqiValue = station.data.time[currentHour-1].variables.AQI.value
                val lvl = AQILevel.getAQILevelString(aqiValue)

                val ozonUnit = station.data.time[currentHour-1].variables.o3_concentration.units
                val ozvalue = station.data.time[currentHour-1].variables.o3_concentration.value
                val nOVal = station.data.time[currentHour-1].variables.no2_concentration.value
                val nOunit = station.data.time[currentHour-1].variables.no2_concentration.units
                val pm10val = station.data.time[currentHour-1].variables.pm10_concentration.value
                val pm10Unit  = station.data.time[currentHour-1].variables.pm10_concentration.units
                val nitShip = station.data.time[currentHour-1].variables.no2_local_fraction_shipping.value
                val nitHeating = station.data.time[currentHour-1].variables.no2_local_fraction_heating.value
                val nitInd = station.data.time[currentHour-1].variables.no2_local_fraction_industry.value
                val nitExc = station.data.time[currentHour-1].variables.no2_local_fraction_traffic_exhaust.value
                val pmHeat = station.data.time[currentHour-1].variables.pm10_local_fraction_heating.value
                val pmShip = station.data.time[currentHour-1].variables.pm10_local_fraction_shipping.value
                val pmInd = station.data.time[currentHour-1].variables.pm10_local_fraction_industry.value
                val pmExc = station.data.time[currentHour-1].variables.pm10_local_fraction_traffic_exhaust.value
                val pmNonEx = station.data.time[currentHour-1].variables.pm10_local_fraction_traffic_nonexhaust.value

                val sSText = "Svevestøv nivå: " + String.format("%.2f", pm10val) + pm10Unit
                val nitText = "Nitrogeninnhold: " + String.format("%.2f", nOVal) + nOunit
                val ozText = "Ozon nivå: " + String.format("%.2f", ozvalue) + ozonUnit
                val aqiText = "AQI nivå: " + String.format("%.2f", aqiValue) + "\n"

                val nitrogenLvls = "Nitrogenkilder:\nOppvarming: " + nitHeating.toString() + "%\nIndustri: " + nitInd +
                        "%\nTrafikk/Eksos: " + nitExc + "%\nShipping: " + nitShip + "%"

                val pm10Lvls = "Svevestøvkilder:\nOppvarming: " + pmHeat.toString() + "%\nIndustri: " + pmInd +
                        "%\nEksos: " + pmExc + "%\nTrafikk: " + pmNonEx + "%\nShipping: " + pmShip + "%"

                svevestov.text = sSText
                nitrogen.text = nitText
                ozone.text = ozText
                nitlvls.text = nitrogenLvls
                pm10lvls.text = pm10Lvls
                tittel.text = location
                nivaTxt.text = lvl
                aqiLevel.text = aqiText

                if (lvl.equals("Hoy")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_lens_red_35dp
                        ))
                }

                if (lvl.equals("Moderat")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_lens_yellow_35dp
                        ))
                }

                if (lvl.equals("Lav")) {
                    riskDisplay.setImageDrawable(
                        ContextCompat.getDrawable(
                            parentContext,
                            R.drawable.ic_lens_green_35dp
                        ))
                }
            }
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
}