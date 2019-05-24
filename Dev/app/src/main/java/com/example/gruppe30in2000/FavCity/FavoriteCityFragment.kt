package com.example.gruppe30in2000.FavCity

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.ContentValues.TAG
import android.content.RestrictionsManager.RESULT_ERROR
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.*
import com.example.gruppe30in2000.API.AirQualityStation
import com.example.gruppe30in2000.API.AirQualityStationCollection
import com.example.gruppe30in2000.MainActivity
import com.example.gruppe30in2000.R
import com.fatboyindustrial.gsonjodatime.Converters
import com.github.salomonbrys.kotson.fromJson
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.widget.Autocomplete
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList


class FavoriteCityFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var fView: View
    private lateinit var mContext: Context

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

        // Dersom den nærmeste stasjonen ikke er lagt til, setter vi verdien til true og legge til den næremeste.
        if (!addNearestStation) {
            addNearestStation = true
            // If list of all station is not empty then we get and add the nearest station available
            if (MainActivity.staticAirQualityStationsList.isNotEmpty()) {
                getNearestStation()
            }
        }

        return fView
    }


    private fun save() {
        // save data in shared prefs
        val sharedPreferences = mContext.getSharedPreferences(MainActivity.preference, Context.MODE_PRIVATE)
        val gson = Gson()
        val dateGson = Converters.registerDateTime(GsonBuilder()).create()
        val editor = sharedPreferences?.edit()

        val stationsJson = gson.toJson(MainActivity.staticAirQualityStationsList)
        val lastCheckJson = dateGson.toJson(DateTime())

        editor?.putString(MainActivity.stations, stationsJson)
        editor?.putString(MainActivity.lastCheck, lastCheckJson)
        editor?.apply()

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val floatingButton = fView.findViewById<FloatingActionButton>(R.id.floating_button)
        LocalBroadcastManager.getInstance(mContext)
            .registerReceiver(mMessageReceiver, IntentFilter("from-mapstationhandler"))
        val seekbar = view.findViewById<SeekBar>(R.id.seekbar)
        val progresslabel = view.findViewById<TextView>(R.id.progress_text)
        val restore_button = view.findViewById<ImageButton>(R.id.restore_button)


        loadFavoriteElement()

        initRecycleView(dataset)

        // Starter AllstationView on click.
        floatingButton.setOnClickListener {
            val intent = Intent(this.context, AllStationView::class.java)
            intent.putExtra("EXTRA_SESSION_ID", "SOMEVALUE FROM FAVOrite")
            startActivityForResult(intent, SecondActivityCode)
        }



        val refreshButton = fView.findViewById<ImageButton>(R.id.refresh_button)

        refreshButton.setOnClickListener {

            Log.e("After set", dataset.toString())
            GlobalScope.launch{
                if (this@FavoriteCityFragment.update()) {
                    activity?.runOnUiThread {
                        run {
                            Log.e("After set", dataset.toString())
                            viewAdapter.notifyDataSetChanged()
                            // Tilbakestiller dataene til nåtid
                            val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                            val date = Calendar.getInstance().get(Calendar.DATE)

                            val timeIndex = getTimeIndex(time, date)
                            forecasting(timeIndex)
                            seekbar.progress = timeIndex

                            Toast.makeText(mContext, "Målingene er oppdatert!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



        // Get the current time in the 24 hours format (ranging from 0 - 23)
        val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val date = Calendar.getInstance().get(Calendar.DATE)

        val timeIndex = getTimeIndex(time, date)



        progresslabel.text = getDateTimeString(timeIndex)


        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                if (i != 0) {
                    progresslabel.text = getDateTimeString(i)
                } else {
                    progresslabel.text = getDateTimeString(0)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO: Open a dialog that ask the user to confirm change?
                if (seekBar.progress == 0) {
                    forecasting(1)
                } else {
                    forecasting(seekBar.progress)
                }
                Toast.makeText(
                    mContext,
                    "Endret informasjon til stasjonene til tidspunktet: ${progresslabel.text}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        restore_button.setOnClickListener {
            Toast.makeText(mContext, "Tilbakestilt data til nåtid", Toast.LENGTH_SHORT).show()
            // Tilbakestiller dataene til nåtid
            forecasting(timeIndex)
            seekbar.progress = timeIndex
        }

        forecasting(timeIndex)
        seekbar.progress = timeIndex
    }


    private fun update() : Boolean {
        val stations : ArrayList<AirQualityStation>

        try {
            val stationsGetter = AirQualityStationCollection()
            stations = stationsGetter.airQualityStationList
        } catch (e : Exception) {
            activity?.runOnUiThread {
                run {
                    Toast.makeText(mContext, "Kunne ikke oppdatere data, sjekk netverkstilkoblingen din.", Toast.LENGTH_LONG).show()
                    this@FavoriteCityFragment.viewAdapter.notifyDataSetChanged()
                }
            }
            return false
        }

        if(stations.isEmpty()){
            activity?.runOnUiThread {
                run {
                    Toast.makeText(mContext, "Kunne ikke oppdatere data. APIet er nede", Toast.LENGTH_LONG).show()
                    this@FavoriteCityFragment.viewAdapter.notifyDataSetChanged()
                }
            }
            return false

        } else {
            // successful get request
            fun isInset(eoi: String): Boolean {
                for (element in dataset) {
                    if (eoi == element.eoi)
                        return true
                }

                return false
            }

            MainActivity.staticAirQualityStationsList = stations

            val aqStationFavourites = stations.filter {s -> isInset(s.meta.location.areacode)}

            val newDataSet = ArrayList<CityElement>()

            loop@ for (element in dataset) {
                for (station in aqStationFavourites) {
                    if (element.eoi == station.meta.location.areacode){
                        val cityElement = CityElement(station, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                        newDataSet.add(cityElement)
                        continue@loop
                    }
                }
            }

            // clear dataset, and add again
            dataset.clear()

            for (cityElement in newDataSet) {
                dataset.add(cityElement)
            }


            GlobalScope.launch {
                save()
            }

            return true

        }

    }

    // Returnerer stasjonen sin tid i string format.
    private fun getDateTimeString(currentTime: Int): String {
        if (MainActivity.staticAirQualityStationsList.isNotEmpty()) {
            val datetime = MainActivity.staticAirQualityStationsList[0].data.time[currentTime].from.split("T")
            val date = datetime[0]
            val hour = datetime[1].take(5)
            return date + " - Kl:" + hour
        }
        return "No data"

    }

    // Denne metoden endrer alle nødvendig informasjon om en stasjon til den sendte tiden
    private fun forecasting(time: Int) {
        val newDateset = ArrayList<CityElement>()
        // Looper gjennom alle favoritt stasjonene og legger til stasjonen inni en liste med valgt tidspunkt.
        for (station in dataset) {
            val tempStation = getStation(station.location.name)
            if (tempStation != null) {
                newDateset.add(CityElement(tempStation, time))
            }
        }
        dataset = newDateset
        initRecycleView(dataset)
    }



    // En handler som håndterer de innhentede Intents. Denne metoden onReceieve blir kalt hver gang en Intent
    // med en action navn "custom-event-name" blir broadcasted.
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Henter lokasjonen stringen som ligger i intent.
            val location = intent.getStringExtra("location")
            if (checkFavouriteCity(location)) { // Hvis stasjonen allerede finnes i listen trenger ikke vi å gå videre.
                return
            }

            addFavoriteElement(location)
        }
    }


    // Initialiserer Recycle view, viser alle tilgjengelig stasjonene.
    private fun initRecycleView(dataset: ArrayList<CityElement>) {

        viewManager = LinearLayoutManager(mContext)

        // TODO: Finne ut hvorfor context er null her naar man trykker paa legg til fra mapstationholder
        viewAdapter = CityListAdapter(dataset, mContext, context)


        recyclerView = fView.findViewById<RecyclerView>(R.id.recyclerView).apply {

            layoutManager = viewManager

            adapter = viewAdapter
        }


        // make a swipe controller object to enable swipe card.
        var swipeController = object : SwipeController() {
            override fun deleteItem(pos: Int) {
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == SecondActivityCode) {
            when (resultCode) {
                RESULT_OK -> {
                    val returnedLocation = data.getStringExtra("Stationlocation")
                    if (checkFavouriteCity(returnedLocation)) {
                        Toast.makeText(mContext, "Stasjonen finnes allerede i favoritter!", Toast.LENGTH_SHORT).show()
                        return
                    }


                    addFavoriteElement(returnedLocation)
                }
                RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.e(TAG, status.statusMessage)
                }
                RESULT_CANCELED -> {
                    Log.e("CANCELED", "CANCELED")
                    // The user canceled the operation.
                }
            }
        }
    }

    // Metode som legger til en ny cardview inni recycleviewet
    private fun addFavoriteElement(location: String) {
        val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val date = Calendar.getInstance().get(Calendar.DATE)
        val timeIndex = getTimeIndex(time, date)
        if (checkFavouriteCity(location)) { // TODO: Change content to work for the newer cityElement location. DONE?
            return
        }
        val bits = location.split(",").toTypedArray()
        val formatedLoc = bits[0]

        for (data in MainActivity.staticAirQualityStationsList) {
            val locationName = data.meta.location.name
            if (locationName.equals(formatedLoc)) {
                dataset.add(CityElement(data, timeIndex))
                break
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
        val json: String = gson.toJson(dataset)
        editor?.putString(key, json)
        editor?.apply()
    }

    /**
     * Metoden skal kalles naar main vinduet loades.
     */
    private fun loadFavoriteElement() {
        val sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        val gson = Gson()

        val json: String? = sharedPreferences?.getString(key, null)

        if (json == null) {
            return
        } else {
            // Get the current time in the 24 hours format (ranging from 0 - 23)
            val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            dataset = gson.fromJson(json)
            forecasting(currentTime - 1)
        }
    }

    // privat metode som går gjennom alle favorittstasjonene og sjekke om location allerede finnes.
    private fun checkFavouriteCity(location: String): Boolean {
        for (data in dataset) {
            if (location.contains(data.location.name)) {
                return true
            }
        }
        return false
    }

    private fun getStation(location: String): AirQualityStation? {
        for (station in MainActivity.staticAirQualityStationsList) {
            if (station.meta.location.name.equals(location)) {
                return station
            }
        }
        return null
    }

    // sletter en stasjon/cardview ved swipe
    fun deleteItemAt(pos: Int) {
        val item = dataset[pos]
        dataset.removeAt(pos)
        viewAdapter.notifyItemRemoved(pos)
        viewAdapter.notifyItemRangeChanged(pos, dataset.size)

        // oppdatere
        saveFavoriteElement()

        Toast.makeText(this.context, "Fjernet ${item.title}", Toast.LENGTH_SHORT).show()

    }

    // Metode for å finne og legge til den nærmeste stasjonen.
    fun getNearestStation() {
        val fused = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        val tmpPos = Location(LocationManager.GPS_PROVIDER)
        val myPos = Location(LocationManager.GPS_PROVIDER)

        var tmpStation: AirQualityStation = MainActivity.staticAirQualityStationsList[0]
        var dist: Float = 1000000.00f
        var tmpDist: Float


        if (ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fused.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myPos.latitude = location.latitude
                    myPos.longitude = location.longitude

                }

                for (station in MainActivity.staticAirQualityStationsList) {
                    //Det er feil i api'et, så må bytte på lat og long
                    tmpPos.latitude = station.meta.location.longitude.toDouble()
                    tmpPos.longitude = station.meta.location.latitude.toDouble()

                    tmpDist = tmpPos.distanceTo(myPos)
                    if (dist > tmpDist) {
                        tmpStation = station
                        dist = tmpDist
                    }
                }
                addNearestStation(tmpStation.meta.location.name)
            }

        }
    }

    // Legge til den nærmeste stasjonen inni recycleview.
    private fun addNearestStation(location: String) {
        if (checkFavouriteCity(location)) {
            return
        }
        val bits = location.split(",").toTypedArray()
        val formatedLoc = bits[0]

        for (data in MainActivity.staticAirQualityStationsList) {
            val locationName = data.meta.location.name
            if (locationName.equals(formatedLoc)) {
                val element = CityElement(data, getTimeIndex(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.DATE)))
                dataset.add(0, element)
            }
        }

        initRecycleView(dataset)

        viewAdapter.notifyDataSetChanged()

        // lagrer det nye arrayet!
        saveFavoriteElement()

        Toast.makeText(mContext, "Lagt til ${location} i favoritter!", Toast.LENGTH_LONG).show()

    }

    fun getTimeIndex(time: Int, date: Int): Int {
        if (MainActivity.staticAirQualityStationsList.isEmpty()) {
            return 0
        }

        var c = 0
        MainActivity.staticAirQualityStationsList[0].data.time.forEach {
            val datetime = it.from.split("T")
            var d = datetime[0].takeLast(2).toInt()
            var t = datetime[1].take(2).toInt()

            if (date == d && time == t) {
                return c
            }
            c++
        }
        return 0
    }
}
