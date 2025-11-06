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
import com.example.mojepocasi.R
import com.example.mojepocasi.ViewModel.WeatherViewModel
import com.example.mojepocasi.databinding.ActivityMainBinding
import com.example.mojepocasi.model.CurrentResponseApi
import com.example.mojepocasi.ui.theme.MojePocasiTheme
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import com.github.matteobattilana.weather.PrecipType


class MainActivity : ComponentActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy{ Calendar.getInstance() }
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
                        data?.let{
                            StatusText.text=it.weather?.get(0)?.main ?: "-"
                        WindText.text=it.wind.speed.let{Math.round(it).toString()} +"Km"
                            MaxTempText.text=it.main.tempMax.let{Math.round(it).toString()}+"°"
                        CurrentTempText.text=it.main.temp.let{Math.round(it).toString()}+"°"
                        MinTempText.text=it.main.tempMax.let{Math.round(it).toString()}+"°"

                        val drawable=if(IsNightNow()) R.drawable.night_bg
                            else{
                                SetDynamicallyWallpaper(it.weather?.get(0)?.icon?:"-")

                            }
                            BgImage.setImageResource(drawable)
                        }

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
    private fun IsNightNow(): Boolean{
    return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }
    private fun SetDynamicallyWallpaper(icon:String):Int{
        return when(icon.dropLast(1)){
            "01"->{
                InitWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }
            "02","03","04"->{
                InitWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09","10","11"->{
                InitWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13"->{
                InitWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50"->{
                InitWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }
    private fun InitWeatherView(type: PrecipType){
        binding.weatherView.apply{
            setWeatherData(type)
            angle=-20
            emissionRate=100.0f
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