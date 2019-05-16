package com.example.gruppe30in2000.API

import android.util.Log
import com.google.gson.Gson
import com.github.salomonbrys.kotson.*

class AirQualityStationCollection{
        var airQualityStationList = ArrayList<AirQualityStation>()
        private val userAgent = "Gruppe30"

        init{
                airQualityStationList = getAirStations()
        }

        fun getStations() : ArrayList<Station> {
                val url = "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations"
                val response = khttp.get(url, headers = mapOf("User-Agent" to userAgent))

                return Gson().fromJson(response.text)
        }

        fun getAirStations() : ArrayList<AirQualityStation> {
                val stationList = ArrayList<AirQualityStation>()
                val stations = getStations()

                val gson = Gson()

                for (station in stations) {
                        val url =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=${station.eoi}"
                        val airQualityResponse = khttp.get(url, headers = mapOf("User-Agent" to userAgent))
                        stationList.add(gson.fromJson(airQualityResponse.text))

                }

                Log.e("test", "test")
                return stationList
        }

}

class AirQualityStation(val data: Data, val meta: Meta) {
    // TODO: Legger til metoder her. (Get, og set, etc.)
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
        // viktig - svevestoev
        val AQI_pm10: AQIPm10,
        // ogsaa svevestoev
        val AQI_pm25: AQIPm25,
        // nitrogen dioksid
        val no2_concentration: No2Concentration,
        val no2_local_fraction_heating: No2LocalFractionHeating,
        val no2_local_fraction_industry: No2LocalFractionIndustry,
        val no2_local_fraction_shipping: No2LocalFractionShipping,
        val no2_local_fraction_traffic_exhaust: No2LocalFractionTrafficExhaust,
        val no2_nonlocal_fraction: No2NonlocalFraction,
        // bakkenear ozone
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

//open data class Measurement(
//    val units: String,
//    val value: Double
//)

data class No2Concentration(
        val units: String,
        val value: Double
)

data class Pm25LocalFractionHeating(
        val units: String,
        val value: Int
)

data class Pm25Concentration(
        val units: String,
        val value: Double
)

data class No2LocalFractionShipping(
        val units: String,
        val value: Int
)

data class No2LocalFractionIndustry(
        val units: String,
        val value: Int
)

data class AQI(
        val units: String,
        val value: Double
)

data class Pm25LocalFractionShipping(
        val units: String,
        val value: Int
)

data class Pm10Concentration(
        val units: String,
        val value: Double
)

data class No2LocalFractionHeating(
        val units: String,
        val value: Int
)

data class O3NonlocalFraction(
        val units: String,
        val value: Int
)

data class Pm25NonlocalFraction(
        val units: String,
        val value: Int
)

data class Pm10LocalFractionTrafficNonexhaust(
        val units: String,
        val value: Int
)

data class AQIPm25(
        val units: String,
        val value: Double
)

data class Pm10LocalFractionTrafficExhaust(
        val units: String,
        val value: Int
)

data class Pm25LocalFractionIndustry(
        val units: String,
        val value: Int
)

data class AQINo2(
        val units: String,
        val value: Double
)

data class Pm10LocalFractionShipping(
        val units: String,
        val value: Int
)

data class AQIO3(
        val units: String,
        val value: Double
)

data class No2LocalFractionTrafficExhaust(
        val units: String,
        val value: Int
)

data class Pm25LocalFractionTrafficExhaust(
        val units: String,
        val value: Int
)

data class No2NonlocalFraction(
        val units: String,
        val value: Int
)

data class Pm10LocalFractionIndustry(
        val units: String,
        val value: Int
)

data class Pm10LocalFractionHeating(
        val units: String,
        val value: Int
)

data class AQIPm10(
        val units: String,
        val value: Double
)

data class Pm10NonlocalFraction(
        val units: String,
        val value: Int
)

data class O3Concentration(
        val units: String,
        val value: Double
)

data class Pm25LocalFractionTrafficNonexhaust(
        val units: String,
        val value: Int
)

data class Meta(
        val location: Location,
        val reftime: String,
        val sublocations: List<Any>,
        val superlocation: Superlocation
)

data class Superlocation(
        val areaclass: String,
        val areacode: String,
        val latitude: String,
        val longitude: String,
        val name: String,
        val superareacode: String
)

data class Location(
        val areacode: String,
        val latitude: String,
        val longitude: String,
        val name: String
)