package com.artonov.ariwara

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private var diaryId: Int = 0

    val db by lazy { DiaryDB(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Buat Diary"
        setupView()
        setupListener()
        diaryId = intent.getIntExtra("intent_id", 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // tangani klik pada tombol kembali
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupView() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.etDate.setText(currentDate)

        val intentType = intent.getIntExtra("intent_type", 0)
        when (intentType) {
            Constant.TYPE_CREATE -> {
                binding.fabUpdate.visibility = View.GONE
            }
            Constant.TYPE_UPDATE -> {
                binding.fabDone.visibility = View.INVISIBLE
                getDiary()
            }
        }
        binding.fabDone.setImageTintList(ContextCompat.getColorStateList(this, R.color.cream))
        binding.fabUpdate.setImageTintList(ContextCompat.getColorStateList(this, R.color.cream))

    }

    fun setupListener() {
        setupRgMood()
        mood = "Fine"
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
            fabUpdate.setOnClickListener() {
                CoroutineScope(Dispatchers.IO).launch {
                    db.diaryDao().updateDiary(
                        Diary(
                            diaryId,
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

    fun setupRgMood() {
        binding.rbUnhappy.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rbUnhappy.setBackgroundResource(R.drawable.rb_unhappy)
            } else {
                binding.rbUnhappy.setBackgroundResource(R.drawable.rb_unhappy_passive)
            }
        }
        binding.rbSad.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rbSad.setBackgroundResource(R.drawable.rb_sad)
            } else {
                binding.rbSad.setBackgroundResource(R.drawable.rb_sad_passive)
            }
        }
        binding.rbFine.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rbFine.setBackgroundResource(R.drawable.rb_fine)
            } else {
                binding.rbFine.setBackgroundResource(R.drawable.rb_fine_passive)
            }
        }
        binding.rbGood.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rbGood.setBackgroundResource(R.drawable.rb_good)
            } else {
                binding.rbGood.setBackgroundResource(R.drawable.rb_good_passive)
            }
        }
        binding.rbHappy.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rbHappy.setBackgroundResource(R.drawable.rb_happy)
            } else {
                binding.rbHappy.setBackgroundResource(R.drawable.rb_happy_passive)
            }
        }
    }

    fun getDiary() {
        diaryId = intent.getIntExtra("intent_id", 0)

        CoroutineScope(Dispatchers.IO).launch {
            val diary = db.diaryDao().getDiaryById(diaryId)[0]
            binding.apply {
                etDate.setText(diary.date)
                etNote.setText(diary.note)
                when (diary.mood) {
                    "Unhappy" -> binding.rbUnhappy.isChecked = true
                    "Sad" -> binding.rbSad.isChecked = true
                    "Fine" -> binding.rbFine.isChecked = true
                    "Good" -> binding.rbGood.isChecked = true
                    "Happy" -> binding.rbHappy.isChecked = true
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