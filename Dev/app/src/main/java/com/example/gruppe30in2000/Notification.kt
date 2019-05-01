//package com.example.gruppe30in2000
//
//import android.view.View
//import android.widget.CheckBox
//
//class Notification(view: View) {
//    private var view = view
//    val good = view.findViewById<CheckBox>(R.id.goodSwitch)
//    val moderate = view.findViewById<CheckBox>(R.id.moderateSwitch)
//    val bad = view.findViewById<CheckBox>(R.id.badSwitch)
//    val vBad = view.findViewById<CheckBox>(R.id.vBadSwitch)
//
//    fun refreshNotification() : MutableList<String> {
//        val checked = mutableListOf<String>()
//        if (good.isChecked) checked.add("Good")
//        if (moderate.isChecked) checked.add("Moderate")
//        if (bad.isChecked) checked.add("Bad")
//        if (vBad.isChecked) checked.add("VBad")
//        return checked
//    }
//}