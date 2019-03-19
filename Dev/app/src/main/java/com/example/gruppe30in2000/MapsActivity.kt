package com.example.gruppe30in2000

import android.Manifest
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.annotation.NonNull








class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Customize the map style
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )

            if (!success) {
                //Log.e(FragmentActivity.TAG, "Style parsing failed.")
                print("Styling parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            // Log.e(FragmentActivity.TAG, "Can't find style. Error: ", e)
            print("Cant find style")
        }



        enableMyLocation()


        // Add a marker in Oslo and move the camera
        val oslo = LatLng(59.911491, 10.757933)
        mMap.addMarker(MarkerOptions().position(oslo).title("Marker in Oslo"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(oslo))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo,5.0F))

        addStation(61.0,9.0, "testStation")
    }


    //Add a pin to the map with the position and name
    fun addStation(lat : Double, lng : Double, name : String){
        val tmp = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(tmp).title("Station name: " + name))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(tmp))
    }


    // Map - Current location
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }


    fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
                //break
            }
        }
    }


}