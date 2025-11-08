package com.example.mojepocasi.Repository

import com.example.mojepocasi.Server.ApiServices

class CityRepository(val api: ApiServices) {
    fun GetCities(q:String,limit:Int)=
        api.GetCitiesList(q,limit,"apiklic")
}