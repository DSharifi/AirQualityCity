package com.example.gruppe30in2000


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creates LocationPermission object and asks user to allow location
        val lp = LocationPermission(this)
        lp.enableMyLocation()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(FavoriteCity())

//        val intent = Intent(this, FavoriteCity::class.java)
//      startActivity(intent)

    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(FavoriteCity())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_map -> {
                val mf = MapFragment()
                //mf.onRequestPermissionsResult()
                replaceFragment(mf)

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
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
