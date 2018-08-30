package com.fumifuyohouse.punchcard.model

import android.util.Log
import com.fumifuyohouse.punchcard.manager.PreferenceHelper
import io.realm.RealmModel
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kawasaki on 2018/04/10.
 */
@RealmClass
open class PunchCard : RealmModel {
    @PrimaryKey
    var full_date : String = ""
    var year: Int = 0
    var month: Int = 0
    var date: Int = 0
    var in_time: Long = 0
    var out_time: Long = 0
    var break_start_time: Long = 0
    var break_end_time: Long = 0

    @Ignore
    var isFooter: Boolean = false

    fun setFormattedFullDate(year: Int, month: Int, date: Int) {
        full_date = "${month}_${year}_${date}"
    }

    fun setFormattedFullDate() {
        full_date = "${month}_${year}_${date}"
    }

    fun copy(): PunchCard {
        val result = PunchCard()
        result.full_date = this.full_date
        result.year = this.year
        result.month = this.month
        result.date = this.date
        result.in_time = this.in_time
        result.out_time = this.out_time
        result.break_start_time = this.break_start_time
        result.break_end_time = this.break_end_time
        result.isFooter = this.isFooter
        return result
    }

    fun setDefault() {
        val now = Calendar.getInstance()
        year = now.get(Calendar.YEAR)
        month = now.get(Calendar.MONTH) + 1
        date = now.get(Calendar.DATE)

        val inTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.inHour)
        now.set(Calendar.HOUR_OF_DAY, inTimeList[0])
        now.set(Calendar.MINUTE, inTimeList[1])
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.MILLISECOND, 0)
        in_time = now.timeInMillis

        val breakStartTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.breakStartHour)
        now.set(Calendar.HOUR_OF_DAY, breakStartTimeList[0])
        now.set(Calendar.MINUTE, breakStartTimeList[1])
        break_start_time = now.timeInMillis

        val breakEndTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.breakEndHour)
        now.set(Calendar.HOUR_OF_DAY, breakEndTimeList[0])
        now.set(Calendar.MINUTE, breakEndTimeList[1])
        break_end_time = now.timeInMillis

        val outTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.outHour)
        now.set(Calendar.HOUR_OF_DAY, outTimeList[0])
        now.set(Calendar.MINUTE, outTimeList[1])
        out_time = now.timeInMillis

        setFormattedFullDate()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("in_time:")
        sb.append(in_time)
        sb.append("out_time:")
        sb.append(out_time)
        sb.append("break_start_time:")
        sb.append(break_start_time)
        sb.append("break_end_time:")
        sb.append(break_end_time)
        return sb.toString()
    }
}