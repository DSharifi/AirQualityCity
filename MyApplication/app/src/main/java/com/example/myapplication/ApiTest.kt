package com.example.myapplication
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import java.util.*

data class Station(
    val eoi: String,
    val longitude: Double,
    val latitude: Double
)

data class Time(
    val from: Any,
    val to: Any
)

data class AirQualityStation(
    val eoi: String,
    val data: Any
)

fun main() {
    val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations")
    val gson = Gson()
    println(response.text)
    val stationsList = gson.fromJson<List<Station>>(response.text)
    println(stationsList[0])

    for(station in stationsList){
        val airQualityResponse = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=" + station.eoi)
        println(airQualityResponse.text)
        //val airQualityList = gson.fromJson<List<AirQualityStation>>(airQualityResponse.text)
        //println(airQualityList[0])
    }
}