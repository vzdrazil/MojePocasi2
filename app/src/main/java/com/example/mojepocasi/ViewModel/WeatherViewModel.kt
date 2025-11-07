package com.example.mojepocasi.ViewModel
import com.example.mojepocasi.Repository.WeatherRepository
import androidx.lifecycle.ViewModel
import com.example.mojepocasi.Server.ApiClient
import com.example.mojepocasi.Server.ApiServices

class WeatherViewModel(val repository:WeatherRepository): ViewModel()
{
    constructor():this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))
    fun loadCurrentWeather(lat:Double,lng:Double,unit:String)=
        repository.GetCurrentWeather(lat,lng,unit)

    fun loadForecastWeather(lat:Double,lng:Double,unit:String)=
        repository.GetForecastWeather(lat,lng,unit)
}