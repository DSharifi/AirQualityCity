package com.example.gruppe30in2000.FavCity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.gruppe30in2000.LocalSettings
import com.example.gruppe30in2000.R
import com.example.gruppe30in2000.SettingsFragment


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


        holder.linearView.setOnClickListener {
            // extend view and show basic info
            // https://stackoverflow.com/questions/41464629/expand-collapse-animation-in-cardview

            holder.svevestov.text = "svevestøv"
            holder.nitrogen.text = "nitrogendioksid"
            holder.ozone.text = "oznoe"
            holder.test.text = "TEST"

            if (holder.svevestov.visibility == View.GONE) {
                holder.svevestov.visibility = View.VISIBLE
                holder.nitrogen.visibility = View.VISIBLE
                holder.ozone.visibility = View.VISIBLE
                holder.test.visibility = View.VISIBLE
            }
            else  {
                holder.svevestov.visibility = View.GONE
                holder.nitrogen.visibility = View.GONE
                holder.ozone.visibility = View.GONE
                holder.test.visibility = View.GONE
            }

        }

        holder.ib.setOnClickListener {


            val settings = SettingsFragment()

            settings.loadData()

            val dialogBuilder = AlertDialog.Builder(context) // make a dialog builder
            val dialogView = LayoutInflater.from(context).inflate(R.layout.infobox, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder

            val alertDialog = dialogBuilder.create()


            val statID = dialogView.findViewById<TextView>(R.id.stationID)
            val healthInfo = dialogView.findViewById<TextView>(R.id.specialInfo)
            val extBtn = dialogView.findViewById<Button>(R.id.exitBtn)

            var infotext = ""

            statID.text = holder.title.text.toString()

            val lvl = holder.description.text.toString()

            if (getInfo(lvl).equals("good")) {
                statID.setBackgroundColor(context.getColor(R.color.good))
                extBtn.setBackgroundColor(context.getColor(R.color.good))
                infotext = context.getString(R.string.goodLvl)
                infotext += context.getString(R.string.hEGood)
                infotext += context.getString(R.string.allGood)
                healthInfo.text = infotext
            }
            if (getInfo(lvl).equals("moderat")) {
                statID.setBackgroundColor(context.getColor(R.color.moderate))
                extBtn.setBackgroundColor(context.getColor(R.color.moderate))
                infotext = context.getString(R.string.modLvl)
                infotext += context.getString(R.string.hEModerate)
                if (LocalSettings.astmaState) infotext += context.getString(R.string.astmaM)
                if (LocalSettings.heartState) infotext += context.getString(R.string.hjerteM)
                if (LocalSettings.oldState) infotext += context.getString(R.string.eldreM)
                if (LocalSettings.pregState || LocalSettings.genState) infotext += context.getString(R.string.allGood)
                healthInfo.text = infotext
            }
            if (getInfo(lvl).equals("bad")) {
                statID.setBackgroundColor(context.getColor(R.color.bad))
                extBtn.setBackgroundColor(context.getColor(R.color.bad))
                infotext = context.getString(R.string.badLVl)
                infotext += context.getString(R.string.hEBad)
                if (LocalSettings.astmaState) infotext += context.getString(R.string.astmaB)
                if (LocalSettings.heartState) infotext += context.getString(R.string.hjerteB)
                if (LocalSettings.oldState) infotext += context.getString(R.string.eldreB)
                if (LocalSettings.pregState) infotext += context.getString(R.string.gravideB)
                if (LocalSettings.genState) infotext += context.getString(R.string.generalB)
                healthInfo.text = infotext
            }

            alertDialog.show()

            extBtn.setOnClickListener {
                alertDialog.hide()
            }
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun filteredList(list: ArrayList<CityElement>) {
        dataSet = list
        notifyDataSetChanged()
    }

    // Method to validate and change the riskdisplay image by description text.
    fun validateRiskType(text: String, holder: ViewHolder) {
        // Change risk displayimage color.
        when {
            text.contains("hoy", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_lens_red_35dp
                    )
                )

            text.contains("moderat", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_lens_yellow_35dp
                    )
                )

            text.contains("lav", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_lens_green_35dp
                    )
                )

            else -> Log.e("log: ", "FEIL RISK INPUT!!")
        }
    }

    class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {
        val title = textView.findViewById<TextView>(R.id.title_text)
        val description = textView.findViewById<TextView>(R.id.description_text)
        val riskDisplay = textView.findViewById<ImageView>(R.id.risk_display)
        val linearView = textView.findViewById<RelativeLayout>(R.id.relative_view)
        val addButton = textView.findViewById<ImageButton>(R.id.add_button)
        val ib = textView.findViewById<ImageButton>(R.id.infoButton)


        val svevestov = textView.findViewById<TextView>(R.id.pollution)
        val nitrogen = textView.findViewById<TextView>(R.id.pollution2)
        val ozone = textView.findViewById<TextView>(R.id.pollution3)
        val test = textView.findViewById<TextView>(R.id.pollution4)


    }

    fun getInfo(text: String) : String {

        when {
            text.contains("hoy", ignoreCase = true) ->
                return "bad"
            text.contains("moderat", ignoreCase = true) ->
                return "moderat"
            text.contains("lav", ignoreCase = true) ->
                return "good"

            else -> Log.e("log: ", "FEIL INPUT!!")
        }
        return "ferdig"

    }

}