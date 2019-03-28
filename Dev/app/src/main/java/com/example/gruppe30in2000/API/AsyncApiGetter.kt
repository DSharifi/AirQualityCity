package com.example.gruppe30in2000.API

import android.os.AsyncTask
import java.lang.Exception

interface OnTaskCompleted{
    fun onTaskCompletedApiGetter(values: ArrayList<AirQualityStation>)
}

class AsyncApiGetter : AsyncTask<Unit, Unit, String> {
    var listener : OnTaskCompleted
    constructor(l : OnTaskCompleted){
        listener = l
    }
    var airQualityList = ArrayList<AirQualityStation>()
    override fun doInBackground(vararg params: Unit?): String? {
        try {
            val a = AirQualityStationCollection()
            airQualityList = a.airQualityStationList
        } catch (e : Exception){ }
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