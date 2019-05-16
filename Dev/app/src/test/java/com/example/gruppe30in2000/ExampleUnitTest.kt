package com.example.gruppe30in2000

import android.location.Location
import android.view.View
import com.example.gruppe30in2000.API.AirQualityStation
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun get_station_list() {
        var stations = getStations()
        assert(stations.isNotEmpty())
    }

    @Test
    fun get_station_measurements() {
        var measurements = getAirStations()
        assert(measurements.isNotEmpty())
    }


//    @Test
//    fun nearest_station_is_bekkestua() {
////        var stations = getAirStations()
////
////        var location = Location("")
////
////
////        location.latitude = 63.397237
////        location.longitude = 10.591130
////
////
////        var foundStation = getNearestStation(location, stations)
//
//        var get
//
//
//
//        assertEquals("Bekkestua", foundStation.meta.location.name);
//    }

}
