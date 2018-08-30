package com.fumifuyohouse.punchcard.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.fumifuyohouse.punchcard.R
import com.fumifuyohouse.punchcard.preference.NumberPickerPreference
import kotlinx.android.synthetic.main.activity_edit.*


/**
 * Created by kawasaki on 2018/04/20.
 */
class SettingActivity: AppCompatActivity() {
    val TAG = "SettingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        fragmentManager.beginTransaction().replace(R.id.main_area,
                SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)

            val inTime: NumberPickerPreference = preferenceScreen.findPreference(getString(R.string.pref_key_in_time)) as NumberPickerPreference
            inTime.summary = inTime.value

            val outTime: NumberPickerPreference = preferenceScreen.findPreference(getString(R.string.pref_key_out_time)) as NumberPickerPreference
            outTime.summary = outTime.value

            val breakStartTime: NumberPickerPreference = preferenceScreen.findPreference(getString(R.string.pref_key_break_start_time)) as NumberPickerPreference
            breakStartTime.summary = breakStartTime.value

            val breakEndTime: NumberPickerPreference = preferenceScreen.findPreference(getString(R.string.pref_key_break_end_time)) as NumberPickerPreference
            breakEndTime.summary = breakEndTime.value
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            when(key) {
                getString(R.string.pref_key_in_time) -> {
                    findPreference(key).summary = sharedPreferences?.getString(key, "")
                }
                getString(R.string.pref_key_out_time) -> {
                    findPreference(key).summary = sharedPreferences?.getString(key, "")
                }
                getString(R.string.pref_key_break_start_time) -> {
                    findPreference(key).summary = sharedPreferences?.getString(key, "")
                }
                getString(R.string.pref_key_break_end_time) -> {
                    findPreference(key).summary = sharedPreferences?.getString(key, "")
                }
                else  -> {

                }
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences
                    .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences
                    .unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}