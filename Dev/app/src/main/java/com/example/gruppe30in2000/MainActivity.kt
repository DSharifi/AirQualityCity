package com.example.gruppe30in2000


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
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
import com.example.gruppe30in2000.FavCity.FavoriteCity
import com.example.gruppe30in2000.Map.MapFragment
import com.example.gruppe30in2000.Map.MapStationsHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnTaskCompleted {

    companion object {
        //Have to be static in order to access it from MapFragment
        var staticAirQualityStationsList = ArrayList<AirQualityStation>()
    }

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
                val mf = SettingsFragment()
                replaceFragment(mf)
                //message.setText(R.string.title_notifications)
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
}
