package com.example.gruppe30in2000.FavCity

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import android.support.v4.content.LocalBroadcastManager
import com.example.gruppe30in2000.PreferenceFragment
import com.example.gruppe30in2000.R
import android.preference.PreferenceManager;



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
            holder.ib.visibility = View.INVISIBLE
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

            val sSText = "Svevestøv nivå: " + String.format("%.2f", dataSet[pos].pm10val) + dataSet[pos].pm10Unit
            val nitText = "Nitrogeninnhold: " + String.format("%.2f", dataSet[pos].nOVal) + dataSet[pos].nOunit
            val ozText = "Ozon nivå: " + String.format("%.2f", dataSet[pos].ozvalue) + dataSet[pos].ozonUnit + "\n"

            val nitrogenLvls = "Nitrogenkilder:\nOppvarming: " + dataSet[pos].nitHeating.toString() + "%\nIndustri: " + dataSet[pos].nitInd +
                    "%\nTrafikk/Eksos: " + dataSet[pos].nitExc + "%\nShipping: " + dataSet[pos].nitShip + "%"

            val pm10Lvls = "Svevestøvkilder:\nOppvarming: " + dataSet[pos].pmHeat.toString() + "%\nIndustri: " + dataSet[pos].pmInd +
                    "%\nEksos: " + dataSet[pos].pmExc + "%\nTrafikk: " + dataSet[pos].pmNonEx + "%\nShipping: " + dataSet[pos].pmShip + "%"


            holder.svevestov.text = sSText
            holder.nitrogen.text = nitText
            holder.ozone.text = ozText
            holder.nitLvls.text = nitrogenLvls
            holder.pm10Lvls.text = pm10Lvls


            if (holder.svevestov.visibility == View.GONE) {
                holder.svevestov.visibility = View.VISIBLE
                holder.nitrogen.visibility = View.VISIBLE
                holder.ozone.visibility = View.VISIBLE
                holder.nitLvls.visibility = View.VISIBLE
                holder.pm10Lvls.visibility = View.VISIBLE
            }
            else  {
                holder.svevestov.visibility = View.GONE
                holder.nitrogen.visibility = View.GONE
                holder.ozone.visibility = View.GONE
                holder.nitLvls.visibility = View.GONE
                holder.pm10Lvls.visibility = View.GONE
            }

        }

        holder.ib.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(context) // make a dialog builder
            val dialogView = LayoutInflater.from(context).inflate(R.layout.infobox, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder

            val alertDialog = dialogBuilder.create()

            val settings = LayoutInflater.from(context).inflate(R.layout.fragment_settings, null)

            val astma = settings.findViewById<CheckBox>(R.id.astma)
            val hjerte = settings.findViewById<CheckBox>(R.id.hjerte)
            val eldre = settings.findViewById<CheckBox>(R.id.eldre)
            val gravide = settings.findViewById<CheckBox>(R.id.gravide)
            val generell = settings.findViewById<CheckBox>(R.id.ingen)

            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

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
                if (prefs.getBoolean(PreferenceFragment.astmaKEY, false) == true) infotext += context.getString(R.string.astmaM)
                if (prefs.getBoolean(PreferenceFragment.heartKEY, false) == true) infotext += context.getString(R.string.hjerteM)
                if (prefs.getBoolean(PreferenceFragment.oldKEY, false) == true) infotext += context.getString(R.string.eldreM)
                if (prefs.getBoolean(PreferenceFragment.pregKEY, false) == true || prefs.getBoolean(PreferenceFragment.genKEY, false) == true) infotext += context.getString(R.string.allGood)
                healthInfo.text = infotext
            }
            if (getInfo(lvl).equals("bad")) {
                statID.setBackgroundColor(context.getColor(R.color.bad))
                extBtn.setBackgroundColor(context.getColor(R.color.bad))
                infotext = context.getString(R.string.badLVl)
                infotext += context.getString(R.string.hEBad)
                if (prefs.getBoolean(PreferenceFragment.astmaKEY, false) == true) infotext += context.getString(R.string.astmaB)
                if (prefs.getBoolean(PreferenceFragment.oldKEY, false) == true) infotext += context.getString(R.string.eldreB)
                if (prefs.getBoolean(PreferenceFragment.heartKEY, false) == true) infotext += context.getString(R.string.hjerteB)
                if (prefs.getBoolean(PreferenceFragment.pregKEY, false) == true) infotext += context.getString(R.string.gravideB)
                if (prefs.getBoolean(PreferenceFragment.genKEY, false) == true) infotext += context.getString(R.string.generalB)
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
        val nitLvls = textView.findViewById<TextView>(R.id.pollution4)
        val pm10Lvls = textView.findViewById<TextView>(R.id.pollution5)


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