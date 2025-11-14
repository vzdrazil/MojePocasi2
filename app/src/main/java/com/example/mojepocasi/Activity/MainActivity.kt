package com.example.mojepocasi.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.mojepocasi.Adapter.ForecastAdapter
import com.example.mojepocasi.R
import com.example.mojepocasi.ViewModel.WeatherViewModel
import com.example.mojepocasi.databinding.ActivityMainBinding
import com.example.mojepocasi.model.CurrentResponseApi
import com.example.mojepocasi.model.ForecastResponseApi
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.util.*
import android.graphics.Color

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            if (lat == 0.0) {
                lat = 49.4556
                lon = 17.4506
                name = "Přerov"
            }

            AddCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }

            CityText.text = name
            progressBar.visibility = View.VISIBLE

            // Načtení aktuálního počasí
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object : retrofit2.Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi>,
                        response: Response<CurrentResponseApi>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val data = response.body()
                            detailLayout.visibility = View.VISIBLE
                            data?.let {
                                StatusText.text = it.weather?.get(0)?.main ?: "-"
                                WindText.text =
                                    it.wind?.speed?.let { s -> Math.round(s).toString() } + " Km/h"
                                HumidityText.text = it.main?.humidity?.toString() + "%"
                                MaxTempText.text =
                                    it.main?.tempMax?.let { t -> Math.round(t).toString() } + "°"

                                val temp = it.main?.temp?.let { t -> Math.round(t) } ?: 0
                                CurrentTempText.text = "$temp°"


                                val tempColor = when
                                {
                                    temp < -11 -> "#4B0082"       // tmavě fialová
                                    temp in -11..0 -> "#003366"        // tmavě modrá
                                    temp in 0..10 -> "#339966"   // zelená
                                    temp in 11..20 -> "#FFFF66"  // světle žlutá
                                    temp in 21..30 -> "#FF9933"  // oranžová
                                    temp > 30 -> "#FF3300"       // červená
                                    else -> "#000000"            // fallback černá
                                }

                                CurrentTempText.setTextColor(Color.parseColor(tempColor))
                                MinTempText.text =
                                    it.main?.tempMin?.let { t -> Math.round(t).toString() } + "°"

                                val icon = it.weather?.get(0)?.icon ?: "-"

                                // --- OPRAVENÁ LOGIKA PRO DEN/NOC PODLE TIMEZONE ---
                                val timezoneOffset = it.timezone ?: 0 // v sekundách
                                val tz = TimeZone.getTimeZone("GMT")
                                tz.rawOffset = timezoneOffset * 1000 // převedeno na ms

                                val nowLocal = Calendar.getInstance(tz)
                                val sunrise = Calendar.getInstance(tz).apply {
                                    timeInMillis = (it.sys?.sunrise?.toLong() ?: 0L) * 1000L
                                }
                                val sunset = Calendar.getInstance(tz).apply {
                                    timeInMillis = (it.sys?.sunset?.toLong() ?: 0L) * 1000L
                                }

                                val isDay = nowLocal.after(sunrise) && nowLocal.before(sunset)

                                // -------------------------------------------------------

                                val drawable =
                                    setDynamicallyWallpaper(icon, lottieAnimationView, isDay)
                                BgImage.setImageResource(drawable)
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            t.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            // Nastavení blur efektu
            val radius = 10f
            val devorView = window.decorView
            val rootView = devorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground = devorView.background
            rootView?.let {
                blueView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blueView.clipToOutline = true
            }

            // Načtení forecast počasí
            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : retrofit2.Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi?>,
                        response: Response<ForecastResponseApi?>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            blueView.visibility = View.VISIBLE
                            data?.let {
                                forecastAdapter.differ.submitList(it.list)
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL, false
                                    )
                                    adapter = forecastAdapter
                                    visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi?>, t: Throwable) {
                        // Neřešíme
                    }
                })
        }
    }

    private fun setDynamicallyWallpaper(
        icon: String,
        weatherAnim: LottieAnimationView,
        isDay: Boolean
    ): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                weatherAnim.setAnimation(R.raw.clear_sky)
                weatherAnim.playAnimation()
                if (isDay) {
                    weatherAnim.setAnimation(R.raw.clear_sky)
                    weatherAnim.playAnimation()
                    R.drawable.cloudy_bg
                }
                else {
                    weatherAnim.setAnimation(R.raw.moony )
                    weatherAnim.playAnimation()
                    R.drawable.night_bg
                }
            }
            "02", "03", "04" -> {
                weatherAnim.setAnimation(R.raw.cloudy)
                weatherAnim.playAnimation()
                if (isDay) R.drawable.cloudy_bg else R.drawable.night_bg
            }
            "09", "10" -> {
                weatherAnim.setAnimation(R.raw.rain)
                weatherAnim.playAnimation()
                if (isDay) R.drawable.rainy_bg else R.drawable.night_bg
            }
            "11" -> {  // bouřka
                weatherAnim.setAnimation(R.raw.superstorm)
                weatherAnim.playAnimation()
                if (isDay) R.drawable.rainy_bg else R.drawable.night_bg
            }
            "13" -> {
                weatherAnim.setAnimation(R.raw.snow)
                weatherAnim.playAnimation()
                if (isDay) R.drawable.snow_bg else R.drawable.night_bg
            }
            "50" -> {
                weatherAnim.setAnimation(R.raw.haze)
                weatherAnim.playAnimation()
                if (isDay) R.drawable.haze_bg else R.drawable.night_bg
            }
            else -> 0
        }
    }
}
