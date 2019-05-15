package com.example.gruppe30in2000.Settings

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import com.example.gruppe30in2000.R


class PreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        val astmaKEY = "astma_key"
        val oldKEY = "old_key"
        val genKEY = "gen_key"
        val heartKEY = "heart_key"
        val pregKEY = "preg_key"

        val alertValue = "alertValue"
        val mSKey = "MapStyle"


        //Updates and shows the chosen setting-value
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()
            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null)

            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener =
                sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, ""))
        }
    }


    override fun onCreatePreferences(savedstates: Bundle?, rootkey: String?) {
        setPreferencesFromResource(R.xml.settings_screen, rootkey)
        bindPreferenceSummaryToValue(findPreference("alertValue"))
        bindPreferenceSummaryToValue(findPreference("MapStyle"))
        val mailTo = findPreference("MailTo")
        mailTo.setOnPreferenceClickListener {
            val mailto = Intent(Intent.ACTION_SEND)
            mailto.setType("message/rfc822")
            mailto.putExtra(Intent.EXTRA_EMAIL, arrayOf("sdbjunes@student.matnat.uio.no"))
            mailto.putExtra(Intent.EXTRA_SUBJECT, "Tilbakemelding til AirQalityCity")
            mailto.putExtra(Intent.EXTRA_TEXT, "Skriv din tilbakemelding her.. ")
            startActivity(Intent.createChooser(mailto, "velg mail applikasjon"))
            true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }





}