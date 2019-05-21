package com.example.gruppe30in2000

import android.location.Location
import com.example.gruppe30in2000.API.AirQualityStation
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FunctionUnitTesting {


    @Test
    fun get_station_list() {
        val stations = getStations()
        assert(stations.isNotEmpty())
    }

    @Test
    fun assert_Getting_All_Stations() {
        val stations = getStations()
        val measurements = getAirStations()

        assertEquals(stations.size, measurements.size)
    }




    @Test
    fun assert_Parsing_of_metaInfo() {
        System.out.println(System.getProperty("user.dir"));
        val json :String = File("src/test/java/com/example/gruppe30in2000/DummyJSON.JSON").readText(Charsets.UTF_8)
        val gson = Gson()

        val station = gson.fromJson<AirQualityStation>(json)

        assertEquals(station.meta.location.name, "Alnabru")
    }

    @Test
    fun get_station_measurements() {
        val measurements = getAirStations()
        assert(measurements.isNotEmpty())
    }


    @Test
    fun nearest_station_is_bekkestua() {
        val stations = getAirStations()

        val location = Location("")


        println(location)
        println(location.toString())

        location.latitude = 63.397237
        location.longitude = 10.591130




        val foundStation = getNearestStation(location, stations)
        assertEquals("Bekkestua", foundStation.meta.location.name)
    }
}

