package com.example.mojepocasi.Activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mojepocasi.R
import com.example.mojepocasi.databinding.ActivityCityListBinding
import com.example.mojepocasi.databinding.CityViewholderBinding

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy{City}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setContentView(R.layout.activity_city_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}