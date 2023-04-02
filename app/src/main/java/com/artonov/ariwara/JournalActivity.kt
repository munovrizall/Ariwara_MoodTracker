package com.artonov.ariwara

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.EditText
import android.widget.Toast
import com.artonov.ariwara.database.Diary
import com.artonov.ariwara.database.DiaryDB
import com.artonov.ariwara.databinding.ActivityJournalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class JournalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJournalBinding
    private var mood: String = ""

    val db by lazy { DiaryDB(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupListener()
    }

    fun setupView() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        binding.etDate.setText(currentDate)
    }

    fun setupListener() {
        binding.apply {
            rgMood.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbUnhappy -> mood = "Unhappy"
                    R.id.rbSad -> mood = "Sad"
                    R.id.rbFine -> mood = "Fine"
                    R.id.rbGood -> mood = "Good"
                    R.id.rbHappy -> mood = "Happy"
                }
            }
            etDate.setOnClickListener() {
                getDate()
            }
            ivCalendar.setOnClickListener() {
                getDate()
            }
            fabDone.setOnClickListener() {
                CoroutineScope(Dispatchers.IO).launch {
                    db.diaryDao().addDiary(
                        Diary(
                            0,
                            mood,
                            binding.etNote.text.toString(),
                            binding.etDate.text.toString()
                        )
                    )
                    finish()
                }
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
                    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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