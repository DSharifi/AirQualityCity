package com.example.gruppe30in2000.Map

import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.AQILevel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.random.Random

class MapStationsHandler(googleMap: GoogleMap){
    val mMap : GoogleMap

    val greenHeat = arrayListOf<LatLng>()
    val yellowHeat = arrayListOf<LatLng>()
    val redHeat = arrayListOf<LatLng>()

    init {
        mMap = googleMap
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
            val aqiLevel = AQILevel.getAQILevel(aqiValue)

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
            mMap.addMarker(MarkerOptions().position(pos).title("Station: " + name).icon(iconColor))
        }


    fun createHeatMap(){
        val heatmap = Heatmap(mMap, greenHeat, yellowHeat, redHeat)
    }
}