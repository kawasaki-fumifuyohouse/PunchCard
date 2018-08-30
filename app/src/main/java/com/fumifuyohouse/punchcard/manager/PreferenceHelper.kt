package com.fumifuyohouse.punchcard.manager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import com.fumifuyohouse.punchcard.R

/**
 * Created by kawasaki on 2018/04/25.
 */
object PreferenceHelper {
    val TAG = "PreferenceHelper"

    private lateinit var prefs: SharedPreferences
    private lateinit var resource: Resources
    fun setUp(context: Context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        resource = context.resources
    }

    var listRangeStart: Long
        get() = prefs.getLong("list_range_start",0L)
        set(value) { prefs.edit().putLong("list_range_start", value).apply() }

    var listRangeEnd: Long
        get() = prefs.getLong("list_range_end", 0L)
        set(value) { prefs.edit().putLong("list_range_end", value).apply() }

    var inHour: String
        get() = prefs.getString(resource.getString(R.string.pref_key_in_time), resource.getString(R.string.pref_default_in_time))
        set(value) {prefs.edit().putString(resource.getString(R.string.pref_key_in_time), value).apply() }

    var outHour: String
        get() = prefs.getString(resource.getString(R.string.pref_key_out_time), resource.getString(R.string.pref_default_out_time))
        set(value) {prefs.edit().putString(resource.getString(R.string.pref_key_out_time), value).apply() }

    var breakStartHour: String
        get() = prefs.getString(resource.getString(R.string.pref_key_break_start_time), resource.getString(R.string.pref_default_break_start_time))
        set(value) {prefs.edit().putString(resource.getString(R.string.pref_key_break_start_time), value).apply() }

    var breakEndHour: String
        get() = prefs.getString(resource.getString(R.string.pref_key_break_end_time), resource.getString(R.string.pref_default_break_end_time))
        set(value) {prefs.edit().putString(resource.getString(R.string.pref_key_break_end_time), value).apply() }

    fun parseTimeToList(target: String): List<Int> {
        return target.split(Regex(":")).map { str -> str.toInt() }
    }

    fun formatTime(hour: Int, minute: Int): String {
        val newTimeStr = String.format("%02d", hour)
        val newMinuteStr = String.format("%02d", minute)
        return "$newTimeStr:$newMinuteStr"
    }
}