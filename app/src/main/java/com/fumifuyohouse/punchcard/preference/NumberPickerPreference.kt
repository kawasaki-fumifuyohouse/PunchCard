package com.fumifuyohouse.punchcard.preference

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TimePicker


/**
 * Created by kawasaki on 2018/04/25.
 */
class NumberPickerPreference : DialogPreference {
    companion object {
        val TAG = "NumberPickerPreference"

        // ループするかどうか
        val WRAP_SELECTOR_WHEEL = true
    }

    private var picker: TimePicker? = null

    var value: String = ""
        set(value) {
            field = value
            persistString(this.value)
        }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)
    }

    fun setup(context: Context, attrs: AttributeSet) {
//        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference)
//        mInHour = array.getInt(R.styleable.NumberPickerPreference_in_hour, 10)
//        mInMinute = array.getInt(R.styleable.NumberPickerPreference_in_minute, 0)
//
//        array.recycle()
    }

    override fun onCreateDialogView(): View {
        val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER

        picker = TimePicker(context)
        picker!!.layoutParams = layoutParams

        val dialogView = FrameLayout(context)
        dialogView.addView(picker)

        return dialogView
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val time: List<String> = value.split(Regex(":"))
        picker!!.hour = time[0].toInt()
        picker!!.minute = time[1].toInt()
        picker!!.setIs24HourView(true)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            picker!!.clearFocus()
            val newTimeStr = String.format("%02d", picker!!.hour)
            val newMinuteStr = String.format("%02d", picker!!.minute)
            val newValue = "$newTimeStr:$newMinuteStr"
            if (callChangeListener(newValue)) {
                value = newValue
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getString(index)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        value = if (restorePersistedValue) {
            getPersistedString("00:00")

        } else {
            defaultValue as String
        }
    }
}