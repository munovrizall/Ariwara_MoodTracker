package com.artonov.ariwara

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.artonov.ariwara.databinding.ActivityJournalBinding
import java.text.SimpleDateFormat
import java.util.*

class JournalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJournalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListener()
    }

    fun setupListener() {
        binding.apply {
            etDate.setOnClickListener() {
                getDate()
            }
            ivCalendar.setOnClickListener() {
                getDate()
            }
        }
    }

    fun getDate() {
        // Mendapatkan tanggal saat ini
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Membuat instance dari DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this@JournalActivity,
            { view, selectedYear, selectedMonth, selectedDay ->

                // Membuat objek Date dari tanggal yang dipilih
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time

                // Mengecek apakah tanggal yang dipilih melebihi tanggal saat ini
                if (selectedDate.after(Calendar.getInstance().time)) {
                    Toast.makeText(
                        this@JournalActivity,
                        "Tanggal tidak valid!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Mengubah tanggal yang dipilih menjadi string dengan format tertentu
                    val dateString = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        .format(selectedDate)
                    // Set tanggal yang dipilih ke TextInputEditText
                    binding.etDate.setText(dateString)
                }
            },
            year,
            month,
            day
        )
        // Tampilkan DatePickerDialog
        datePickerDialog.show()
    }
}