package com.example.gruppe30in2000.Settings

import android.app.NotificationManager
import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.gruppe30in2000.FavCity.FavoriteCity
import com.example.gruppe30in2000.StationUtil.AQILevel
import com.example.gruppe30in2000.R


class Notification (context : Context, notifyMan : NotificationManager) {
    val parentContext = context
    val notificationManager = notifyMan


    fun notifyer() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(parentContext)

        var id = 1232132

        for (station in FavoriteCity.dataset) {

            if (prefs.getString(PreferenceFragment.alertValue, "10").toInt() <= AQILevel.getAlertLevel(station.aqiValue)){

                Log.e(prefs.getString(PreferenceFragment.alertValue, "10"), AQILevel.getAlertLevel(station.aqiValue).toString())

                val builder = NotificationCompat.Builder(parentContext)
                    .setSmallIcon(R.drawable.ic_warning_blue_24dp)
                    .setContentTitle("AQS: " + station.title)
                    .setContentText("Forurensingsnivå: " + station.description)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("Forurensingsnivå: " + station.description)
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                id++
                notificationManager.notify(id, builder.build())
            }
        }
    }
}