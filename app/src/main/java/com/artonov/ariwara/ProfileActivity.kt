package com.artonov.ariwara

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.artonov.ariwara.database.DiaryDB
import com.artonov.ariwara.databinding.ActivityProfileBinding
import com.artonov.ariwara.util.NotificationReceiver
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    val db by lazy { DiaryDB(this) }

    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        getUsername()
        setupView()
        setupBottomNav()
        setupPieChart()
        supportActionBar?.title = "Profile"
        if (!isPostNotificationPermissionGranted()) requestPermissions()

        binding.ivNotification.setOnClickListener() {
            showTimePicker()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lakukan aksi yang diinginkan
                Toast.makeText(this, "Permission diterima", Toast.LENGTH_SHORT).show()
            } else {
                // Izin tidak diberikan, tampilkan pesan atau lakukan aksi alternatif
                Toast.makeText(this, "Permission ditolak", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun setupView() {
        val wordList = listOf(
            "Kamu kuat dan mampu menghadapi segala tantangan",
            "Kamu memiliki potensi yang luar biasa",
            "Kamu sangat berharga",
            "Kamu layak bahagia",
            "Kamu itu unik",
            "Kamu layak untuk diterima dan dicintai",
            "Kamu pantas mendapatkan segala yang terbaik",
            "Kamu adalah sumber kekuatanmu sendiri",
            "Kamu selalu punya pilihan untuk menjadi lebih baik"
        )
        val randomWord = wordList.random()

        binding.tvSubtitle.text = "\"$randomWord\""

    }

    private fun setupBottomNav() {
        val colorStateList = ContextCompat.getColorStateList(this, R.color.menu_bottom_color)
        binding.apply {
            bottomNavigationView.itemTextColor = colorStateList
            bottomNavigationView.itemIconTintList = colorStateList
        }
        binding.bottomNavigationView.selectedItemId = R.id.menuProfile
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun isPostNotificationPermissionGranted(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.areNotificationsEnabled()
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    private fun requestPermissions() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Izin diperlukan")
            .setMessage("Permission diperlukan untuk memberikan daily affirmation")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.WAKE_LOCK), PERMISSION_REQUEST_CODE)
            }
            .setCancelable(false)
            .show()
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(

            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent

        )

        Toast.makeText(this, "Daily Affirmation berhasil diaktifkan", Toast.LENGTH_SHORT).show()

    }

    private fun showTimePicker() {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "ariwara")

        picker.addOnPositiveButtonClickListener {

            if (picker.hour > 12) {
                String.format("%02d", picker.hour - 12) + " : " + String.format(
                    "%02d",
                    picker.minute
                ) + "PM"

            } else {

                String.format("%02d", picker.hour) + " : " + String.format(
                    "%02d",
                    picker.minute
                ) + "AM"

            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            setAlarm()

        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name: CharSequence = "Ariwara Notification"
            val description = "Channel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ariwara", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }

    }


    private fun setupPieChart() {
        CoroutineScope(Dispatchers.IO).launch {
            val moodsWithCount = db.diaryDao().getMoodCounts()
            withContext(Dispatchers.Main) {
                val pieEntries = mutableListOf<PieEntry>()
                moodsWithCount.forEach { mood ->
                    pieEntries.add(PieEntry(mood.count.toFloat(), mood.mood))
                }

                val colors = mutableListOf<Int>(
                    Color.parseColor("#FACD1C"), // Kuning
                    Color.parseColor("#9ED871"), // Hijau muda
                    Color.parseColor("#2BD028"), // Hijau
                    Color.parseColor("#FF8541"), // Jingga
                    Color.parseColor("#FF565F"), // Merah
                )


                val dataSet = PieDataSet(pieEntries, "")
                dataSet.colors = colors


                val data = PieData(dataSet)
                data.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt()
                            .toString() // Mengonversi nilai kembali ke bilangan bulat dan mengembalikan sebagai string
                    }
                })

                data.setValueTextSize(18f)
                data.setValueTextColor(Color.BLACK)

                binding.moodPieChart.data = data

                binding.moodPieChart.apply {
                    holeRadius = 40f
                    legend.isEnabled = false
                    description.isEnabled = false
                    setEntryLabelTextSize(14f)
                    setEntryLabelColor(Color.BLACK)
                    animateY(1000)
                    invalidate()
                }
            }
        }
    }

    private fun getUsername() {
        //Mengambil SharedPreferences dengan mode Context.MODE_PRIVATE atau Context.MODE_MULTI_PROCESS
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", null)

        // Melakukan sesuatu dengan nilai SharedPreferences
        if (name != null) {
            // Tampilkan nama pengguna ke TextView
            binding.tvUsername.text = "Halo, $name"
        }
    }

}