package com.fadlurahmanfdev.example.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import com.fadlurahmanfdev.example.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class DateTimePickerBottomsheet(context: Context):BottomSheetDialog(context) {
    private lateinit var callback: Callback

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var btnConfirm: Button
    private lateinit var btnCancel: Button

    init {
        initView()
    }

    private fun initView(){
        val view = LayoutInflater.from(context)
            .inflate(R.layout.bottomsheet_date_time_picker, null)

        setContentView(view)

        datePicker = view.findViewById(R.id.datePicker)
        timePicker = view.findViewById(R.id.timePicker)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)

        timePicker.setIs24HourView(true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnConfirm.setOnClickListener {
            callback.onConfirm(datePicker.dayOfMonth, datePicker.month, datePicker.year, timePicker.currentHour, timePicker.currentMinute)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
            callback.onCancel()
        }
    }

    fun initCallback(callback: Callback){
        this.callback = callback
    }

    interface Callback {
        fun onConfirm(dayOfMonth: Int, month: Int, year: Int, hour: Int, minute: Int)
        fun onCancel()
    }
}