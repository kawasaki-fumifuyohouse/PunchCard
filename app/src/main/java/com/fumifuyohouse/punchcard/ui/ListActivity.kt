package com.fumifuyohouse.punchcard.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import com.fumifuyohouse.punchcard.R
import com.fumifuyohouse.punchcard.Util
import com.fumifuyohouse.punchcard.manager.PreferenceHelper
import com.fumifuyohouse.punchcard.model.PunchCard
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by kawasaki on 2018/04/19.
 */
class ListActivity: AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
            YesNoDialog.YesNoDialogInterface {

    companion object {
        val TAG = "ListActivity"
    }

    private val dateFormatForRange = SimpleDateFormat("yyyy/MM/dd")
    private val timeFormat = SimpleDateFormat("HH:mm")
    private val TAG_DELETE_ITEMS = "delete_items"

    var index: Int = -1
    private val INDEX_START_DATE = 0
    private val INDEX_START_TIME = 1
    private val INDEX_END_DATE = 2
    private val INDEX_END_TIME = 3

    private lateinit var mRealm: Realm
    private lateinit var mAdapter: ListAdapter
    private lateinit var mRangeStart: Calendar
    private lateinit var mRangeEnd: Calendar
    private val REQUEST_EDIT = 0
    private val REQUEST_NEW = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        Realm.init(this)
        mRealm = Realm.getDefaultInstance()

        setupRange()
        setupList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    private fun setupRange() {
        // 期間はじめ
        var startMillis = PreferenceHelper.listRangeStart
        mRangeStart = Calendar.getInstance()
        if (startMillis != 0L) {
            mRangeStart.timeInMillis = startMillis

        } else {
            // 今月の1日10時
            mRangeStart.set(Calendar.DATE, 1)
            val inTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.inHour)
            mRangeStart.set(Calendar.HOUR_OF_DAY, inTimeList[0])
            mRangeStart.set(Calendar.MINUTE, inTimeList[1])
            mRangeStart.set(Calendar.SECOND, 0)
            mRangeStart.set(Calendar.MILLISECOND, 0)
        }
        button_select_start_date.text = dateFormatForRange.format(mRangeStart.timeInMillis)
        button_select_start_date.setOnClickListener({
            index = INDEX_START_DATE
            val datepicker = MyDatePicker()
            val bundle = Bundle()
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_YEAR, mRangeStart.get(Calendar.YEAR))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_MONTH, mRangeStart.get(Calendar.MONTH))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_DATE, mRangeStart.get(Calendar.DATE))
            datepicker.arguments = bundle
            datepicker.show(supportFragmentManager, MyDatePicker.TAG)
        })
        button_select_start_time.text = timeFormat.format(mRangeStart.timeInMillis)
        button_select_start_time.setOnClickListener({
            index = INDEX_START_TIME
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, mRangeStart.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, mRangeStart.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })

        // 期間終わり
        var endMillis = PreferenceHelper.listRangeEnd
        mRangeEnd = Calendar.getInstance()
        if (endMillis != 0L) {
            mRangeEnd.timeInMillis = endMillis

        } else {
            // 今月末日の19時
            mRangeEnd.add(Calendar.MONTH, 1)
            mRangeEnd.set(Calendar.DATE, 1)
            val outTimeList = PreferenceHelper.parseTimeToList(PreferenceHelper.outHour)
            mRangeEnd.set(Calendar.HOUR_OF_DAY, outTimeList[0])
            mRangeEnd.set(Calendar.MINUTE, outTimeList[1])
            mRangeEnd.set(Calendar.SECOND, 0)
            mRangeEnd.set(Calendar.MILLISECOND, 0)
            mRangeEnd.add(Calendar.DATE, -1)
        }
        button_select_end_date.text = dateFormatForRange.format(mRangeEnd.timeInMillis)
        button_select_end_date.setOnClickListener({
            index = INDEX_END_DATE
            val datepicker = MyDatePicker()
            val bundle = Bundle()
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_YEAR, mRangeEnd.get(Calendar.YEAR))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_MONTH, mRangeEnd.get(Calendar.MONTH))
            bundle.putInt(MyDatePicker.KEY_ARGUMENT_DATE, mRangeEnd.get(Calendar.DATE))
            datepicker.arguments = bundle
            datepicker.show(supportFragmentManager, MyDatePicker.TAG)
        })
        button_select_end_time.text = timeFormat.format(mRangeEnd.timeInMillis)
        button_select_end_time.setOnClickListener({
            index = INDEX_END_TIME
            val timepicker = MyTimePicker()
            val bundle = Bundle()
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_HOUR, mRangeEnd.get(Calendar.HOUR_OF_DAY))
            bundle.putInt(MyTimePicker.KEY_ARGUMENT_MINUTE, mRangeEnd.get(Calendar.MINUTE))
            timepicker.arguments = bundle
            timepicker.show(supportFragmentManager, MyTimePicker.TAG)
        })
    }

    private fun setupList() {
        mAdapter = ListAdapter(mRealm, object : ListAdapter.ListItemListener {
            override fun editItem(id: String) {
                val intent = Intent(this@ListActivity, EditActivity::class.java)
                intent.putExtra(EditActivity.ARGUMENT_KEY_ID, id)
                startActivityForResult(intent, REQUEST_EDIT)
            }
        }, mRangeStart.timeInMillis, mRangeEnd.timeInMillis)
        list_view.adapter = mAdapter
        list_view.layoutManager = LinearLayoutManager(this)
        fab.setOnClickListener({
            intent = Intent(this, EditActivity::class.java)
            startActivityForResult(intent, REQUEST_NEW)
        })

        button_delete_all.setOnClickListener({
            val dialog = YesNoDialog.getInstance(null, getString(R.string.dialog_confirm_delete_witin_period), true)
            dialog.show(supportFragmentManager, TAG_DELETE_ITEMS)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingActivity::class.java))
            true
        }
        R.id.action_share -> {
            formatForShare()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when(requestCode) {
                REQUEST_NEW -> {
                    dataLoad()

                }
                REQUEST_EDIT -> {
                    dataLoad()

                }
                else -> {

                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when(index) {
            INDEX_START_DATE -> {
                mRangeStart.set(year, month, dayOfMonth)
                button_select_start_date.text = dateFormatForRange.format(mRangeStart.timeInMillis)
                PreferenceHelper.listRangeStart = mRangeStart.timeInMillis
                dataLoad()
            }
            INDEX_END_DATE -> {
                mRangeEnd.set(year, month, dayOfMonth)
                button_select_end_date.text = dateFormatForRange.format(mRangeEnd.timeInMillis)
                PreferenceHelper.listRangeEnd = mRangeEnd.timeInMillis
                dataLoad()
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        when(index) {
            INDEX_START_TIME -> {
                mRangeStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                mRangeStart.set(Calendar.MINUTE, minute)
                button_select_start_time.text = timeFormat.format(mRangeStart.timeInMillis)
                dataLoad()
            }
            INDEX_END_TIME -> {
                mRangeEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                mRangeEnd.set(Calendar.MINUTE, minute)
                button_select_end_time.text = timeFormat.format(mRangeEnd.timeInMillis)
                dataLoad()
            }
        }
    }

    private fun dataLoad() {
        mAdapter.dataLoad(mRangeStart.timeInMillis, mRangeEnd.timeInMillis)
    }

    private fun formatForShare() {
        val handler = Handler(Looper.getMainLooper())
        Thread(Runnable {
            val realm = Realm.getDefaultInstance()
            val data = realm.where(PunchCard::class.java)
                    ?.between("in_time", mRangeStart.timeInMillis, mRangeEnd.timeInMillis)
                    ?.sort("in_time")?.findAll()

            var result: String? = null
            if (data != null) {
                result = Util.parseListToCSV(data)
            }
            realm.close()
            result = null
            handler.post { share(result) }

        }).start()
    }

    private fun share(message: String?) {
        if (message != null) {
            val builder: ShareCompat.IntentBuilder = ShareCompat.IntentBuilder.from(this);
            builder.setSubject(getString(R.string.list_share_title));
            builder.setText(message);
            builder.setType("text/plain");
            builder.startChooser();

        } else {
            val errorList = ArrayList<String>()
            errorList.add(getString(R.string.error_share_no_data))
            val errorDialog: ErrorDialog = ErrorDialog.getInstance(getString(R.string.error_title_share), errorList)
            errorDialog.show(supportFragmentManager, ErrorDialog.TAG)
        }
    }

    override fun onClickPositiveButton(tag: String) {
        if (tag.equals(TAG_DELETE_ITEMS)) {
            mAdapter.deleteItems(mRangeStart.timeInMillis, mRangeEnd.timeInMillis)
        }
    }

    override fun onClickNegativeButton(tag: String) {
    }
}