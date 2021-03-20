package com.example.pogodynka_drobinski.API

import com.example.pogodynka_drobinski.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("data/2.5/weather?APPID=8684d9488ed05bed7891fbdfc2be0324")
    suspend fun getCurrentWeather(
            @Query("q") cityName: String
    ): Weather

    @GET("data/2.5/weather?APPID=8684d9488ed05bed7891fbdfc2be0324")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Weather
}