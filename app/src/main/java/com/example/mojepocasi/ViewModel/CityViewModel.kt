package com.example.mojepocasi.ViewModel
import androidx.lifecycle.ViewModel
import com.example.mojepocasi.Repository.CityRepository
import com.example.mojepocasi.Server.ApiClient
import com.example.mojepocasi.Server.ApiServices

class CityViewModel(val repository: CityRepository): ViewModel() {
    constructor():this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))
    fun loadCity(q:String,limit:Int)=
        repository.GetCities(q,limit)

}