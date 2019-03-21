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
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
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
