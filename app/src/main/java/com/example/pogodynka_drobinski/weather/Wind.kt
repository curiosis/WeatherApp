package com.example.pogodynka_drobinski.weather


import com.google.gson.annotations.SerializedName

data class Wind(
    val deg: Int,
    val speed: Double
)