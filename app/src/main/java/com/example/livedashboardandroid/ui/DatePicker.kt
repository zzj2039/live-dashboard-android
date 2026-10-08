package com.example.livedashboardandroid.ui

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.livedashboardandroid.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val dateEditText: AppCompatEditText
    private val datePickerButton: ImageButton

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    var onDateSelected: ((String) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.date_picker, this, true)
        
        dateEditText = findViewById(R.id.dateEditText)
        datePickerButton = findViewById(R.id.datePickerButton)

        datePickerButton.setOnClickListener {
            showDatePicker()
        }

        // 设置默认日期
        val defaultDate = dateFormat.format(calendar.time)
        dateEditText.setText(defaultDate)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = dateFormat.format(calendar.time)
                dateEditText.setText(selectedDate)
                onDateSelected?.invoke(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun setDate(date: String) {
        dateEditText.setText(date)
    }

    fun getDate(): String {
        return dateEditText.text.toString()
    }
}