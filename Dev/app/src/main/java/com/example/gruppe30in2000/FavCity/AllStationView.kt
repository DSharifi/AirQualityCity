package com.example.gruppe30in2000.FavCity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import java.util.ArrayList

class AllStationView : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val airquailityStation = MainActivity.staticAirQualityStationsList
    private var dataset = ArrayList<CityElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_station_view)
//        val value = intent.getStringExtra("EXTRA_SESSION_ID")
//        Log.e("ALLSTATION", value)
//        if (extras != null) {
//            val value = extras.getString("")
//            //The key argument here must match that used in the other activity
//        }

        for (data in airquailityStation) {
            val location = data.meta.location
            val aqiValue = data.data.time[0].variables.AQI.value
            if (aqiValue <= 1.6) {
                dataset.add(CityElement(location.name, "Lav"))

            } else if (aqiValue > 1.6 && aqiValue < 1.8) {
                dataset.add(CityElement(location.name, "Moderat"))
            } else {
                dataset.add(CityElement(location.name, "Hoy"))

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

        // Test sending back data to FavoriteCity.
        title.setOnClickListener {
            intent.putExtra("ValuefromAllStation", "HELLOFROM ALLSTATION")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }



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




}
