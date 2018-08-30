package com.fumifuyohouse.punchcard.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.fumifuyohouse.punchcard.R

/**
 * Created by kawasaki on 2018/06/01.
 */
class YesNoDialog: DialogFragment() {
    companion object {
        val TAG = "YesNoDialog"
        fun getInstance(title: String?, message: String, isNeedCancel: Boolean): YesNoDialog {
            val fragment = YesNoDialog()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("message", message)
            bundle.putBoolean("is_need_cancel", isNeedCancel)
            fragment.arguments = bundle
            return fragment
        }
    }

    interface YesNoDialogInterface {
        fun onClickPositiveButton(tag: String)
        fun onClickNegativeButton(tag: String)
    }
    var mListener: YesNoDialogInterface? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is YesNoDialogInterface) {
            mListener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(activity)
        val title = arguments.getString("title")
        if (title != null) {
            dialog.setTitle(title)
        }
        dialog.setMessage(arguments.getString("message"))
        dialog.setPositiveButton(getText(R.string.dialog_yes), {
            dialog, which ->
            mListener?.onClickPositiveButton(tag)
        })
        if (arguments.getBoolean("is_need_cancel")) {
            dialog.setNegativeButton(getText(R.string.dialog_cancel), {
                dialog, which ->
                mListener?.onClickNegativeButton(tag)
            })
        }
        return dialog.create()
    }
}