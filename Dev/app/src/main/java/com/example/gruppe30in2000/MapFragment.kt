package com.example.gruppe30in2000


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gruppe30in2000.MainActivity.Companion.REQUEST_LOCATION_PERMISSION
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


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

        Log.e("------ onMapReady","tesssttt")
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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        when (requestCode) {
            MainActivity.getLocationPermissionStatus() -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //break
            }
        }
    }

    // Map - Current location


    fun enableMyLocation() {
        if (MainActivity.getLocationPermissionStatus() == 1) {
            mMap.isMyLocationEnabled = true
        }
    }

}
