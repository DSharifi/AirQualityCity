package com.example.gruppe30in2000.FavCity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.API.No2LocalFractionHeating
import com.example.gruppe30in2000.AQILevel
import java.util.*


class CityElement(station: AirQualityStation, currentHour: Int) {

    val index = station.index
    val location= station.meta.location
    val superLocation = station.meta.superlocation
    val calendar = Calendar.getInstance()
    val timeindex = currentHour
    var aqiValue = station.data.time[currentHour].variables.AQI.value
    val data = station.data

    var title = location.name + ", " + superLocation.name
    var description = AQILevel.getAQILevelString(aqiValue)

    var ozonUnit = station.data.time[currentHour].variables.o3_concentration.units
    var ozvalue = station.data.time[currentHour].variables.o3_concentration.value
    var nOVal = station.data.time[currentHour].variables.no2_concentration.value
    var nOunit = station.data.time[currentHour].variables.no2_concentration.units
    var pm10val = station.data.time[currentHour].variables.pm10_concentration.value
    var pm10Unit  = station.data.time[currentHour].variables.pm10_concentration.units
    var nitShip = station.data.time[currentHour].variables.no2_local_fraction_shipping.value
    var nitHeating = station.data.time[currentHour].variables.no2_local_fraction_heating.value
    var nitInd = station.data.time[currentHour].variables.no2_local_fraction_industry.value
    var nitExc = station.data.time[currentHour].variables.no2_local_fraction_traffic_exhaust.value
    var pmHeat = station.data.time[currentHour].variables.pm10_local_fraction_heating.value
    var pmShip = station.data.time[currentHour].variables.pm10_local_fraction_shipping.value
    var pmInd = station.data.time[currentHour].variables.pm10_local_fraction_industry.value
    var pmExc = station.data.time[currentHour].variables.pm10_local_fraction_traffic_exhaust.value
    var pmNonEx = station.data.time[currentHour].variables.pm10_local_fraction_traffic_nonexhaust.value
    var pm25val = station.data.time[currentHour].variables.pm25_concentration.value
    var pm25unit = station.data.time[currentHour].variables.pm25_concentration.units
}
