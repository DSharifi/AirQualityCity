package com.example.gruppe30in2000.FavCity

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.*
import com.example.gruppe30in2000.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
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

import java.io.IOException
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
    private val SecondActivityCode = 101

    private val sharedPREF = "sharedPrefs"
    private val dataSET= "dataset"

    companion object {
        var dataset = ArrayList<CityElement>()

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fView = inflater.inflate(R.layout.fragment_favorite_city, container, false)
        return fView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val floatingButton = fView.findViewById<FloatingActionButton>(R.id.floating_button)

        initRecycleView(dataset)
        floatingButton.setOnClickListener {
            val intent = Intent(this.context, AllStationView::class.java)
            intent.putExtra("EXTRA_SESSION_ID", "SOMEVALUE FROM FAVOrite")
            startActivityForResult(intent, SecondActivityCode)
        }
    }

    // Method the initinalize the recycleView
    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this.context)

        viewAdapter = CityListAdapter(dataset, this.context!!)

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
        Places.initialize(this.requireContext(), "AIzaSyCrfEIKJc8Nqz6dPV-Ju1jgCAb-BRek70g")

        // Create a new Places client instance.
        placesClient = Places.createClient(requireContext())



    }

    // Test method for places autocomplete
    fun onSearchInputEnter(context : Context) {

        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(context)

        startActivityForResult(intent, 1)

    }
     /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name, ID and Address).
     */
    override fun onActivityResult(requestCode : Int , resultCode: Int, data : Intent) {
        if (requestCode == SecondActivityCode) {
            when (resultCode) {
            RESULT_OK -> {
                val returnedLocation = data.getStringExtra("Stationlocation")
                val returnedDescription= data.getStringExtra("DescriptionStation")
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
        dataset.add(CityElement(location, description))
        initRecycleView(dataset)
        viewAdapter.notifyDataSetChanged()
        Toast.makeText(context, "Lagt til ${location} i favoritter!", Toast.LENGTH_LONG).show()
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
        Toast.makeText(this.context,"Removed ${item.title}",Toast.LENGTH_SHORT).show()

    }

    fun hideSoftKeyboard() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
//        if (activity?.currentFocus != null) {
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(edit.windowToken, 0)
//        }
    }

















    // TODO save and load data done by user
    /*
    private fun saveData() {
        val sharedPrefs = getSharedPreferences(sharedPREF, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(dataset)
        editor.putString(dataSET,json)
        editor.apply()
    }


    private fun loadData() {
        val sharedPrefs = getSharedPreferences(sharedPREF, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString(dataSET,"")

        if (json != null) {
            val data = gson.fromJson(json, arrayListOf<Element>().javaClass)
            Toast.makeText(this, "THAO ER FLOPP ${data.size}", Toast.LENGTH_LONG).show()



            TODO() // CRASH WHEN SET dataset variable to data
            // dataset = data
        }
        else {
            return
        }
    }*/
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
