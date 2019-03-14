package com.example.myapplication
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



// Stasjonsobjekt med metadata og alle
data class Measurement(
    val from: String,
    val to: String,
    val variables: Variables
)




// Returnerer en liste med alle stasjoner
fun getStations() : List<Station> {
    val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations")
    return Gson().fromJson(response.text)
}

// returner ett RefTime objekt
fun getRefTimes() : RefTime {
    val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/reftimes")
    return Gson().fromJson(response.text)
}

fun main() {
    val stationList = getStations()
    val refTimes = getRefTimes()
    println("heheh")
    println(stationList[0])

    val airQualityResponse = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=" + stationList[0].eoi)
    val airQualityList = Gson().fromJson<AirQualityStation>(airQualityResponse.text)
    println(airQualityList)


/*
    for(station in stationList){
     val airQualityResponse = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=" + station.eoi)
      //println(airQualityResponse.text)
      val airQualityList = Gson().fromJson<List<AirQualityStation>>(airQualityResponse.text)
      println("test")
      println(airQualityList[0])
  }
  */

}

// TODO: Trenger kun REFTIME for historisk data. Reftime finnes ikke for siste måling.
// TODO: Vil si: Livedata:  airqualityReponse uten parameter for reftime



