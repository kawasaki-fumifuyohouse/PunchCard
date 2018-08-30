package com.fumifuyohouse.punchcard.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

/**
 * Created by kawasaki on 2018/04/20.
 */
class MyTimePicker : DialogFragment() {
    companion object {
        val TAG = "MyTimePicker"
        val KEY_ARGUMENT_HOUR = "hour"
        val KEY_ARGUMENT_MINUTE = "minute"
    }

    lateinit var mListener: TimePickerDialog.OnTimeSetListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TimePickerDialog.OnTimeSetListener) {
            mListener = context
        } else {
            throw RuntimeException("Activity は TimePickerDialog.OnTimeSetListener を実装する必要があります")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = arguments.getInt(KEY_ARGUMENT_HOUR, c.get(Calendar.HOUR_OF_DAY))
        val minute = arguments.getInt(KEY_ARGUMENT_MINUTE, c.get(Calendar.MINUTE))
        return TimePickerDialog(context, mListener, hour, minute, true)
    }
}