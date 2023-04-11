package com.artonov.ariwara

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artonov.ariwara.database.DiaryDB
import com.artonov.ariwara.databinding.ActivityProfileBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    val db by lazy { DiaryDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
        getUsername()
        setupPieChart()
    }

    private fun setupBottomNav() {
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

   private fun setupPieChart() {
       CoroutineScope(Dispatchers.IO).launch {
           val moodsWithCount = db.diaryDao().getMoodCounts()
           withContext(Dispatchers.Main) {
               val pieEntries = mutableListOf<PieEntry>()
               moodsWithCount.forEach { mood ->
                   pieEntries.add(PieEntry(mood.count.toFloat(), mood.mood))
               }

               val colors = mutableListOf<Int>(
                   Color.parseColor("#FFEB3B"), // Kuning
                   Color.parseColor("#4CAF50"), // Hijau
                   Color.parseColor("#2196F3"), // Biru
                   Color.parseColor("#FF9800"), // Jingga
                   Color.parseColor("#F44336"), // Merah
               )


               val dataSet = PieDataSet(pieEntries, "")
               dataSet.colors = colors


               val data = PieData(dataSet)
               data.setValueFormatter(object : ValueFormatter() {
                   override fun getFormattedValue(value: Float): String {
                       return value.toInt().toString() // Mengonversi nilai kembali ke bilangan bulat dan mengembalikan sebagai string
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