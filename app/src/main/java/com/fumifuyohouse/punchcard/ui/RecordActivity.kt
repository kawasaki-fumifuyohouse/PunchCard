package com.fumifuyohouse.punchcard.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.fumifuyohouse.punchcard.R
import com.fumifuyohouse.punchcard.Util
import com.fumifuyohouse.punchcard.manager.PreferenceHelper
import com.fumifuyohouse.punchcard.model.PunchCard
import io.realm.Realm
import io.realm.Sort
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.activity_record.*
import java.util.*

/**
 * Created by kawasaki on 2018/04/19.
 */
class RecordActivity: AppCompatActivity() {
    companion object {
        val TAG = "RecordActivity"
    }

    lateinit var mRealm : Realm
    var mToday : PunchCard = PunchCard()
    val mDateFormat = SimpleDateFormat("yyyy/MM/dd")
    val mTimeFormat = SimpleDateFormat("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        setSupportActionBar(toolbar)
        Realm.init(this)
        PreferenceHelper.setUp(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingActivity::class.java))
            true
        }
        R.id.action_list -> {
            startActivity(Intent(this, ListActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        setup()
        dataload()
    }

    override fun onStop() {
        super.onStop()
        mRealm.close()
    }

    fun setup() {
        mRealm = Realm.getDefaultInstance()

        // 出勤
        button_in.setOnClickListener({
            mRealm.executeTransactionAsync({ bgRealm: Realm ->
                val now = Calendar.getInstance()
                val record : PunchCard = PunchCard()
                record.year = now.get(Calendar.YEAR)
                record.month = now.get(Calendar.MONTH) + 1
                record.date = now.get(Calendar.DATE)
                record.setFormattedFullDate()
                record.in_time = now.timeInMillis
                bgRealm.copyToRealmOrUpdate(record)

                mToday = record

            }, {
                Log.d(MainActivity.TAG, "INSERT成功")
                button_in.isEnabled = false

                val inTime = Calendar.getInstance()
                inTime.timeInMillis = mToday.in_time
                value_in_date.text = mDateFormat.format(inTime.time)
                value_in_time.text = mTimeFormat.format(inTime.time)

            }, { th: Throwable ->
                Log.d(MainActivity.TAG, "INSERT失敗")
            })
        })

        // 退勤
        button_out.setOnClickListener({
            mRealm.executeTransactionAsync({ bgRealm ->
                val target = bgRealm.where(PunchCard::class.java).equalTo("full_date", mToday.full_date).findFirst()

                if (target == null) {
                    throw RuntimeException("該当レコードがありません")

                } else {
                    target.out_time = Calendar.getInstance().timeInMillis
                    bgRealm.insertOrUpdate(target)

                    mToday = target.copy()
                }

            }, {
                Log.d(MainActivity.TAG, "INSERT成功")
                button_out.isEnabled = false
                val outTime = Calendar.getInstance()
                outTime.timeInMillis = mToday.out_time
                value_out_date.text = mDateFormat.format(outTime.time)
                value_out_time.text = mTimeFormat.format(outTime.time)

            }, { th: Throwable ->
                th.printStackTrace()
                Log.w(MainActivity.TAG, "INSERT失敗", th)
            })
        })

        // FIXME 試験用
        insert_dummy.setOnClickListener({
            Util.insertDummyMonthlyData(mRealm)
        })
        delete_all_data.setOnClickListener({
            Util.deleteDummyData(mRealm)
        })
    }

    fun dataload() {
        Log.d(TAG, "dataload")
        // 最も新しい出勤レコードを探す
        val list = mRealm.where(PunchCard::class.java).sort("in_time", Sort.DESCENDING).findAll()
        if (list.isEmpty()) {
            Log.d(TAG, "データ0件")
            // TODO 何か必要？
            setZero()

            return
        }
        var record: PunchCard? = list.first()
        Log.d(MainActivity.TAG, "採用 ${record}")
        if (record != null && record.in_time != 0L) {
            if (record.out_time == 0L) {
                Log.d(TAG, "dataload 出勤記録有り、退勤記録なし")
                // 退勤記録がない場合、このまま使用する。
                mToday = record.copy()
                button_in.isEnabled = false

                val inTime = Calendar.getInstance()
                inTime.timeInMillis = record.in_time
                value_in_date.text = mDateFormat.format(inTime.time)
                value_in_time.text = mTimeFormat.format(inTime.time)

                value_out_date.text = ""
                value_out_time.text = ""

            } else if (Util.isSameDate(System.currentTimeMillis(), record.out_time)){
                Log.d(TAG, "dataload 出勤記録有り、退勤記録当日分あり")
                // 退勤記録が当日のものである場合、表示する。
                mToday = record.copy()
                button_in.isEnabled = false
                button_out.isEnabled = false

                val inTime = Calendar.getInstance()
                inTime.timeInMillis = record.in_time
                value_in_date.text = mDateFormat.format(inTime.time)
                value_in_time.text = mTimeFormat.format(inTime.time)

                val outTime = Calendar.getInstance()
                outTime.timeInMillis = record.out_time
                value_out_date.text = mDateFormat.format(outTime.time)
                value_out_time.text = mTimeFormat.format(outTime.time)

            } else {
                Log.d(TAG, "dataload 出勤記録有り、退勤記録あり　過去分")
                setZero()
            }
        } else {
            Log.d(TAG, "dataload データなし")
            setZero()
        }
    }

    private fun setZero() {
        value_in_date.text = ""
        value_in_time.text = ""
        value_out_date.text = ""
        value_out_time.text = ""
    }
}