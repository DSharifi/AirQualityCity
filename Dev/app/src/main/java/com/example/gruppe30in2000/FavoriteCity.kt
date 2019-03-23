package com.example.gruppe30in2000

import android.content.ContentValues.TAG
import android.location.Address
import android.location.Geocoder
import android.nfc.Tag
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.ArrayList
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import java.io.IOException


class FavoriteCity : Fragment() {
    data class Station(
        // identifier, expression of interest
        val eoi: String
    )


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fView: View
    private val sharedPREF = "sharedPrefs"
    private val dataSET= "dataset"

    private var dataset = ArrayList<CityElement>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fView = inflater.inflate(R.layout.fragment_favorite_city, container, false)
        return fView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        fView
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.fragment_favorite_city)

        val floatingButton = fView.findViewById<FloatingActionButton>(R.id.floating_button)

        ////// MAKE 2 element to current
        val title1 = SpannableStringBuilder("Oslo") as Editable
        val description1 = SpannableStringBuilder("Lav") as Editable

        val title2 = SpannableStringBuilder("Trondheim") as Editable
        val description2 = SpannableStringBuilder("Moderat") as Editable

        val element = CityElement(title1, description1)
        val element2 = CityElement(title2, description2)
        dataset.add(element)
        dataset.add(element2)
//        loadData()
        initRecycleView(dataset)

//        println(getAirStations()[3].data.time[0].variables.AQI.value)



        //Toast.makeText(this, "CURRENT DATA SIZE: ${dataset.size}", Toast.LENGTH_LONG).show()


        floatingButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this.context!!) // make a dialog builder
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder
            val alertDialog = dialogBuilder.create()
            alertDialog.show()



            val addButton = dialogView.findViewById<Button>(R.id.add_button)
            val edit_title = dialogView.findViewById<EditText>(R.id.edit_title)
            val edit_description = dialogView.findViewById<EditText>(R.id.edit_description)
            val search_text = dialogView.findViewById<EditText>(R.id.search_input)

            // make a common textWatcher to use for several editText listener
            val textWatcher = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val titleInput = edit_title.text
                    val descriptionInput = edit_description.text
                    addButton.isEnabled = (!titleInput.isEmpty() && !descriptionInput.isEmpty())
                }
            }

            edit_title.addTextChangedListener(textWatcher)
            edit_description.addTextChangedListener(textWatcher)
            search_text.setOnEditorActionListener { v, actionId, event ->
                    if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.action == KeyEvent.ACTION_DOWN
                        || event.action == KeyEvent.KEYCODE_ENTER)
                    {

                        Log.e("Searching", "Serafdashi")
                        geolocate(search_text.text)


                    }
false // return false if no change/edits were made

            }


            addButton.setOnClickListener {
                val tempElement = CityElement(edit_title.text, edit_description.text)
                dataset.add(tempElement)
                Toast.makeText(this.context, "Element added!", Toast.LENGTH_SHORT).show()

                initRecycleView(dataset)
                alertDialog.hide()

                Toast.makeText(this.context, "Dataset Length: ${dataset.size}", Toast.LENGTH_LONG).show()
                //saveData()
            }
        }
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
    // Method the initinalize the recycleView
    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this.context)

        viewAdapter = CityListAdapter(dataset, this.context!!)

        recyclerView = fView.findViewById<RecyclerView>(R.id.recyclerView).apply {

            layoutManager = viewManager

            adapter = viewAdapter
        }



        var swipeController = object : SwipeController() {
            override fun deleteItem(pos : Int) {
                deleteItemAt(pos)
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerView)




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
        Toast.makeText(this.context,"Removed ${item.title}",Toast.LENGTH_SHORT).show()

    }

//
//    // Henter livedata for alle målestasjoner
//    fun getAirStations() : ArrayList<AirQualityStation> {
//        val stationList = ArrayList<AirQualityStation>()
//        val stations = getStations()
//
//        val gson = Gson()
//
//        for (station in stations) {
//            // Hent stasjonmaaling
//            val airQualityResponse = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/?station=${station.eoi}")
//            stationList.add(gson.fromJson(airQualityResponse.text))
//        }
//
//        return stationList
//    }
//
//    fun getAqi(station:Int, time:Int) : Double{
//        return getAirStations()[station].data.time[time].variables.AQI.value
//    }
//
//    // Returnerer en liste med alle stasjoner
//    fun getStations() : ArrayList<Station> {
//        val response = khttp.get("https://in2000-apiproxy.ifi.uio.no/weatherapi/airqualityforecast/0.1/stations")
//        return Gson().fromJson(response.text)
//    }



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

