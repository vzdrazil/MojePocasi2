package com.example.mojepocasi.Repository

import com.example.mojepocasi.Server.ApiServices

class WeatherRepository(val api: ApiServices) {
    fun GetCurrentWeather(lat:Double,lng: Double,unit:String)=api.getCurrentWeather(lat,lng,unit,"API_KEY_HERE_ISNT")
}