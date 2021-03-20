package com.example.pogodynka_drobinski

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pogodynka_drobinski.repository.Repository
import com.example.pogodynka_drobinski.weather.Weather
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class MainViewModel(private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Weather> = MutableLiveData()

    fun getWeather(cityName: String) {
        viewModelScope.launch {
            val response = Repository.getCurrentWeather(cityName)
            myResponse.value = response
        }
    }

    fun getWeather(lat: String, lon: String){
        viewModelScope.launch {
            val response = Repository.getCurrentWeather(lat, lon)
            myResponse.value = response
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun timeConvert(input: Long):String?{
        return try {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val date = Date(input*1000)
            simpleDateFormat.format(date).toString()
        }catch (e: Exception){
            e.toString()
        }
    }



    fun tempConvert(temp: Double) = (temp - 273.15).roundToInt()
}