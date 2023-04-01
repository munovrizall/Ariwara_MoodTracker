package com.artonov.ariwara

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.artonov.ariwara.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    //UNHAPPY, SAD, FINE, GOOD, HAPPY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUsername()
        setupListener()
        setupBottomNav()
    }

    private fun setupListener() {
        binding.fabAdd.setOnClickListener() {
            val intent = Intent(this, JournalActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
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

