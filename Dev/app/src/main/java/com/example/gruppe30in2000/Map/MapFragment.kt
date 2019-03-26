<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
<<<<<<< HEAD
package com.example.gruppe30in2000
=======
package com.example.gruppe30in2000.Map
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
=======
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
//import com.google.maps.android.heatmaps.Gradient
//import com.google.maps.android.heatmaps.HeatmapTileProvider


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
=======
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var mView: View
<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
=======
    private var airQualityStationList = ArrayList<AirQualityStation>()
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
        // Inflate the layout for this fragment
=======
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
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

<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt

=======
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt

=======
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
        // Customize the map style
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.map_style
                )
            )
<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt

=======
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
            if (!success) {
                //Log.e(FragmentActivity.TAG, "Style parsing failed.")
                print("Styling parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            // Log.e(FragmentActivity.TAG, "Can't find style. Error: ", e)
            print("Cant find style")
        }

<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt

        activateLocationIfEnabled()

        // Add a marker in Oslo and move the camera
        val oslo = LatLng(59.911491, 10.757933)
        mMap.addMarker(MarkerOptions().position(oslo).title("Marker in Oslo"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo,5.0F))

        addStation(61.0,9.0, "testStation", 1)
        addStation(62.0,9.5, "testStation", 2)

        var heatmap = Heatmap(mMap)
    }


    //Add a pin to the map with the position and name
    // TODO: endre slik den sjekker på luftverdien istedenfor int
    fun addStation(lat : Double, lng : Double, name : String, color : Int){
        val tmp = LatLng(lat, lng)

        when (color) {
            1 ->
                mMap.addMarker(MarkerOptions().position(tmp).title("Station name: " + name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

            2 ->
                mMap.addMarker(MarkerOptions().position(tmp).title("Station name: " + name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))

            3 ->
                mMap.addMarker(MarkerOptions().position(tmp).title("Station name: " + name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        }
=======
        activateLocationIfEnabled()

        //Moves the camera to Oslo
        val oslo = LatLng(59.911491, 10.757933)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo,5.0F))

        //Creates StationHandler and adds all stations
        val mapStations = MapStationsHandler(mMap)
        mapStations.addAllStations(MainActivity.staticAirQualityStationsList)

>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
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
        }
    }


}
<<<<<<< HEAD:Dev/app/src/main/java/com/example/gruppe30in2000/MapFragment.kt
=======
package com.example.gruppe30in2000


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var mView: View
    private var airQualityStationList = ArrayList<AirQualityStation>()

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
        val mapStations = MapStationsHandler(mMap)
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
        }
    }


}
>>>>>>> 8d71fdcd676ab5e4cf3f8ab83dd65291b0899acc
=======
>>>>>>> 6ebb3f9ae1e88dbf5f12a4451ab922992d15468a:Dev/app/src/main/java/com/example/gruppe30in2000/Map/MapFragment.kt
