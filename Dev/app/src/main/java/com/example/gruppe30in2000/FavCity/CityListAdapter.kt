package com.example.gruppe30in2000.FavCity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import android.support.v4.content.LocalBroadcastManager
import com.example.gruppe30in2000.R


class CityListAdapter (private var dataSet: ArrayList<CityElement>, context: Context) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {
    val context = context



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val textView = LayoutInflater.from(parent.context).inflate(R.layout.city_element, parent, false) as View
        return ViewHolder(textView)

    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
//        Log.e("VIEWHOLDER: ", "onBindViewHolder: called")

        // set the element (cardview) text and description text base on the current position of the dataSet list.
        holder.title.text = dataSet[pos].title
        holder.description.text = dataSet[pos].description

        // VALIDATE the risk type of newly added city
        validateRiskType(holder.description.text.toString(), holder)


        if (context is AllStationView) { // If the context that call on this adapter is All stationView, make the addbutton visible.
            holder.addButton.visibility = View.VISIBLE
        }


        // On addButton clicked. Get the current card information and send back to AllstationView. with the custom: custom-message
        holder.addButton.setOnClickListener {
            val location = holder.title.text.toString()
            val description = holder.description.text.toString()

            val intent = Intent("from-cityadapter")
            intent.putExtra("location", location)
            intent.putExtra("description", description)
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent)
        }


        // Edit elementContent - Similar code to when adding a new element in FavoriteCity.
        holder.linearView.setOnClickListener {

            // get the current content
            val tempTitle = dataSet[pos].title
            val tempDescription= dataSet[pos].description

            // Build new dialog for content change
            val dialogBuilder = AlertDialog.Builder(context) // make a dialog builder
            val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder
            dialogView.edit_title.text = tempTitle
            dialogView.edit_description.text = tempDescription

            val alertDialog = dialogBuilder.create()

            alertDialog.show()

            // Change Element content
            val addButton = dialogView.findViewById<Button>(R.id.add_button)
            addButton.text = "Save"
            val edit_title = dialogView.findViewById<TextView>(R.id.edit_title)
            val edit_description = dialogView.findViewById<TextView>(R.id.edit_description)


            // make a common textWatcher to use for several editText listener
            val textWatcher = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val titleInput = edit_title.text
                    val descriptionInput = edit_description.text

                    addButton.isEnabled = (!titleInput.isEmpty() && !descriptionInput.isEmpty())
                }
            }

            edit_title.addTextChangedListener(textWatcher)
            edit_description.addTextChangedListener(textWatcher)


            addButton.setOnClickListener {
                val titleLength = dataSet[pos].title.length
                val descriptLength = dataSet[pos].description.length


                // Edit the gui element content
                holder.title.text = edit_title.text
                holder.description.text = edit_description.text

                validateRiskType(holder.description.text.toString(), holder)

                alertDialog.hide()

                Toast.makeText(addButton.context, "Edit saved", Toast.LENGTH_SHORT).show()
            }

        }

    }
    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun filteredList(list : ArrayList<CityElement>) {
        dataSet = list
        notifyDataSetChanged()
    }

    // Method to validate and change the riskdisplay image by description text.
    fun validateRiskType(text : String, holder : ViewHolder) {
        // Change risk displayimage color.
        when {
            text.contains("hoy", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_red_35dp
                ))

            text.contains("moderat", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_yellow_35dp
                ))

            text.contains("lav", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_green_35dp
                ))

            else -> Log.e("log: ", "FEIL RISK INPUT!!")
        }
    }

    class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {
        val title = textView.findViewById<TextView>(R.id.title_text)
        val description = textView.findViewById<TextView>(R.id.description_text)
        val riskDisplay = textView.findViewById<ImageView>(R.id.risk_display)
        val linearView = textView.findViewById<RelativeLayout>(R.id.relative_view)
        val addButton = textView.findViewById<ImageButton>(R.id.add_button)
    }



}