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

import java.io.IOException
import java.lang.reflect.Type
import java.util.*


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
            Log.e("FDSFDS", "FDSFDS")
            addNearestStation = true
            getNearestStation()
        }

        return fView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val floatingButton = fView.findViewById<FloatingActionButton>(R.id.floating_button)
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, IntentFilter("from-mapstationhandler"))

        loadFavoriteElement()

        initRecycleView(dataset)


        floatingButton.setOnClickListener {
            val intent = Intent(this.context, AllStationView::class.java)
            intent.putExtra("EXTRA_SESSION_ID", "SOMEVALUE FROM FAVOrite")
            startActivityForResult(intent, SecondActivityCode)
        }
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
            val description = intent.getStringExtra("description")
            Log.e("Allstation View", "Received Message from cityadapter ${location} - ${description}")
            addFavoriteElement(location, description)
        }
    }

    // Method that initinalize the recycleView
    private fun initRecycleView(dataset: ArrayList<CityElement>) {

        viewManager = LinearLayoutManager(context)

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
        }
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerView)

        // Initialize places context and api
        Places.initialize(mContext, "AIzaSyCrfEIKJc8Nqz6dPV-Ju1jgCAb-BRek70g")

        // Create a new Places client instance.
        placesClient = Places.createClient(mContext)
    }

    /**
     * Override the activity's onActivityResult(), check the request code, and
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
                    addFavoriteElement(returnedLocation,returnedDescription)
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
    private fun addFavoriteElement(location: String, description: String) {
        if (checkFavouriteCity(location)) {
            return
        }
        val bits = location.split(",").toTypedArray()
        val formatedLoc = bits[0]

        Log.e("TESTE NAVN", "Dette er loc:" + formatedLoc)
        for (data in MainActivity.staticAirQualityStationsList) {
            val locationName = data.meta.location.name
            if (locationName.equals(formatedLoc)) {
                val calendar = Calendar.getInstance()
                val currentHour = calendar .get(Calendar.HOUR_OF_DAY)
                val AQI_o3String = data.data.time[currentHour-1].variables.o3_concentration.units
                val AQI_o3Value = data.data.time[currentHour-1].variables.o3_concentration.value

                val nitVal = data.data.time[currentHour-1].variables.no2_concentration.value
                val nitUnit = data.data.time[currentHour-1].variables.no2_concentration.units
                val nitHeating = data.data.time[currentHour-1].variables.no2_local_fraction_heating.value
                val nitShipping = data.data.time[currentHour-1].variables.no2_local_fraction_shipping.value
                val nitIndustry = data.data.time[currentHour-1].variables.no2_local_fraction_industry.value
                val nitTrafficExhaust = data.data.time[currentHour-1].variables.no2_local_fraction_traffic_exhaust.value

                val pm10val = data.data.time[currentHour-1].variables.pm10_concentration.value
                val pm10uni = data.data.time[currentHour-1].variables.pm10_concentration.units
                val pm10Heating = data.data.time[currentHour-1].variables.pm10_local_fraction_heating.value
                val pm10LocalFractionShipping = data.data.time[currentHour-1].variables.pm10_local_fraction_shipping.value
                val pm10LocalFractionIndustry = data.data.time[currentHour-1].variables.pm10_local_fraction_industry.value
                val pm10LocalFractionTrafficExhaust = data.data.time[currentHour-1].variables.pm10_local_fraction_traffic_exhaust.value
                val pm10LocalFractionTrafficNonexhaust = data.data.time[currentHour-1].variables.pm10_local_fraction_traffic_nonexhaust.value

                dataset.add(CityElement(location, description, AQI_o3String, AQI_o3Value, nitVal, nitUnit, pm10val,
                    pm10uni, nitHeating, nitIndustry, nitShipping, nitTrafficExhaust, pm10Heating, pm10LocalFractionShipping,
                    pm10LocalFractionIndustry, pm10LocalFractionTrafficExhaust, pm10LocalFractionTrafficNonexhaust))
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
            if (data.title.equals(location)) {
                return true
            }
        }
        return false
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
                    Log.e("not null ---- " , location.longitude.toString())
                    Log.e("not null ---- " , location.latitude.toString())

                }
                else{
                    Log.e("Location = " , " Null---")

                }

                Log.e("- mypos lat: " , myPos.latitude.toString())
                Log.e("- mypos long: " , myPos.longitude.toString())


                for (station in MainActivity.staticAirQualityStationsList){
                    //Det er feil i api'et, så må bytte på lat og long
                    tmpPos.latitude = station.meta.location.longitude.toDouble()
                    tmpPos.longitude = station.meta.location.latitude.toDouble()

                    tmpDist = tmpPos.distanceTo(myPos)
                    if (dist > tmpDist){
                        tmpStation = station
                        dist = tmpDist
                        Log.e("Distance in float:" , dist.toString())
                    }
                }
                val value = tmpStation.data.time[0].variables.AQI.value
                addFavoriteElement(tmpStation.meta.location.name, getAQILevelString(value))
            }

        }
    }
}


// TODO SEARCH TEXT AUTOCOMPLETE
//
//            search_text.setOnEditorActionListener { v, actionId, event ->
//                    if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || event.action == KeyEvent.ACTION_DOWN
//                        || event.action == KeyEvent.KEYCODE_ENTER)
//                    {
//                        onSearchInputEnter(search_text.context, alertDialog) // call on google place search.
//                        hideSoftKeyboard()
//                    }
//false // return false if no change/edits were made
//
//            }

// TODO PREVIOUS CONTENT OF FLOATBUTTON.setonclick..

/*
      val dialogBuilder = AlertDialog.Builder(this.context!!) // make a dialog builder
      val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null) // get the dialog xml view
      dialogBuilder.setView(dialogView) // set the view into the builder
      val alertDialog = dialogBuilder.create()
      alertDialog.show()
      val addButton = dialogView.findViewById<Button>(R.id.add_button)
      val edit_title = dialogView.findViewById<TextView>(R.id.edit_title)
      val edit_description = dialogView.findViewById<TextView>(R.id.edit_description)
      val searchText = dialogView.findViewById<RelativeLayout>(R.id.search_input)
//             make a common textWatcher to use for several editText/TextView listener
      val textWatcher = object: TextWatcher {
          override fun afterTextChanged(s: Editable?) {
          }
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
          }
          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              val titleInput = edit_title.text
              val descriptionInput = edit_description.text
              addButton.isEnabled = (!titleInput.isEmpty())
          }
      }
      edit_title.addTextChangedListener(textWatcher)
      edit_description.addTextChangedListener(textWatcher)
      searchText.setOnClickListener {
          onSearchInputEnter(searchText.context) // call on google place search.
          Log.e("CURRENT PLACE", currentPlace)
          if (currentPlace.isNotEmpty()) {
              Log.e("IS NOT NYULL OR BLACK", "FDSAFSS")
              edit_title.text = currentPlace
              currentPlace = "" // reset value
          }
      }
      addButton.setOnClickListener {
          val tempElement = CityElement(edit_title.text.toString(), edit_description.text.toString())
          dataset.add(tempElement)
          Toast.makeText(this.context, "Element added!", Toast.LENGTH_SHORT).show()
          initRecycleView(dataset)
          alertDialog.hide()
          Toast.makeText(this.context, "Dataset Length: ${dataset.size}", Toast.LENGTH_LONG).show()
          //saveData()
      }*/


// Initialize the AutocompleteSupportFragment.
//        val autocompleteFragment =  fragmentManager?.findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment
//
//        autocompleteFragment?.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))

//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//
//            override fun onPlaceSelected(place : Place) {
//                Log.i(TAG, "Place: " + place.name + ", " + place.id)
//            }
//
//            override fun onError(status : Status) {
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })


