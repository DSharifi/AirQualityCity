package com.example.gruppe30in2000

import android.os.AsyncTask

class AsyncApiGetter : AsyncTask<Unit, Unit, String> {
    var listener : OnTaskCompleted
    constructor(l : OnTaskCompleted){
        listener = l
    }
    var airQualityList = ArrayList<AirQualityStation>()
    override fun doInBackground(vararg params: Unit?): String? {
        val a = AirQualityStationCollection()
        airQualityList = a.airQualityStationList
        //Log.e("ddddddddd", airQualityList[0].meta.superlocation.name)
        return null
    }
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onTaskCompletedApiGetter(airQualityList)
    }
    fun getAirQualityStationsList() : ArrayList<AirQualityStation>{
        return airQualityList
    }
}