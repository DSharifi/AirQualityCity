package com.example.gruppe30in2000.FavCity

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Activity.*
import android.content.*
import android.content.ContentValues.TAG
import android.content.RestrictionsManager.RESULT_ERROR
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.*
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.API.Data
import com.example.gruppe30in2000.API.Meta
import com.example.gruppe30in2000.AQILevel
import com.example.gruppe30in2000.AQILevel.Companion.getAQILevelString
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import com.github.salomonbrys.kotson.fromJson
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_favorite_city.*
import org.joda.time.Hours

import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class FavoriteCity : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fView: View
    private lateinit var placesClient : PlacesClient
    private lateinit var mContext : Context

    private val SecondActivityCode = 101
    // navn paa shared preferences
    private val name = "favorite cities preferences"

    // navn paa datasettet i shared preferences
    private val key = "favorite cities"

    companion object {
        var dataset = ArrayList<CityElement>()
        var addNearestStation = false
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fView = inflater.inflate(R.layout.fragment_favorite_city, container, false)

        // TODO: Finne en maate aa kun legge til naermeste stasjon engang naar appen kjorer?
        // Add the nearest station to favourite
        if (!addNearestStation) {
            addNearestStation = true
            // If list of all station is not empty then we get and add the nearest station available
            if (MainActivity.staticAirQualityStationsList.isNotEmpty()) {
                getNearestStation()
            }
        }

        return fView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val floatingButton = fView.findViewById<FloatingActionButton>(R.id.floating_button)
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, IntentFilter("from-mapstationhandler"))
        val seekbar = view.findViewById<SeekBar>(R.id.seekbar)
        val progresslabel = view.findViewById<TextView>(R.id.progress_text)
        val restore_button = view.findViewById<ImageButton>(R.id.restore_button)


        loadFavoriteElement()

        initRecycleView(dataset)


        floatingButton.setOnClickListener {
            val intent = Intent(this.context, AllStationView::class.java)
            intent.putExtra("EXTRA_SESSION_ID", "SOMEVALUE FROM FAVOrite")
            startActivityForResult(intent, SecondActivityCode)
        }

        // Get the current time in the 24 hours format (ranging from 0 - 23)
        val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        progresslabel.text = getDateTimeString(currentTime-1)

        seekbar.progress = currentTime
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                if (i != 0) {
                    progresslabel.text = getDateTimeString(i-1)
                }
                else {
                    progresslabel.text = getDateTimeString(0)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO: Open a dialog that ask the user to confirm change?
                if (seekBar.progress == 0) {
                    forecasting(1)
                }
                else {
                    forecasting(seekBar.progress)
                }
                Toast.makeText(mContext, "Endret informasjon til stasjonene til tidspunktet: ${progresslabel.text}", Toast.LENGTH_SHORT).show()

            }
        })

        restore_button.setOnClickListener {
            Toast.makeText(mContext, "Tilbakestilt informasjon til statsjonnene til nåtid: ${currentTime}", Toast.LENGTH_SHORT).show()
            // Tilbakestiller dataene til nåtid
            forecasting(currentTime-1)
            progresslabel.text = getDateTimeString(currentTime-1)
            seekbar.progress = currentTime
        }
    }

    private fun getDateTimeString(currentTime : Int) : String{
        val datetime = MainActivity.staticAirQualityStationsList[0].data.time[currentTime].from.split("T")
        val date = datetime[0]
        val hour = datetime[1].take(5)
        return date + " - Kl:" + hour


    }
    // TODO: Denne metoden endrer alle nødvendig informasjon om en stasjon på den valgte tiden
    private fun forecasting(time : Int) {
        // TODO: Loop through dataset and change info for each station to the specified time?
        val newDateset = ArrayList<CityElement>()
        for (station in dataset) {
            val tempStation = getStation(station.location.name)
            if (tempStation != null) {
                newDateset.add(CityElement(tempStation, time))
            }
        }
        dataset = newDateset
        initRecycleView(dataset)
    }
    // Handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val location = intent.getStringExtra("location")
            if (checkFavouriteCity(location)) { // if the station already exists in favourite we return
                return
            }
            // TODO: Loop through the list of station and get the info that is needed.
            for (station in MainActivity.staticAirQualityStationsList) {
                if (station.meta.location.name == location) {
                    val aqi = station.data.time[0].variables.AQI.value
                    val description = getAQILevelString(aqi) // get the aqi level in string
                    Log.e("Allstation View", "Received Message from cityadapter ${location} - ${description}")
                    addFavoriteElement(location)

                }
            }

            Log.e("Allstation View", "Received Message from cityadapter ${location}")
            addFavoriteElement(location)
        }
    }


    // Method that initinalize the recycleView
    private fun initRecycleView(dataset: ArrayList<CityElement>) {

        viewManager = LinearLayoutManager(mContext)

        // TODO: Finne ut hvorfor context er null her naar man trykker paa legg til fra mapstationholder
        viewAdapter = CityListAdapter(dataset, mContext)


        recyclerView = fView.findViewById<RecyclerView>(R.id.recyclerView).apply {

            layoutManager = viewManager

            adapter = viewAdapter
        }


        // make a swipe controller object to enable swipe card.
        var swipeController = object : SwipeController() {
            override fun deleteItem(pos : Int) {
                deleteItemAt(pos)
            }

            override fun moveItem(oldPos: Int, newPos: Int) {
                Log.e("FacvouriteCity", "Moving")
                moveItem(oldPos, newPos)
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerView)

    }

     /* Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name, ID and Address).
     */
    // Metode som henter result message from AllstationView
    override fun onActivityResult(requestCode : Int , resultCode: Int, data : Intent) {
        if (requestCode == SecondActivityCode) {
            when (resultCode) {
            RESULT_OK -> {
                val returnedLocation = data.getStringExtra("Stationlocation")
                val returnedDescription= data.getStringExtra("DescriptionStation")
                if (checkFavouriteCity(returnedLocation)) {
                    Toast.makeText(mContext, "Stasjonen finnes allerede i favoritter!", Toast.LENGTH_SHORT).show()
                    return
                }


                addFavoriteElement(returnedLocation)
            } RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.e(TAG, status.statusMessage)
            } RESULT_CANCELED -> {
                Log.e("CANCELED","CANCELED")
                // The user canceled the operation.
            }
            }
        }
    }

    // Method to add new favourite location to view.
    private fun addFavoriteElement(location: String) {
        if (checkFavouriteCity(location)) { // TODO: Change content to work for the newer cityElement location. DONE?
            return
        }
        val bits = location.split(",").toTypedArray()
        val formatedLoc = bits[0]

        for (data in MainActivity.staticAirQualityStationsList) {
            val locationName = data.meta.location.name
            if (locationName.equals(formatedLoc)) {
                dataset.add(CityElement(data, Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
            }
        }

        initRecycleView(dataset)

        viewAdapter.notifyDataSetChanged()

        // lagrer det nye arrayet!
        saveFavoriteElement()

        Toast.makeText(mContext, "Lagt til ${location} i favoritter!", Toast.LENGTH_LONG).show()

    }

    /**
     * Kalles direkte paa av addFavoriteElement().
     * Metoden lagrer listen med favoritter til
     */

    private fun saveFavoriteElement() {
        val sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        val gson = Gson()
        val json : String = gson.toJson(dataset)
        editor?.putString(key, json)
        editor?.apply()
    }

    /**
     * Metoden skal kalles naar main vinduet loades.
     */
    private fun loadFavoriteElement() {
        val sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        val gson = Gson()

        val json : String? = sharedPreferences?.getString(key, null)

        if (json == null) {
            return
        } else {
            dataset = gson.fromJson(json)
        }
    }
    private fun checkFavouriteCity(location: String) : Boolean {
        for (data in dataset) {
            if (data.location.name.contentEquals(location)) {
                return true
            }
        }
        return false
    }

    private fun getStation(location : String) : AirQualityStation?{
        for (station in MainActivity.staticAirQualityStationsList) {
            if (station.meta.location.name.equals(location)) {
                return station
            }
        }
        return null
    }

    private fun geolocate(text : Editable ) : Boolean {
        val searchString = text.toString()
        Log.d(TAG, "geolocate: geolocating")
        val geocoder  = Geocoder(this.context)
        var list = ArrayList<Address>().toList()

        try {
            list = geocoder.getFromLocationName(searchString,1)
        }catch (e : IOException) {
            Log.e("geolate:", "IOException" + e.message)
        }


        if (list.size > 0) {
            val address = list.get(0)
            Log.e("Result address:", address.toString())

//            Toast.makeText(this.context, address.toString(), Toast.LENGTH_LONG).show()
        }

        return false
    }

    // Fetch a spesifct place by id
    fun fetchPlaces() {

        // Define a Place ID.
        var placeId = "INSERT_PLACE_ID_HERE"

        // Specify the fields to return.
        val placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        // Add a listener to handle the response.

        placesClient.fetchPlace(request).addOnSuccessListener {
                response ->
            val place = response.place
            Log.i(TAG, "Place found: " + place.name)
        }.addOnFailureListener {
                exception ->
            Log.e(TAG, "Place not found: " + exception.message)
        }
    }

    fun placeAutocomplete() {
        val token = AutocompleteSessionToken.newInstance()

        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(-33.880490, 151.184363),
            LatLng(-33.858754, 151.229596));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().
            .setLocationBias(bounds)
            //.setLocationRestriction(bounds)
            .setCountry("au")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery("query")
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener {
                response ->
            for (prediction in response.autocompletePredictions) {
                Log.i(TAG, prediction.placeId)
                Log.i(TAG, prediction.getPrimaryText(null).toString())
            }
        }.addOnFailureListener {
                exception ->
            val apiException = ApiException(Status(1))
            Log.e(TAG, "Place not found: " + apiException.statusCode)
        }
    }


    fun moveItem(oldPos: Int, newPos: Int) {
        val fooditem = dataset.get(oldPos)
        dataset.removeAt(oldPos)
        dataset.add(newPos, fooditem)
        viewAdapter.notifyItemMoved(oldPos, newPos)
    }


    fun deleteItemAt(pos: Int) {
        val item = dataset[pos]
        dataset.removeAt(pos)
        viewAdapter.notifyItemRemoved(pos)
        viewAdapter.notifyItemRangeChanged(pos,dataset.size)

        // oppdatere
        saveFavoriteElement()

        Toast.makeText(this.context,"Removed ${item.title}",Toast.LENGTH_SHORT).show()

    }

    fun hideSoftKeyboard() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
//        if (activity?.currentFocus != null) {
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(edit.windowToken, 0)
//        }
    }

    fun getNearestStation(){
        val fused = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        val tmpPos = Location(LocationManager.GPS_PROVIDER)
        val myPos = Location(LocationManager.GPS_PROVIDER)

        var tmpStation : AirQualityStation = MainActivity.staticAirQualityStationsList[0]
        var dist : Float = 1000000.00f
        var tmpDist : Float


        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fused.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null){
                    myPos.latitude = location.latitude
                    myPos.longitude = location.longitude
//                    Log.e("not null ---- " , location.longitude.toString())
//                    Log.e("not null ---- " , location.latitude.toString())

                }
                else{
//                    Log.e("Location = " , " Null---")

                }



                for (station in MainActivity.staticAirQualityStationsList){
                    //Det er feil i api'et, så må bytte på lat og long
                    tmpPos.latitude = station.meta.location.longitude.toDouble()
                    tmpPos.longitude = station.meta.location.latitude.toDouble()

                    tmpDist = tmpPos.distanceTo(myPos)
                    if (dist > tmpDist){
                        tmpStation = station
                        dist = tmpDist
//                        Log.e("Distance in float:" , dist.toString())
                    }
                }
                val value = tmpStation.data.time[0].variables.AQI.value
                addFavoriteElement(tmpStation.meta.location.name)
            }

        }
    }
}
