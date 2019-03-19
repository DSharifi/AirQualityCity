package com.example.gruppe30in2000

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
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
import android.provider.MediaStore.Audio.Playlists.Members.moveItem
import android.util.Log


class FavoriteCity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val sharedPREF = "sharedPrefs"
    private val dataSET= "dataset"

    private var dataset = ArrayList<CityElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_city)

        val floatingButton = findViewById<FloatingActionButton>(R.id.floating_button)

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



        //Toast.makeText(this, "CURRENT DATA SIZE: ${dataset.size}", Toast.LENGTH_LONG).show()



        floatingButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this) // make a dialog builder
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder
            val alertDialog = dialogBuilder.create()
            alertDialog.show()



            val addButton = dialogView.findViewById<Button>(R.id.add_button)
            val edit_title = dialogView.findViewById<EditText>(R.id.edit_title)
            val edit_description = dialogView.findViewById<EditText>(R.id.edit_description)


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


            addButton.setOnClickListener {
                val tempElement = CityElement(edit_title.text, edit_description.text)
                dataset.add(tempElement)
                Toast.makeText(this, "Element added!", Toast.LENGTH_SHORT).show()

                initRecycleView(dataset)
                alertDialog.hide()

                Toast.makeText(this, "Dataset Length: ${dataset.size}", Toast.LENGTH_LONG).show()
                //saveData()
            }
        }
    }


    // Method the initinalize the recycleView
    private fun initRecycleView(dataset: ArrayList<CityElement>) {
        viewManager = LinearLayoutManager(this)

        viewAdapter = CityListAdapter(dataset, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {

            layoutManager = viewManager

            adapter = viewAdapter
        }

//        val swipeController = SwipeController()
//        val itemTouchhelper = ItemTouchHelper(swipeController)
//        itemTouchhelper.attachToRecyclerView(recyclerView)


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.e("onSwiped:", "Swiping")

//                deleteItem(viewHolder.adapterPosition)

            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun moveItem(oldPos: Int, newPos: Int) {
        val fooditem = dataset.get(oldPos)
        dataset.removeAt(oldPos)
        dataset.add(newPos, fooditem)
        viewAdapter.notifyItemMoved(oldPos, newPos)
    }


    fun deleteItem(pos: Int) {
        val item = dataset[pos]
        dataset.removeAt(pos)
        viewAdapter.notifyItemRemoved(pos)
        Toast.makeText(this,"Removed ${item.title}",Toast.LENGTH_SHORT).show()

    }
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
