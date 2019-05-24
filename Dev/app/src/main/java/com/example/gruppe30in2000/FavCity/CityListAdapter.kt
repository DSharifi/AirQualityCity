package com.example.gruppe30in2000.FavCity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import android.support.v4.content.LocalBroadcastManager
import android.preference.PreferenceManager;
import android.widget.Button;
import android.support.v4.content.ContextCompat.startActivity
import com.example.gruppe30in2000.*
import com.example.gruppe30in2000.Settings.PreferenceFragment
import com.example.gruppe30in2000.StationUtil.GraphActivity
import com.example.gruppe30in2000.StationUtil.PieChartActivity


class CityListAdapter (private var dataSet: ArrayList<CityElement>, context: Context, activityContext: Context?) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.city_element, parent, false) as View
        return ViewHolder(textView)

    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {

        // Sette cardviewet sin lokasjon og deskription text basert på den nåværende posisjonen i listen
        holder.title.text = dataSet[pos].title
        holder.description.text = dataSet[pos].description

        // Validerer tekstene og setter bakgrunnsfarge basert på teksten.
        if (holder.description.text.toString().equals("Lav")) {
            holder.description.setBackgroundResource(R.drawable.rounded_good)
            holder.description.text = " God "
        }
        if (holder.description.text.toString().equals("Moderat")) {
            holder.description.setBackgroundResource(R.drawable.rounded_moderate)
            holder.description.text = " Moderat "
        }
        if (holder.description.text.toString().equals("Hoy")) {
            holder.description.setBackgroundResource(R.drawable.rounded_bad)
            holder.description.text = " Dårlig "
        }

        // Dersom context som kaller på denne adapteren er contexten til AllstationView, så gjør vi addButton og ib synlig.
        if (context is AllStationView) {
            holder.addButton.visibility = View.VISIBLE
            holder.ib.visibility = View.INVISIBLE
        }


        // Når brukeren trykker på legg til knappen. Henter vi den nåværende cardview sin informasjon og sender tilbake til AllstationView
        // Med den action navn: from-cityadapter
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
            val sS25Text = "Svevestøv PM2.5 nivå: " + String.format("%.2f", dataSet[pos].pm25val) + dataSet[pos].pm25unit
            val sS10Text = "Svevestøv PM10 nivå: " + String.format("%.2f", dataSet[pos].pm10val) + dataSet[pos].pm10Unit
            val nitText = "Nitrogeninnhold: " + String.format("%.2f", dataSet[pos].nOVal) + dataSet[pos].nOunit
            val ozText = "Ozon nivå: " + String.format("%.2f", dataSet[pos].ozvalue) + dataSet[pos].ozonUnit
            val aqiText = "AQI nivå: " + String.format("%.2f", dataSet[pos].aqiValue) + "\n"

            holder.svevestovpm10.text = sS10Text
            holder.svevestovpm25.text = sS25Text
            holder.nitrogen.text = nitText
            holder.ozone.text = ozText
            holder.aqilvl.text = aqiText


            holder.linechartButton.setOnClickListener{
                val i = Intent(this.context, GraphActivity::class.java)
                i.putExtra("index",dataSet[pos].index)
                startActivity(this.context, i, null)
            }
            holder.pm10Button.setOnClickListener{
                val i = Intent(this.context, PieChartActivity::class.java)
                i.putExtra("index",dataSet[pos].index)
                i.putExtra("chartNr", 0)
                i.putExtra("timeIndex", dataSet[pos].timeindex)
                startActivity(this.context, i, null)
            }
            holder.pm25Button.setOnClickListener{
                val i = Intent(this.context, PieChartActivity::class.java)
                i.putExtra("index",dataSet[pos].index)
                i.putExtra("chartNr", 1)
                i.putExtra("timeIndex", dataSet[pos].timeindex)
                startActivity(this.context, i, null)
            }
            holder.no2Button.setOnClickListener{
                val i = Intent(this.context, PieChartActivity::class.java)
                i.putExtra("index",dataSet[pos].index)
                i.putExtra("chartNr", 2)
                i.putExtra("timeIndex", dataSet[pos].timeindex)
                startActivity(this.context, i, null)
            }


            if (holder.svevestovpm25.visibility == View.GONE) {
                holder.svevestovpm25.visibility = View.VISIBLE
                holder.svevestovpm10.visibility = View.VISIBLE
                holder.nitrogen.visibility = View.VISIBLE
                holder.ozone.visibility = View.VISIBLE
                holder.aqilvl.visibility = View.VISIBLE
                holder.linechartButton.visibility = View.VISIBLE
                holder.pm10Button.visibility = View.VISIBLE
                holder.pm25Button.visibility = View.VISIBLE
                holder.no2Button.visibility = View.VISIBLE

            }
            else  {
                holder.svevestovpm25.visibility = View.GONE
                holder.svevestovpm10.visibility = View.GONE
                holder.nitrogen.visibility = View.GONE
                holder.ozone.visibility = View.GONE
                holder.aqilvl.visibility = View.GONE
                holder.linechartButton.visibility = View.GONE
                holder.pm10Button.visibility = View.GONE
                holder.pm25Button.visibility = View.GONE
                holder.no2Button.visibility = View.GONE
            }
        }

        holder.ib.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(context) // Bygger en dialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.infobox, null) // Inflater dialogen utifra dialog xml filen
            dialogBuilder.setView(dialogView)

            val alertDialog = dialogBuilder.create()

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
                if (prefs.getBoolean(PreferenceFragment.astmaKEY, false)) infotext += context.getString(R.string.astmaM)
                if (prefs.getBoolean(PreferenceFragment.heartKEY, false)) infotext += context.getString(R.string.hjerteM)
                if (prefs.getBoolean(PreferenceFragment.oldKEY, false)) infotext += context.getString(R.string.eldreM)
                if (prefs.getBoolean(PreferenceFragment.pregKEY, false) || prefs.getBoolean(
                        PreferenceFragment.genKEY, false)) infotext += context.getString(R.string.allGood)
                healthInfo.text = infotext
            }
            if (getInfo(lvl).equals("bad")) {
                statID.setBackgroundColor(context.getColor(R.color.bad))
                extBtn.setBackgroundColor(context.getColor(R.color.bad))
                infotext = context.getString(R.string.badLVl)
                infotext += context.getString(R.string.hEBad)
                if (prefs.getBoolean(PreferenceFragment.astmaKEY, false)) infotext += context.getString(R.string.astmaB)
                if (prefs.getBoolean(PreferenceFragment.oldKEY, false)) infotext += context.getString(R.string.eldreB)
                if (prefs.getBoolean(PreferenceFragment.heartKEY, false)) infotext += context.getString(R.string.hjerteB)
                if (prefs.getBoolean(PreferenceFragment.pregKEY, false)) infotext += context.getString(R.string.gravideB)
                if (prefs.getBoolean(PreferenceFragment.genKEY, false)) infotext += context.getString(R.string.generalB)
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

    class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {
        val title = textView.findViewById<TextView>(R.id.title_text)
        val description = textView.findViewById<TextView>(R.id.description_text)
        val riskDisplay = textView.findViewById<ImageView>(R.id.risk_display)
        val linearView = textView.findViewById<RelativeLayout>(R.id.relative_view)
        val addButton = textView.findViewById<ImageButton>(R.id.add_button)
        val ib = textView.findViewById<ImageButton>(R.id.infoButton)


        val svevestovpm25 = textView.findViewById<TextView>(R.id.pollution)
        val svevestovpm10 = textView.findViewById<TextView>(R.id.pollution1)
        val nitrogen = textView.findViewById<TextView>(R.id.pollution2)
        val ozone = textView.findViewById<TextView>(R.id.pollution3)
        val aqilvl = textView.findViewById<TextView>(R.id.pollution4)
        val linechartButton = textView.findViewById<Button>(R.id.linechart)
        val pm10Button = textView.findViewById<Button>(R.id.piechart_pm10)
        val pm25Button = textView.findViewById<Button>(R.id.piechart_pm25)
        val no2Button = textView.findViewById<Button>(R.id.piechart_no2)
    }

    fun getInfo(text: String) : String {

        when {
            text.contains("dårlig", ignoreCase = true) ->
                return "bad"
            text.contains("moderat", ignoreCase = true) ->
                return "moderat"
            text.contains("god", ignoreCase = true) ->
                return "good"

            else -> Log.e("log: ", "FEIL INPUT!!")
        }
        return "ferdig"
    }
}