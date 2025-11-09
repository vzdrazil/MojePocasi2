package com.example.mojepocasi.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mojepocasi.Adapter.CityAdapter
import com.example.mojepocasi.R
import com.example.mojepocasi.databinding.ActivityCityListBinding
import com.example.mojepocasi.databinding.CityViewholderBinding
import com.example.mojepocasi.model.CityResponseApi
import com.example.mojepocasi.ViewModel.CityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel: CityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.LinearLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        window.apply{
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT

        }

        binding.apply {
            cityEdit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    progressBar2.visibility = View.VISIBLE
                    cityViewModel.loadCity(s.toString(), 10)
                        .enqueue(object : Callback<CityResponseApi> {
                            override fun onResponse(
                                call: Call<CityResponseApi>,
                                response: Response<CityResponseApi>
                            ) {
                                if (response.isSuccessful) {
                                    val data = response.body()
                                    data?.let {
                                        progressBar2.visibility = View.GONE
                                        cityAdapter.differ.submitList(it)
                                        cityView.apply {
                                            layoutManager = LinearLayoutManager(
                                                this@CityListActivity,
                                                LinearLayoutManager.HORIZONTAL, false
                                            )

                                            adapter = cityAdapter

                                        }
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<CityResponseApi?>,
                                t: Throwable
                            ) {

                            }
                        }
                        )
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }
            })
        }
    }
}
