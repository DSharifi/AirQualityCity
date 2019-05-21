package com.example.gruppe30in2000

import android.location.Location
import com.example.gruppe30in2000.API.AirQualityStation


fun getNearestStation(myPos : Location, stationList: ArrayList<AirQualityStation>) : AirQualityStation  {
    var tmpPos = Location("")
    var nearestStation : AirQualityStation = stationList[0]

    tmpPos.longitude = nearestStation.meta.location.longitude.toDouble()
    tmpPos.latitude = nearestStation.meta.location.latitude.toDouble()

    var lowestDist = tmpPos.distanceTo(myPos)

//    println(myPos)
//    println(tmpPos)

//    for (station in stationList) {
//
//        tmpPos.latitude =  station.meta.location.latitude.toDouble()
//        tmpPos.longitude = nearestStation.meta.location.longitude.toDouble()
//
//        var distance = myPos.distanceTo(tmpPos)
//
//        if (distance < lowestDist) {
//            lowestDist = distance
//            nearestStation = station
//        }
//
//        println(distance)
//        println(nearestStation)
//        println(station)
//        println("\n\n\n")
//
//    }


    return nearestStation
}
