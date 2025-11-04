package com.example.mojepocasi.Server
import com.example.mojepocasi.model.CurrentResponseApi
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface ApiServices {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") ApiKey: String):Call<CurrentResponseApi>


}