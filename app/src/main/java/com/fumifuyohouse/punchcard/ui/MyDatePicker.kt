package com.fumifuyohouse.punchcard.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

/**
 * Created by kawasaki on 2018/04/19.
 */
class MyDatePicker: DialogFragment() {
    lateinit var mListener: DatePickerDialog.OnDateSetListener

    companion object {
        val TAG = "MyDatePicker"
        val KEY_ARGUMENT_YEAR = "year"
        val KEY_ARGUMENT_MONTH = "month"
        val KEY_ARGUMENT_DATE = "date"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DatePickerDialog.OnDateSetListener) {
            mListener = context
        } else {
            throw RuntimeException("Activity は DatePickerDialog.OnDateSetListener を実装する必要があります")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = arguments.getInt(KEY_ARGUMENT_YEAR, c.get(Calendar.YEAR))
        val month = arguments.getInt(KEY_ARGUMENT_MONTH, c.get(Calendar.MONTH) + 1)
        val date = arguments.getInt(KEY_ARGUMENT_DATE, c.get(Calendar.DATE))

        return DatePickerDialog(activity, mListener, year, month, date)
    }
}