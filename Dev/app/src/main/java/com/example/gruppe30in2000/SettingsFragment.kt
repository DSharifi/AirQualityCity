package com.example.gruppe30in2000


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    lateinit var astamaCB : CheckBox
    lateinit var oldCB : CheckBox
    lateinit var genCB : CheckBox
    lateinit var heartCB : CheckBox
    lateinit var pregCB : CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        astamaCB = view.findViewById<CheckBox>(R.id.astma)
        oldCB = view.findViewById<CheckBox>(R.id.eldre)
        genCB = view.findViewById<CheckBox>(R.id.ingen)
        heartCB = view.findViewById<CheckBox>(R.id.hjerte)
        pregCB = view.findViewById<CheckBox>(R.id.gravide)

        if (savedInstanceState != null) {

            Log.e("Saved inst", "RECOVER")

            astamaCB.isChecked = savedInstanceState.getBoolean("astma")

            oldCB.isChecked = savedInstanceState.getBoolean("old")

            heartCB.isChecked = savedInstanceState.getBoolean("heart")

            genCB.isChecked = savedInstanceState.getBoolean("general")

            pregCB.isChecked = savedInstanceState.getBoolean("pregnant")

        }
        return view
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("astma", astamaCB.isChecked)
        outState.putBoolean("old", oldCB.isChecked)
        outState.putBoolean("heart", heartCB.isChecked)
        outState.putBoolean("general", genCB.isChecked)
        outState.putBoolean("pregnant", pregCB.isChecked)

        Log.e("Saved inst", "onSaveInstance")
    }


}
