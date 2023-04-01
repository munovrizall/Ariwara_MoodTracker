package com.artonov.ariwara

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.artonov.ariwara.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
        getUsername()
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

    private fun getUsername() {
        //Mengambil SharedPreferences dengan mode Context.MODE_PRIVATE atau Context.MODE_MULTI_PROCESS
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", null)

        // Melakukan sesuatu dengan nilai SharedPreferences
        if (name != null) {
            // Tampilkan nama pengguna ke TextView
            binding.textView.text = "Halo, $name"
        }
    }
}