package com.example.pogodynka_drobinski

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pogodynka_drobinski.repository.Repository
import com.example.pogodynka_drobinski.weather.Weather
import com.example.pogodynka_drobinski.weather.WeatherX
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
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

    var icon: Int? = null
    var desc: String? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    fun fetchWeather(weather: WeatherX, fragment: FragmentActivity){
        when (weather.icon) {
            "01d" -> {
                icon = R.drawable.ic_day_clear
                desc = fragment.getString(R.string.clear_sky)
            }
            "01n" -> {
                icon = R.drawable.ic_night_clear
                desc = fragment.getString(R.string.clear_sky)
            }

            "02d" -> {
                icon = R.drawable.ic_day_partial_cloud
                desc = fragment.getString(R.string.few_clouds)
            }
            "02n" -> {
                icon = R.drawable.ic_night_partial_cloud
                desc = fragment.getString(R.string.few_clouds)
            }

            "03d", "03n" -> {
                icon = R.drawable.ic_cloudy
                desc = fragment.getString(R.string.scattered_clouds)
            }
            "04d", "04n" -> {
                icon = R.drawable.ic_overcast
                desc = fragment.getString(R.string.broken_clouds)
            }
            "09d", "09n" -> {
                icon = R.drawable.ic_rain
                desc = fragment.getString(R.string.shower_rain)
            }

            "10d" -> {
                icon = R.drawable.ic_day_rain
                desc = fragment.getString(R.string.rain)
            }
            "10n" -> {
                icon = R.drawable.ic_night_rain
                desc = fragment.getString(R.string.rain)
            }

            "11d", "11n" -> {
                icon = R.drawable.ic_rain_thunder
                desc = fragment.getString(R.string.thunderstorm)
            }
            "13d", "13n" -> {
                icon = R.drawable.ic_snow
                desc = fragment.getString(R.string.snow)
            }
            "50d", "50n" -> {
                icon = R.drawable.ic_mist
                desc = fragment.getString(R.string.mist)
            }
        }
    }


    fun tempConvert(temp: Double) = (temp - 273.15).roundToInt()

}