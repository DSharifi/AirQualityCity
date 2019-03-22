package com.example.gruppe30in2000

import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapStationsHandler(googleMap: GoogleMap){
    val mMap : GoogleMap

    val greenHeat = arrayListOf<LatLng>()
    val yellowHeat = arrayListOf<LatLng>()
    val redHeat = arrayListOf<LatLng>()


    init {
        mMap = googleMap


        val testlat = LatLng(59.5,9.0)
        val testlat2 = LatLng(59.0,9.0)
        val testlat3 = LatLng(58.5,9.0)

        /*
        greenHeat.add(testlat)
        yellowHeat.add(testlat2)
        redHeat.add(testlat3)
        */

    }

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

            addStation(lng, lat, name, aqi)
            }
        }

        //Add a pin to the map with the position and name
        // TODO: Endre til å skille på riktig luftverdier
        fun addStation(lat : Double, lng : Double, name : String, aqiValue : Double){
            val pos = LatLng(lat, lng)
            val iconcolor : BitmapDescriptor

            if (aqiValue < 50){
                iconcolor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                greenHeat.add(pos)

            } else if (aqiValue >= 50 && aqiValue < 100 ){
                iconcolor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                yellowHeat.add(pos)
            }
            else {
                iconcolor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                redHeat.add(pos)
            }

            mMap.addMarker(MarkerOptions().position(pos).title("Station name: " + name).icon(iconcolor))
        }

    fun createHeatMap(){
        val heatmap = Heatmap(mMap, greenHeat, yellowHeat, redHeat)
    }

}