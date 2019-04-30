package com.example.gruppe30in2000.Map


import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.Console


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
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.map_style
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

        activateLocationIfEnabled()

        //Moves the camera to Oslo
        val oslo = LatLng(59.911491, 10.757933)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo,5.0F))

        //Creates StationHandler and adds all stations
        val mapStations = MapStationsHandler(mMap, this.context!!)
        mapStations.addAllStations(MainActivity.staticAirQualityStationsList)


    }


    //Activates location button on map if its enabled, else show toast
    //TODO: Endre else til å linke til settings slik at det kan settes på manuelt
    fun activateLocationIfEnabled() {
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        }
        else {
            Toast.makeText(activity!!.applicationContext, R.string.enable_location, Toast.LENGTH_LONG).show()

            /*
            https://stackoverflow.com/questions/31127116/open-app-permission-settings/33268774
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
             */
        }
    }


}