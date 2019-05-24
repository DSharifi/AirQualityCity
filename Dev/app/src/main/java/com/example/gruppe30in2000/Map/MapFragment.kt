package com.example.gruppe30in2000.Map


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.Settings.PreferenceFragment
import com.example.gruppe30in2000.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_map, container, false)
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = mView.findViewById<MapView>(R.id.map)
        mMapView.onCreate(null)
        mMapView.onResume()
        mMapView.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap


        // Customize the map style
        try {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val chosen = sp.getString(PreferenceFragment.mSKey, "1")
            if (chosen.toInt() == 2) {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.map_style
                    )
                )
                if (!success) {
                    print("Styling parsing failed")
                }
            }
            if (chosen.toInt() == 1) {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.map_alt_style
                    )
                )
                if (!success) {
                    print("Styling parsing failed")
                }
            }
        } catch (e: Resources.NotFoundException) {
            print("Cant find style")
        }
        activateLocationIfEnabled()

        //Moves the camera to Oslo
        val oslo = LatLng(59.911491, 10.757933)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo,5.0F))

        //Creates StationHandler and adds all stations
        val mapStations = MapStationsHandler(mMap, this.context!!)
        mapStations.addAllStations(MainActivity.staticAirQualityStationsList)

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setPadding(25, 25, 25, 125)
    }


    //Activates location button on map if its enabled, else show toast
    fun activateLocationIfEnabled() {
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        }
        else {
            Toast.makeText(activity!!.applicationContext, R.string.enable_location, Toast.LENGTH_LONG).show()
        }
    }


}