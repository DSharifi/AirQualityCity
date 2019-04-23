package com.example.gruppe30in2000.Map

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.R
import com.example.gruppe30in2000.AQILevel.Companion.getAQILevel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.random.Random

class MapStationsHandler(googleMap: GoogleMap, context: Context) : GoogleMap.OnMarkerClickListener {
    val mMap : GoogleMap
    lateinit var mMarker : Marker

    val parentContext = context
    val greenHeat = arrayListOf<LatLng>()
    val yellowHeat = arrayListOf<LatLng>()
    val redHeat = arrayListOf<LatLng>()

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
            aqi = station.data.time[0].variables.AQI.value


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

        val dialogView = LayoutInflater.from(parentContext).inflate(R.layout.add_favourite_map_dialog, null)
        dialogBuilder.setView(dialogView) // set the view into the builder
        val alertDialog = dialogBuilder.create()
        alertDialog.show()


        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
        val leggtilButton = dialogView.findViewById<Button>(R.id.add_button)
        val title = dialogView.findViewById<TextView>(R.id.edit_title)
        title.text = marker?.title


        cancelButton.setOnClickListener {
            alertDialog.hide()
        }


        // TODO: Receive markers info (location etc..) in FavoriteCity, use LocalbroadcastManager??
        // TODO: Sende riktig description
        leggtilButton.setOnClickListener {
            val location = tempLocation .toString()
            val description = "lav"

            val intent = Intent("from-mapstationhandler")
            intent.putExtra("location", location)
            intent.putExtra("description", description)
            LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent)
            alertDialog.hide()
        }

        return true
    }


    fun createHeatMap(){
        val heatmap = Heatmap(mMap, greenHeat, yellowHeat, redHeat)
    }
}
