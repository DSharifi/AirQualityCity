package com.example.gruppe30in2000


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast


class SettingsFragment : Fragment() {

    val PREFS = "SHARED_PREFS"
    val astmaKEY = "astma_key"
    val oldKEY = "old_key"
    val genKEY = "gen_key"
    val heartKEY = "heart_key"
    val pregKEY = "preg_key"


    lateinit var fView: View

    private var astmaB = false
    private var oldB = false
    private var genB = false
    private var heartB = false
    private var pregB = false

    lateinit var astmaCB: CheckBox
    lateinit var oldCB: CheckBox
    lateinit var genCB: CheckBox
    lateinit var heartCB: CheckBox
    lateinit var pregCB: CheckBox


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        fView = inflater.inflate(R.layout.fragment_settings, container, false)

        astmaCB = fView.findViewById<CheckBox>(R.id.astma)
        oldCB = fView.findViewById<CheckBox>(R.id.eldre)
        genCB = fView.findViewById<CheckBox>(R.id.ingen)
        heartCB = fView.findViewById<CheckBox>(R.id.hjerte)
        pregCB = fView.findViewById<CheckBox>(R.id.gravide)

        val savebtn = fView.findViewById<Button>(R.id.savebutton)

        savebtn.setOnClickListener {
            saveData()
        }

        loadData()
        updateCB()

        return fView
    }

    fun saveData(){
        val preferences = context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.putBoolean(astmaKEY, astmaCB.isChecked)
        editor?.apply()
        editor?.putBoolean(oldKEY, oldCB.isChecked)
        editor?.apply()
        editor?.putBoolean(genKEY, genCB.isChecked)
        editor?.apply()
        editor?.putBoolean(heartKEY, heartCB.isChecked)
        editor?.apply()
        editor?.putBoolean(pregKEY, pregCB.isChecked)
        editor?.apply()

        Toast.makeText(context,"DATA LAGRET", Toast.LENGTH_LONG).show()
    }

    fun loadData(){
        val preferences = context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        if (preferences?.getBoolean(astmaKEY, false) == true) {
            astmaB = true
            LocalSettings.astmaState = true
        }

        if (preferences?.getBoolean(oldKEY, false) == true) {
            oldB = true
            LocalSettings.oldState = true
        }

        if (preferences?.getBoolean(genKEY, false) == true) {
            genB = true
            LocalSettings.genState = true
        }

        if (preferences?.getBoolean(heartKEY, false) == true) {
            heartB = true
            LocalSettings.heartState = true
        }

        if (preferences?.getBoolean(pregKEY, false) == true) {
            pregB = true
            LocalSettings.pregState = true
        }
    }

    fun updateCB(){
        astmaCB.isChecked = astmaB
        oldCB.isChecked = oldB
        genCB.isChecked = genB
        heartCB.isChecked = heartB
        pregCB.isChecked = pregB
    }


}
