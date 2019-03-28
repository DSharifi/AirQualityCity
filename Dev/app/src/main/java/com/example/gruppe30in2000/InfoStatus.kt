package com.example.gruppe30in2000

import android.view.View
import android.widget.CheckBox

class InfoStatus(view: View) {
    private var view = view
    val astma = view.findViewById<CheckBox>(R.id.astma)
    val preg = view.findViewById<CheckBox>(R.id.gravide)
    val heart = view.findViewById<CheckBox>(R.id.hjerte)
    val general = view.findViewById<CheckBox>(R.id.ingen)
    val old = view.findViewById<CheckBox>(R.id.eldre)


    fun getHealtStatus() : MutableList<String> {
        val checked = mutableListOf<String>()
        if (astma.isChecked) checked.add("Astma")
        if (preg.isChecked) checked.add("Preg")
        if (heart.isChecked) checked.add("Heart")
        if (general.isChecked) checked.add("General")
        if (old.isChecked) checked.add("Old")
        return checked
    }
}