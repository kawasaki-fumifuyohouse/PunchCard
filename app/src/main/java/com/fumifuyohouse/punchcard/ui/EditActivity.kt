package com.fumifuyohouse.punchcard.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import com.fumifuyohouse.punchcard.R
import com.fumifuyohouse.punchcard.model.PunchCard
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by kawasaki on 2018/04/19.
 */
class EditActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        val TAG = "EditActivity"
        val ARGUMENT_KEY_ID = "key_id"
    }

    private lateinit var mRealm: Realm
    private lateinit var mToday: PunchCard
    private val mDateFormat = SimpleDateFormat("yyyy/MM/dd (E)")
    private val mTimeFormat = SimpleDateFormat("HH:mm")
    private val LINE_SEPARATOR = System.lineSeparator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)
        Realm.init(this)
        val id: String? = intent.getStringExtra(ARGUMENT_KEY_ID)
        setup()
        dataload(id)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when (index) {
            INDEX_IN_DATE -> {
                val cal = Calendar.getInstance()
                cal.timeInMillis = mToday.in_time
                cal.set(year, month, dayOfMonth)
                mToday.in_time = cal.timeInMillis
                setField(mToday)
            }
            INDEX_OUT_DATE -> {
                val cal = Calendar.getInstance()
                cal.timeInMillis = mToday.out_time
                cal.set(year, month, dayOfMonth)
                mToday.out_time = cal.timeInMillis
                setField(mToday)
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        when (index) {
            INDEX_IN_TIME -> {
                val cal = Calendar.getInstance()
                if (mToday.in_time != 0L) {
                    cal.timeInMillis = mToday.in_time
                }
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                mToday.in_time = cal.timeInMillis
                setField(mToday)
            }
            INDEX_OUT_TIME -> {
                val cal = Calendar.getInstance()
                if (mToday.out_time != 0L) {
                    cal.timeInMillis = mToday.out_time
                }
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                mToday.out_time = cal.timeInMillis
                setField(mToday)
            }
            INDEX_BREAK_START -> {
                val cal = Calendar.getInstance()
                if (mToday.break_start_time != 0L) {
                    cal.timeInMillis = mToday.break_start_time
                }
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                mToday.break_start_time = cal.timeInMillis
                setField(mToday)
            }
            INDEX_BREAK_OUT -> {
                val cal = Calendar.getInstance()
                if (mToday.break_end_time != 0L) {
                    cal.timeInMillis = mToday.break_end_time
                }
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                mToday.break_end_time = cal.timeInMillis
                setField(mToday)
            }
        }
    }

    var index: Int = -1
    val INDEX_IN_DATE = 0
    val INDEX_IN_TIME = 1
    val INDEX_OUT_DATE = 2
    val INDEX_OUT_TIME = 3
    val INDEX_BREAK_START = 4
    val INDEX_BREAK_OUT = 5

    private fun setup() {
        mRealm = Realm.getDefaultInstance()
        datepicker_in.setOnClickListener({
            index = INDEX_IN_DATE
            val datepicker = MyDatePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.in_time
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_YEAR, inTime.get(Calendar.YEAR))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_MONTH, inTime.get(Calendar.MONTH))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_DATE, inTime.get(Calendar.DATE))
            datepicker.arguments = bundle
            datepicker.show(supportFragmentManager, MyDatePicker.TAG)
        })
        timepicker_in.setOnClickListener({
            index = INDEX_IN_TIME
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.in_time
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, inTime.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, inTime.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })
        timepicker_break_start.setOnClickListener({
            index = INDEX_BREAK_START
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.break_start_time
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, inTime.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, inTime.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })
        datepicker_out.setOnClickListener({
            index = INDEX_OUT_DATE
            val datepicker = MyDatePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.out_time
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_YEAR, inTime.get(Calendar.YEAR))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_MONTH, inTime.get(Calendar.MONTH))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_DATE, inTime.get(Calendar.DATE))
            datepicker.arguments = bundle
            datepicker.show(supportFragmentManager, MyDatePicker.TAG)
        })
        timepicker_out.setOnClickListener({
            index = INDEX_OUT_TIME
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.out_time
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, inTime.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, inTime.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })
        timepicker_break_end.setOnClickListener({
            index = INDEX_BREAK_OUT
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = mToday.break_end_time
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, inTime.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, inTime.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })
        button_submit.setOnClickListener({
            val errorList = validation()
            if (errorList.isNotEmpty()) {
                val arrayList = ArrayList<String>()
                for (error in errorList) {
                    arrayList.add(error.second)
                }

                val errorDialog: ErrorDialog = ErrorDialog.getInstance(getString(R.string.error_title_edit), arrayList)
                errorDialog.show(supportFragmentManager, ErrorDialog.TAG)

            } else {
                insert()
            }
        })
    }

    private fun insert() {
        mRealm.executeTransactionAsync({ bgRealm ->
            bgRealm.insertOrUpdate(mToday)

        }, {
            Log.d(TAG, "INSERT成功")
            setResult(Activity.RESULT_OK)
            finish()

        }, {
            Log.e(TAG, "INSERT失敗")
            it.printStackTrace()
        })
    }

    /**
     * エラーコード
     */
    val ERROR_INTIME_AFTER_OUTTIME = -1
    val ERROR_BREAKSTART_AFTER_BREAKEND = -1
    val ERROR_BREAKSTART_NOT_RANGE = -2
    val ERROR_BREAKEND_NOT_RANGE = -3

    private fun validation(): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()
        if (mToday.out_time <= mToday.in_time) {
            result.add(Pair(ERROR_INTIME_AFTER_OUTTIME, getString(R.string.error_intime_after_outtime)))
        }
        if (mToday.break_end_time <= mToday.break_start_time) {
            result.add(Pair(ERROR_BREAKSTART_AFTER_BREAKEND, getString(R.string.error_breakstart_after_breakend)))
        }
        if (mToday.break_start_time < mToday.in_time || mToday.out_time < mToday.break_start_time) {
            result.add(Pair(ERROR_BREAKSTART_NOT_RANGE, getString(R.string.error_breakstart_not_range)))
        }
        if (mToday.out_time < mToday.break_end_time || mToday.break_end_time < mToday.in_time) {
            result.add(Pair(ERROR_BREAKEND_NOT_RANGE, getString(R.string.error_breakend_not_range)))
        }
        return result
    }

    private fun dataload(id: String?) {
        if (id != null) {
            var record = mRealm.where(PunchCard::class.java).equalTo("full_date", id).findFirst()
            if (record != null) {
                mToday = record.copy()
            } else {
                throw RuntimeException("該当レコードがありません。id:${id}")
            }
        } else {
            mToday = PunchCard()
            mToday.setDefault()
        }
        setField(mToday)
    }

    private fun setField(record: PunchCard) {
        if (record.in_time != 0L) {
            val inTime = Calendar.getInstance()
            inTime.timeInMillis = record.in_time
            datepicker_in.text = mDateFormat.format(inTime.time)
            timepicker_in.text = mTimeFormat.format(inTime.time)
        } else {
            datepicker_in.text = ""
            timepicker_in.text = ""
        }
        if (record.out_time != 0L) {
            val outTime = Calendar.getInstance()
            outTime.timeInMillis = record.out_time
            datepicker_out.text = mDateFormat.format(outTime.time)
            timepicker_out.text = mTimeFormat.format(outTime.time)
        } else {
            datepicker_out.text = ""
            timepicker_out.text = ""
        }
        if (record.break_start_time != 0L) {
            val breakStartTime = Calendar.getInstance()
            breakStartTime.timeInMillis = record.break_start_time
            timepicker_break_start.text = mTimeFormat.format(breakStartTime.time)
        } else {
            timepicker_break_start.text = ""
        }
        if (record.break_end_time != 0L) {
            val breakEndTime = Calendar.getInstance()
            breakEndTime.timeInMillis = record.break_end_time
            timepicker_break_end.text = mTimeFormat.format(breakEndTime.time)
        } else {
            timepicker_break_end.text = ""
        }
    }
}