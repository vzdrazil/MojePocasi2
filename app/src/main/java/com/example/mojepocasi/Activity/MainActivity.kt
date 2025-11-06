package com.example.mojepocasi.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.mojepocasi.ViewModel.WeatherViewModel
import com.example.mojepocasi.databinding.ActivityMainBinding
import com.example.mojepocasi.model.CurrentResponseApi
import com.example.mojepocasi.ui.theme.MojePocasiTheme
import retrofit2.Call
import retrofit2.Response


class MainActivity : ComponentActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.apply{
            addFlags(windowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor=Color.Transparent
        }
        binding.apply {
            var lat=51.50
            var lon=-0.12
            var name="London"
            CityText.text=name
            progressBar.visibility=View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat,lon,"metric").enqueue(object:retrofit2.Callback<CurrentResponseApi>{
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
                    if(response.isSuccessful){
                        val data=response.body()
                        progressBar.visibility= View.GONE
                        detailLayout.visibility=View.VISIBLE
                        data?.let{StatusText.text=it.weather?.get(0)?.main ?:"-"
                        WindText.text=it.wind.speed.let{Math.round(it).toString()} }
                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        }
        enableEdgeToEdge()
        setContent {
            MojePocasiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MojePocasiTheme {
        Greeting("Android")
    }
}