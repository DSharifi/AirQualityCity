package com.example.gruppe30in2000

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PreferenceFragment : PreferenceFragmentCompat() {


    companion object {
        val astmaKEY = "astma_key"
        val oldKEY = "old_key"
        val genKEY = "gen_key"
        val heartKEY = "heart_key"
        val pregKEY = "preg_key"

        val alertValue = "alertValue"

    }

    override fun onCreatePreferences(savedstates: Bundle?, rootkey: String?) {
        setPreferencesFromResource(R.xml.settings_screen, rootkey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

}