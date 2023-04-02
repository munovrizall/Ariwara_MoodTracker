package com.artonov.ariwara

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.artonov.ariwara.database.DiaryDB
import com.artonov.ariwara.adapter.DiaryAdapter
import com.artonov.ariwara.database.Diary
import com.artonov.ariwara.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var diaryAdapter: DiaryAdapter

    val db by lazy { DiaryDB(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUsername()
        setupListener()
        setupRecyclerView()
        setupBottomNav()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun setupListener() {
        binding.fabAdd.setOnClickListener() {
            intentJournal(0, Constant.TYPE_CREATE)
            overridePendingTransition(0, 0)
        }
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter(this, arrayListOf(), object : DiaryAdapter.OnAdapterListener {
            override fun onUpdate(diary: Diary) {
                intentJournal(diary.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(diary: Diary) {
                deleteDiary(diary)
            }
        })
        binding.rvJournal.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = diaryAdapter
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuProfile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    fun intentJournal(diaryId: Int, intentType: Int) {
        startActivity(
            Intent(this@MainActivity, JournalActivity::class.java)
                .putExtra("intent_id", diaryId)
                .putExtra("intent_type", intentType)
        )
    }

    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val diaryResponse = db.diaryDao().getDiaries()
            withContext(Dispatchers.Main) {
                diaryAdapter.setData(diaryResponse)
            }
        }
    }

    fun deleteDiary(diary: Diary) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin ingin menghapus?")
            setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.diaryDao().deleteDiary(diary)
                    loadData()
                }
            })
            setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            alertDialog.show()
        }
    }

    fun getUsername() {
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", null)

        if (name == null) {
            // Tampilkan dialog pertanyaan nama pengguna
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Siapa nama kamu?")

            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, which ->
                // Simpan nama pengguna ke SharedPreferences
                val editor = prefs.edit()
                editor.putString("name", input.text.toString())
                editor.apply()
            }

            builder.show()
        }
    }
}

