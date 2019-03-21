package com.example.gruppe30in2000


import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

interface OnTaskCompleted{
    fun onTaskCompletedApiGetter(values: ArrayList<AirQualityStation>);
}

class MainActivity : AppCompatActivity(), OnTaskCompleted  {

    var airQualityStationsList = ArrayList<AirQualityStation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       val asyncApiGetter = AsyncApiGetter(this)
        asyncApiGetter.execute()

        // Creates LocationPermission object and asks user to allow location
        val lp = LocationPermission(this)
        lp.enableMyLocation()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(HomeFragment())

//        val intent = Intent(this, FavoriteCity::class.java)
//      startActivity(intent)
    }

    override fun onTaskCompletedApiGetter(list: ArrayList<AirQualityStation>){
        airQualityStationsList = list
        Log.e("gggggggg", airQualityStationsList[0].meta.superlocation.name)
    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_map -> {
                val mf = MapFragment()
                //mf.onRequestPermissionsResult()
                replaceFragment(mf)

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
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
