package com.artonov.ariwara

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.artonov.ariwara.databinding.FragmentHomeBinding
import com.artonov.ariwara.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root


        // Mengambil SharedPreferences dengan mode Context.MODE_PRIVATE atau Context.MODE_MULTI_PROCESS
        val prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", null)

        // Melakukan sesuatu dengan nilai SharedPreferences
        if (name != null) {
            // Tampilkan nama pengguna ke TextView
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.text = "Halo, $name"
        }
        return view
    }
}