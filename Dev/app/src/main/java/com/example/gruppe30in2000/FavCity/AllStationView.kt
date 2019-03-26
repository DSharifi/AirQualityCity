package com.example.gruppe30in2000.FavCity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

        ////// MAKE 2 element to current
        val title1 = "Oslo"
        val description1 = "Lav"

        val title2 = "Bergen"
        val description2 = "Moderat"

        val element = CityElement(title1, description1)
        val element2 = CityElement(title2, description2)
        dataset.add(element)
        dataset.add(element2)
//        loadData()
        initRecycleView(dataset)
    }

    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this)

        viewAdapter = CityListAdapter(dataset, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllStation).apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


}
