package com.example.gruppe30in2000.API

import android.util.Log
import com.google.gson.Gson
import com.github.salomonbrys.kotson.*
import java.lang.Exception
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger
import com.example.gruppe30in2000.MainActivity
import android.R.string
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock


class AirQualityStationCollection{
        var airQualityStationList = ArrayList<AirQualityStation>()
        private val userAgent = "Gruppe30"
        val g = Gson()
        val client = OkHttpClient()
        val cdl1 = CountDownLatch(1)
        lateinit var cdl2 : CountDownLatch
    init{
            val url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful()) {
                        val string = response.body()!!.string()
                        val stationlist = g.fromJson<ArrayList<Station>>(string)
                        cdl2 = CountDownLatch(stationlist.size)
                        doThis(stationlist)
                        cdl1.countDown()
                    }
                }
            })
            cdl1.await()
            cdl2.await()
        }

    fun doThis(sl : ArrayList<Station>){
        sl.forEach {
            val url =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=${it.eoi}"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful()) {
                        val string = response.body()!!.string()
                        val stationinfo = g.fromJson<AirQualityStation>(string)
                        Log.e("d", stationinfo.meta.location.name)
                        airQualityStationList.add(stationinfo)
                        cdl2.countDown()
                    }
                }
            })
        }
    }

        fun getStations() : ArrayList<Station> {
                val url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations"
                val response = khttp.get(url, headers = mapOf("User-Agent" to userAgent))

                return Gson().fromJson(response.text)
        }


        private fun getAirStations() : ArrayList<AirQualityStation> {
                val stations = getStations()
                val gson = Gson()

                val stationListArr = Array<AirQualityStation?>(stations.size, {null})

                var i = 0

                Log.e("Lengde", stations.size.toString())

                val a = CyclicBarrier(stations.size + 1)

                for (station in stations) {
                    val url =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=${station.eoi}"
                    val j = i

                    khttp.async.get(url, headers = mapOf("User-Agent" to userAgent), onResponse = {
                        stationListArr[j] = gson.fromJson(this.text)
                        Log.e("Index:", j.toString())

                        try {
                            a.await()
                        } catch (e: Exception) {}

                    }, onError = {
                        Log.e("ERROR INDEX", j.toString())
                        a.await()
                    })

                    i++
                }

                try {
                    a.await()
                } catch (e: Exception) {}

                val stationList = ArrayList<AirQualityStation>(stations.size)

                i = 0

                for (station in stationListArr) {
                        if (station != null) {
                            stationList.add(station)
                            station.index = i++
                        }
                }


                return stationList
        }

}


// Her har vi laget alle nødvendige klasser basert på det vi får fra API-et
class AirQualityStation(val data: Data, val meta: Meta) {
        var index = 0

         override fun toString(): String {
            return meta.location.name
         }
}

data class Data(
    val time: List<Time>
)

data class Time(
    val from: String,
    val to: String,
    val variables: Variables
)

data class Variables(
    val AQI: AQI,
    val AQI_no2: AQINo2,
    val AQI_o3: AQIO3,
    val AQI_pm10: AQIPm10,
    val AQI_pm25: AQIPm25,
    val no2_concentration: No2Concentration,
    val no2_local_fraction_heating: No2LocalFractionHeating,
    val no2_local_fraction_industry: No2LocalFractionIndustry,
    val no2_local_fraction_shipping: No2LocalFractionShipping,
    val no2_local_fraction_traffic_exhaust: No2LocalFractionTrafficExhaust,
    val no2_nonlocal_fraction: No2NonlocalFraction,
    val o3_concentration: O3Concentration,
    val o3_nonlocal_fraction: O3NonlocalFraction,
    val pm10_concentration: Pm10Concentration,
    val pm10_local_fraction_heating: Pm10LocalFractionHeating,
    val pm10_local_fraction_industry: Pm10LocalFractionIndustry,
    val pm10_local_fraction_shipping: Pm10LocalFractionShipping,
    val pm10_local_fraction_traffic_exhaust: Pm10LocalFractionTrafficExhaust,
    val pm10_local_fraction_traffic_nonexhaust: Pm10LocalFractionTrafficNonexhaust,
    val pm10_nonlocal_fraction: Pm10NonlocalFraction,
    val pm25_concentration: Pm25Concentration,
    val pm25_local_fraction_heating: Pm25LocalFractionHeating,
    val pm25_local_fraction_industry: Pm25LocalFractionIndustry,
    val pm25_local_fraction_shipping: Pm25LocalFractionShipping,
    val pm25_local_fraction_traffic_exhaust: Pm25LocalFractionTrafficExhaust,
    val pm25_local_fraction_traffic_nonexhaust: Pm25LocalFractionTrafficNonexhaust,
    val pm25_nonlocal_fraction: Pm25NonlocalFraction
)

data class Pm10LocalFractionIndustry(
    val units: String,
    val value: Double
)

data class No2LocalFractionShipping(
    val units: String,
    val value: Double
)

data class Pm10NonlocalFraction(
    val units: String,
    val value: Double
)

data class Pm25LocalFractionTrafficExhaust(
    val units: String,
    val value: Double
)

data class Pm10LocalFractionShipping(
    val units: String,
    val value: Double
)

data class No2Concentration(
    val units: String,
    val value: Double
)

data class Pm10LocalFractionTrafficNonexhaust(
    val units: String,
    val value: Double
)

data class Pm25NonlocalFraction(
    val units: String,
    val value: Double
)

data class AQIPm25(
    val units: String,
    val value: Double
)

data class AQIPm10(
    val units: String,
    val value: Double
)

data class O3Concentration(
    val units: String,
    val value: Double
)

data class AQINo2(
    val units: String,
    val value: Double
)

data class Pm25LocalFractionHeating(
    val units: String,
    val value: Double
)

data class Pm25LocalFractionTrafficNonexhaust(
    val units: String,
    val value: Double
)

data class No2LocalFractionTrafficExhaust(
    val units: String,
    val value: Double
)

data class O3NonlocalFraction(
    val units: String,
    val value: Double
)

data class Pm25LocalFractionShipping(
    val units: String,
    val value: Double
)

data class No2NonlocalFraction(
    val units: String,
    val value: Double
)

data class Pm10LocalFractionHeating(
    val units: String,
    val value: Double
)

data class AQI(
    val units: String,
    val value: Double
)

data class Pm25Concentration(
    val units: String,
    val value: Double
)

data class Pm25LocalFractionIndustry(
    val units: String,
    val value: Double
)

data class Pm10LocalFractionTrafficExhaust(
    val units: String,
    val value: Double
)

data class No2LocalFractionHeating(
    val units: String,
    val value: Double
)

data class Pm10Concentration(
    val units: String,
    val value: Double
)

data class AQIO3(
    val units: String,
    val value: Double
)

data class No2LocalFractionIndustry(
    val units: String,
    val value: Double
)

data class Meta(
    val location: Location,
    val reftime: String,
    val sublocations: List<Any>,
    val superlocation: Superlocation
)

data class Location(
    val areacode: String,
    val latitude: String,
    val longitude: String,
    val name: String
)

data class Superlocation(
    val areaclass: String,
    val areacode: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val superareacode: String
)