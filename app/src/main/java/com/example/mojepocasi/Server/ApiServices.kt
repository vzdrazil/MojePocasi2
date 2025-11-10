package com.example.mojepocasi.Server
import com.example.mojepocasi.model.CityResponseApi
import com.example.mojepocasi.model.CurrentResponseApi
import com.example.mojepocasi.model.ForecastResponseApi
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

    @GET("data/2.5/forecast")
    fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") ApiKey: String):Call<ForecastResponseApi>


    @GET("geo/1.0/direct")
    fun GetCitiesList(@Query("q") q: String,
                      @Query("limit") limit: Int,
                      @Query("appid") ApiKey: String):Call<CityResponseApi>


}