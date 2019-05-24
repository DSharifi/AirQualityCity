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
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import java.util.*


class AllStationView : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val airquailityStation = MainActivity.staticAirQualityStationsList
    private var dataset = ArrayList<CityElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSoftKeyboard()
        setContentView(R.layout.activity_all_station_view)

        // Henter data fra adapteren med en custom melding: "from-cityadapter"
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("from-cityadapter"))

        val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val date = Calendar.getInstance().get(Calendar.DATE)
        val timeIndex = getTimeIndex(time, date)

        for (data in airquailityStation) {
                dataset.add(CityElement(data, timeIndex))
            }

        initRecycleView(dataset)

        val searchInput = findViewById<EditText>(R.id.search_input)

        // Lager her en felles/generic textWatcher slik at flere editText/TextView kan legge til som listener.
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
    }


    // Initialiserer Recycle view, viser alle tilgjengelig stasjonene.
    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this)

        viewAdapter = CityListAdapter(dataset,this, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllStation).apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    // en privat metode som blir kalt på hver gang searchInput endrer seg (afterTextChanged())
    // denne metoden filterer bort alle stasjoner som ikke inneholder text inni navnet.
    // Legger kun de stasjone som inneholder tekststrengen i navnet og oppdaterer recycleviewet.
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


    // En handler som håndterer de innhentede Intents. Denne metoden onReceieve blir kalt hver gang en Intent
    // med en action navn "custom-event-name" blir broadcasted.
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val location = intent.getStringExtra("location")
            val description = intent.getStringExtra("description")
            Log.e("Allstation View", "Received Message from cityadapter ${location} - ${description}")
            passBackDataToActivity(location, description)
        }
    }

    // Denne metode sender data fra den nåværende activity til en annen activity.
    // Legger de to verdiene og sender tilbake ved bruk av putExstra og kalle på "setResult" for å sende intent.
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

    fun getTimeIndex(time : Int, date : Int) : Int{
        var c = 0
        MainActivity.staticAirQualityStationsList[0].data.time.forEach{
            val datetime = it.from.split("T")
            var d = datetime[0].takeLast(2).toInt()
            var t = datetime[1].take(2).toInt()

            if(date == d && time == t){
                return c
            }
            c++
        }
        return 0
    }


    fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
