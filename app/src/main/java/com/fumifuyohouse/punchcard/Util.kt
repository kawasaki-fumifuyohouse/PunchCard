package com.fumifuyohouse.punchcard

import android.util.Log
import com.fumifuyohouse.punchcard.manager.PreferenceHelper
import com.fumifuyohouse.punchcard.model.PunchCard
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kawasaki on 2018/04/18.
 */
class Util {
    companion object {
        val TAG = "Util"
        val dateFormatForShare = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val csvSeparator = ","

        fun insertDummyData(realm: Realm) {
            val NUM = 20
            realm.executeTransaction {
                var inCal = Calendar.getInstance()
                inCal.add(Calendar.DATE, -NUM)
                val inTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.inHour)
                inCal.set(Calendar.HOUR_OF_DAY, inTimeList[0])
                inCal.set(Calendar.MINUTE, inTimeList[1])
                inCal.set(Calendar.SECOND, 0)
                inCal.set(Calendar.MILLISECOND, 0)

                var outCal = Calendar.getInstance()
                outCal.add(Calendar.DATE, -NUM)
                val outTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.outHour)
                outCal.set(Calendar.HOUR_OF_DAY, outTimeList[0])
                outCal.set(Calendar.MINUTE, outTimeList[1])
                outCal.set(Calendar.SECOND, 0)
                outCal.set(Calendar.MILLISECOND, 0)

                for (i in 0..NUM) {
                    val record = PunchCard()
                    record.in_time = inCal.timeInMillis
                    record.out_time = outCal.timeInMillis
                    record.year = inCal.get(Calendar.YEAR)
                    record.month = inCal.get(Calendar.MONTH + 1)
                    record.date = inCal.get(Calendar.DATE)
                    record.setFormattedFullDate()
                    it.copyToRealmOrUpdate(record)

                    inCal.add(Calendar.DATE, 1)
                    outCal.add(Calendar.DATE, 1)
                }
            }
        }

        fun insertDummyMonthlyData(realm: Realm) {
            val MONTH_NUM = 5
            var inCal = Calendar.getInstance()
            inCal.add(Calendar.MONTH, - MONTH_NUM)

            for (i in 0..MONTH_NUM) {
                insertDummyDailyData(realm, inCal)
                inCal.add(Calendar.MONTH, 1)
            }
        }

        fun insertDummyDailyData(realm: Realm, targetMonth: Calendar) {
            Log.d(TAG, "insertDummyDailyData $targetMonth")
            val NUM = 20
            realm.executeTransaction {
                var inCal = Calendar.getInstance()
                inCal.timeInMillis = targetMonth.timeInMillis
                inCal.set(Calendar.DATE, 1)
                val inTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.inHour)
                inCal.set(Calendar.HOUR_OF_DAY, inTimeList[0])
                inCal.set(Calendar.MINUTE, inTimeList[1])
                inCal.set(Calendar.SECOND, 0)
                inCal.set(Calendar.MILLISECOND, 0)

                var outCal = Calendar.getInstance()
                outCal.timeInMillis = targetMonth.timeInMillis
                outCal.set(Calendar.DATE, 1)
                val outTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.outHour)
                outCal.set(Calendar.HOUR_OF_DAY, outTimeList[0])
                outCal.set(Calendar.MINUTE, outTimeList[1])
                outCal.set(Calendar.SECOND, 0)
                outCal.set(Calendar.MILLISECOND, 0)

                for (i in 0..NUM) {
                    val record = PunchCard()
                    record.in_time = inCal.timeInMillis
                    record.out_time = outCal.timeInMillis
                    record.year = inCal.get(Calendar.YEAR)
                    record.month = inCal.get(Calendar.MONTH + 1)
                    record.date = inCal.get(Calendar.DATE)
                    record.setFormattedFullDate()
                    it.copyToRealmOrUpdate(record)

                    inCal.add(Calendar.DATE, 1)
                    outCal.add(Calendar.DATE, 1)
                }
            }
        }

        fun deleteDummyData(realm: Realm) {
            realm.executeTransaction {
                val result = it.where(PunchCard::class.java).findAll()
                result.deleteAllFromRealm()
            }
        }

        /**
         * 同日かどうか判定する
         */
        fun isSameDate(target1: Long, target2: Long) : Boolean {
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = target1
            cal1.set(Calendar.HOUR_OF_DAY, 0)
            cal1.set(Calendar.MINUTE, 0)
            cal1.set(Calendar.SECOND, 0)
            cal1.set(Calendar.MILLISECOND, 0)

            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = target2
            cal2.set(Calendar.HOUR_OF_DAY, 0)
            cal2.set(Calendar.MINUTE, 0)
            cal2.set(Calendar.SECOND, 0)
            cal2.set(Calendar.MILLISECOND, 0)

            return cal1.equals(cal2)
        }

        /**
         * PunchCard型のデータをcsv出力する
         */
        fun parseListToCSV(list: List<PunchCard>): String {
            val sb = StringBuilder()
            val cal = Calendar.getInstance()
            sb.append("in_time, out_time, break_start_time, break_end_time,")
            for (record in list) {
                cal.timeInMillis = record.in_time
                sb.append(dateFormatForShare.format(cal.time))
                sb.append(csvSeparator)
                cal.timeInMillis = record.out_time
                sb.append(dateFormatForShare.format(cal.time))
                sb.append(csvSeparator)
                cal.timeInMillis = record.break_start_time
                sb.append(dateFormatForShare.format(cal.time))
                sb.append(csvSeparator)
                cal.timeInMillis = record.break_end_time
                sb.append(dateFormatForShare.format(cal.time))
                sb.append(csvSeparator)
            }
            return sb.toString()
        }
    }
}