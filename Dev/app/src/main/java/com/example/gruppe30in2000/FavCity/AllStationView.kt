package com.example.gruppe30in2000.FavCity

import android.app.Activity
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.gruppe30in2000.AQILevel
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import java.util.*


class AllStationView : AppCompatActivity() {

    // TODO Fix risk_display overlapping with location in GUI
    // TODO Fix swipecontroller display problem.

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val airquailityStation = MainActivity.staticAirQualityStationsList
    private var dataset = ArrayList<CityElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.gruppe30in2000.R.layout.activity_all_station_view)


        // RECIEVE DATA FROM ADAPTER with custom message: from-cityadapter
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("from-cityadapter"))

        for (data in airquailityStation) {
            val location= data.meta.location
            val superLocation = data.meta.superlocation
            val aqiValue = data.data.time[0].variables.AQI.value

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


            val aqiLevel = AQILevel.getAQILevel(aqiValue)

            when (aqiLevel) {
                1 ->{
                    dataset.add(CityElement(location.name + ", " + superLocation.name, "Lav", AQI_o3String, AQI_o3Value, nitVal, nitUnit, pm10val,
                        pm10uni, nitHeating, nitIndustry, nitShipping, nitTrafficExhaust, pm10Heating, pm10LocalFractionShipping,
                        pm10LocalFractionIndustry, pm10LocalFractionTrafficExhaust, pm10LocalFractionTrafficNonexhaust))
                } 2 ->{
                    dataset.add(CityElement(location.name + ", " + superLocation.name, "Moderat", AQI_o3String, AQI_o3Value, nitVal, nitUnit, pm10val,
                        pm10uni, nitHeating, nitIndustry, nitShipping, nitTrafficExhaust, pm10Heating, pm10LocalFractionShipping,
                        pm10LocalFractionIndustry, pm10LocalFractionTrafficExhaust, pm10LocalFractionTrafficNonexhaust))
                }else ->{
                    dataset.add(CityElement(location.name + ", " + superLocation.name, "Hoy", AQI_o3String, AQI_o3Value, nitVal, nitUnit, pm10val,
                        pm10uni, nitHeating, nitIndustry, nitShipping, nitTrafficExhaust, pm10Heating, pm10LocalFractionShipping,
                        pm10LocalFractionIndustry, pm10LocalFractionTrafficExhaust, pm10LocalFractionTrafficNonexhaust))
                }
            }
        }
        initRecycleView(dataset)

        // TODO implement add cardview and send back the selected cardview to FavoriteCity and display it in Favorites city View.
        val searchInput = findViewById<EditText>(R.id.search_input)

        //make a common textWatcher to use for several editText/TextView listener

        val textWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }


        searchInput.addTextChangedListener(textWatcher)
        val title = findViewById<TextView>(R.id.Stasjoner_title)
    }



    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this)

        viewAdapter = CityListAdapter(dataset, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllStation).apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
    private fun filter(text : String) {

        val filteredElements = ArrayList<CityElement>()
        for (item : CityElement in dataset) {
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                filteredElements.add(item)
            }
        }
        initRecycleView(filteredElements)
        viewAdapter.notifyDataSetChanged()
    }


    // Handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val location = intent.getStringExtra("location")
            val description = intent.getStringExtra("description")
            Log.e("Allstation View", "Received Message from cityadapter ${location} - ${description}")
            passBackDataToActivity(location, description)
        }
    }

    // Method to pass data from current activity to another activity
    // Add the data to send back with putExtra method and set the result.
    private fun passBackDataToActivity(location : String, description: String) {
        intent.putExtra("Stationlocation", location)
        intent.putExtra("DescriptionStation", description)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }


}
