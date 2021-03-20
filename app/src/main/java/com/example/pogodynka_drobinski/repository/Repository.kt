package com.example.pogodynka_drobinski.repository

import com.example.pogodynka_drobinski.API.RetrofitInstance
import com.example.pogodynka_drobinski.weather.Weather
import retrofit2.awaitResponse

class Repository {

    companion object{
        suspend fun getCurrentWeather(cityName: String): Weather =
                RetrofitInstance.api.getCurrentWeather(cityName)

        suspend fun getCurrentWeather(lat: String, lon: String): Weather =
            RetrofitInstance.api.getCurrentWeather(lat, lon)
    }
}
