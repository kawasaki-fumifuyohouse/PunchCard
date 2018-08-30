package com.fumifuyohouse.punchcard.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fumifuyohouse.punchcard.R

/**
 * Created by kawasaki on 2018/04/20.
 */
class ErrorDialog: DialogFragment() {
    companion object {
        val TAG = "ErrorDialog"
        fun getInstance(title: String, errorMessageList: ArrayList<String>): ErrorDialog {
            val fragment = ErrorDialog()
            val bundle = Bundle()
            bundle.putString("errorTitle", title)
            bundle.putStringArrayList("errorMessages", errorMessageList)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val LINE_SEPARATOR = System.lineSeparator()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_fragment_error, container, false)

        // title
        val titleView = view?.findViewById<TextView>(R.id.title)
        if (titleView != null) {
            val title = arguments.getString("errorTitle")
            if (title == null) {
                titleView.visibility = View.GONE

            } else {
                titleView.visibility = View.VISIBLE
                titleView.text = title
            }
        }

        // message
        val errorMessages = arguments.getStringArrayList("errorMessages")
        val sb = StringBuilder()
        for ((index, message) in errorMessages.withIndex()) {
            Log.d(TAG, "${message}")
            if (index != 0) {
                sb.append(LINE_SEPARATOR)
            }
            sb.append(message)
        }
        view?.findViewById<TextView>(R.id.message_view)?.text = sb.toString()
        return view
    }
}