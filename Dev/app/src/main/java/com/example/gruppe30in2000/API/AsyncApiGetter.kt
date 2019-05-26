package com.example.gruppe30in2000.API

import android.os.AsyncTask
import android.util.Log
import java.lang.Exception



interface OnTaskCompleted{
    fun onTaskCompletedApiGetter(values: ArrayList<AirQualityStation>, saveData: Boolean)
}




class AsyncApiGetter : AsyncTask<Unit, Unit, String> {
    var listener : OnTaskCompleted
    var airQualityList = ArrayList<AirQualityStation>()




    constructor(listener : OnTaskCompleted){
        this.listener = listener
    }



    override fun doInBackground(vararg params: Unit?): String? {
        try {
            Log.e("IN BCK", "IN BCK")
            val a = AirQualityStationCollection()
            airQualityList = a.airQualityStationList
            a.airQualityStationList.forEach {
                Log.e("getter", it.meta.location.name)
            }
            Log.e("IN DONE", "IN DONE")
        } catch (e : Exception){ }
        return null
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onTaskCompletedApiGetter(airQualityList, true)
    }


    fun getAirQualityStationsList() : ArrayList<AirQualityStation>{
        return airQualityList
    }










}

