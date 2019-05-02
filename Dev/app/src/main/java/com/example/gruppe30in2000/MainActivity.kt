package com.example.gruppe30in2000


import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import com.example.gruppe30in2000.FavCity.FavoriteCity
import com.example.gruppe30in2000.Map.MapFragment

import android.util.Log
import android.view.View
import android.widget.Toast

import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.API.AsyncApiGetter
import com.example.gruppe30in2000.API.OnTaskCompleted
import com.example.gruppe30in2000.FavCity.CityElement
import com.example.gruppe30in2000.Map.MapStationsHandler
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(), OnTaskCompleted {

    companion object {
        //Have to be static in order to access it from MapFragment
        var staticAirQualityStationsList = ArrayList<AirQualityStation>()
    }

    lateinit var notificationManager : NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial_welcome_view)

        //gets data from api - runs in async thread
        val asyncApiGetter = AsyncApiGetter(this)
        asyncApiGetter.execute()

        // Creates LocationPermission object and asks user to allow location
        val lp = LocationPermission(this)
        lp.enableMyLocation()
    }

    override fun onTaskCompletedApiGetter(list: ArrayList<AirQualityStation>){
        if(list.isEmpty()){
            Toast.makeText(this, "Kunne ikke hente data", Toast.LENGTH_LONG).show()
        }
        staticAirQualityStationsList = list
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        // TODO TEMPORARY TEST FAVOURITE CITY LIST
        // Reset favourite city list everytime the app start.
        FavoriteCity.dataset = ArrayList<CityElement>()
        replaceFragment(FavoriteCity())
    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(FavoriteCity())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_map -> {
                val mf = MapFragment()
                replaceFragment(mf)

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
                val sf = PreferenceFragment()

                replaceFragment(sf)
                //message.setText(R.string.title_notifications)
                notifyer()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

    }




    //Burde flyttes ut til en annen fil


    fun notifyer() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        var id = 1232132

        for (station in FavoriteCity.dataset) {

            if (prefs.getString(PreferenceFragment.alertValue, "10").toInt() <= AQILevel.getAlertLevel(station.aqiValue)){

                Log.e(prefs.getString(PreferenceFragment.alertValue, "10"), AQILevel.getAlertLevel(station.aqiValue).toString())

                val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_warning_blue_24dp)
                    .setContentTitle("AQS: " + station.title)
                    .setContentText("Forurensingsnivå: ??")
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