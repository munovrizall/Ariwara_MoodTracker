package com.artonov.ariwara

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.artonov.ariwara.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        // Bottom Navbar
        binding.bottomNavigationView.setupWithNavController(navController)
        val builder = AppBarConfiguration.Builder(navController.graph)

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



//        val username = findViewById<TextView>(R.id.tvUsername)
//        username.text = "Halo $name"

//        // Ambil EditText dan set teksnya dengan nama pengguna
//        val editName = findViewById<EditText>(R.id.editName)
//        editName.setText(name)
//
//        // Tambahkan listener untuk menyimpan nama pengguna yang baru
//        editName.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                // Simpan nama pengguna ke SharedPreferences
//                val editor = prefs.edit()
//                editor.putString("name", editName.text.toString())
//                editor.apply()
//                true
//            } else {
//                false
//            }
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}

