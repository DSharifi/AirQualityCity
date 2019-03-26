package com.example.gruppe30in2000.API

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson

data class Station(
        // identifier, expression of interest
        val eoi: String
)


data class RefTime(
        // YYYY-MM-DDTHH:mm:ss.sssZ  -- http://www.ecma-international.org/ecma-262/5.1/#sec-15.9.1.15
        val reftimes : kotlin.Array<String>
)




// Returnerer en liste med alle stasjoner
fun getStations() : ArrayList<Station> {
    val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations")
    return Gson().fromJson(response.text)
}

// returner ett RefTime objekt
fun getRefTimes() : RefTime {
    val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/reftimes")
    return Gson().fromJson(response.text)
}

// Henter livedata for alle målestasjoner
fun getAirStations() : ArrayList<AirQualityStation> {
    val stationList = ArrayList<AirQualityStation>()
    val stations = getStations()

    val gson = Gson()

    for (station in stations) {
        // Hent stasjonmaaling
        val airQualityResponse = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=${station.eoi}")
        stationList.add(gson.fromJson(airQualityResponse.text))
    }

    return stationList
}

fun getAqi(station:Int, time:Int) : Double{
    return getAirStations()[station].data.time[time].variables.AQI.value
}

fun main() {
    //TODO: Hvordan vil frontend hente verdiene, metoder eller direkte fra objekt?
    //      this?
    println(getAirStations()[3].data.time[0].variables.AQI.value)
    // or this?
    println(getAqi(3, 0))
}

// TODO: Trenger kun REFTIME for historisk data. Reftime finnes ikke for siste måling.
// TODO: Vil si: Livedata:  airqualityReponse uten parameter for reftime