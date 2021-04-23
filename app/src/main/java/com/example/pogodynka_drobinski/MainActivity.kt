package com.example.pogodynka_drobinski

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import com.example.pogodynka_drobinski.repository.Repository
import com.example.pogodynka_drobinski.weather.Weather
import com.google.android.gms.location.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var mainHandler: Handler
    lateinit var timeTV: TextView
    lateinit var date: TextView
    lateinit var cityInput: TextView
    lateinit var background: ConstraintLayout

    private val updateTextTask = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            timeTV.text = getTime()
            date.text = getDate()
            mainHandler.postDelayed(this, 1000)
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationRequest = LocationRequest()
    private var lat: String = ""
    private var lon: String = ""

    private lateinit var viewModel: MainViewModel
    private var PERMISSION_ID = 17

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val button = findViewById<Button>(R.id.button)
        val adresButton = findViewById<Button>(R.id.Adres)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        cityInput = findViewById(R.id.cityInput)
        val temperatureTV = findViewById<TextView>(R.id.temperature)
        var pressureTV = findViewById<TextView>(R.id.pressure)
        val desc = findViewById<TextView>(R.id.weather_desc)
        val sunRise = findViewById<TextView>(R.id.sun_rise)
        val sunSet = findViewById<TextView>(R.id.sun_set)
        val weatherIcon = findViewById<ImageView>(R.id.weatherIcon)
        val wilg = findViewById<TextView>(R.id.wilg)
        val tempOdcz = findViewById<TextView>(R.id.temp_odcz)
        val temlDN = findViewById<TextView>(R.id.tempDN)

        val tv2 = findViewById<TextView>(R.id.textView8)
        val tv3 = findViewById<TextView>(R.id.textView9)
        val tv4 = findViewById<TextView>(R.id.textView11)

            background = findViewById(R.id.layout)
        val changeModeBtn = findViewById<Button>(R.id.advanceButton)
        timeTV = findViewById(R.id.time)
        date = findViewById(R.id.date)


        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        getLastLocation()
        viewModel.myResponse.observe(this, { response ->
            cityInput.text = response.name
            temperatureTV.text = viewModel.tempConvert(response.main.temp).toString() + "°C"
            pressureTV.text = response.main.pressure.toString() + "hPa"
            sunRise.text = viewModel.timeConvert((response.sys.sunrise).toLong())
            sunSet.text = viewModel.timeConvert((response.sys.sunset).toLong())
            viewModel.fetchWeather(response.weather.first(),this)
            weatherIcon.setImageResource(viewModel.icon!!)
            desc.text = viewModel.desc
            wilg.text = response.main.humidity.toString() + "%"
            tempOdcz.text = viewModel.tempConvert(response.main.feelsLike).toString() + "°C"
            temlDN.text = viewModel.tempConvert(response.main.tempMax).toString() + "°C" + " / " + viewModel.tempConvert(response.main.tempMin).toString() + "°C"
        })

        button.setOnClickListener {
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            if(cityInput.text != ""){
                val cityTV: String = findViewById<EditText>(R.id.cityInput).text.toString()
                viewModel.getWeather(cityTV)
                viewModel.myResponse.observe(this, { response ->
                    cityInput.text = response.name
                    temperatureTV.text = viewModel.tempConvert(response.main.temp).toString() + "°C"
                    pressureTV.text = response.main.pressure.toString() + "hPa"
                    sunRise.text = viewModel.timeConvert((response.sys.sunrise).toLong())
                    sunSet.text = viewModel.timeConvert((response.sys.sunset).toLong())
                    viewModel.fetchWeather(response.weather.first(),this)
                    weatherIcon.setImageResource(viewModel.icon!!)
                    desc.text = viewModel.desc
                    wilg.text = response.main.humidity.toString() + "%"
                    tempOdcz.text = viewModel.tempConvert(response.main.feelsLike).toString() + "°C"
                    temlDN.text = viewModel.tempConvert(response.main.tempMax).toString() + "°C" + " / " + viewModel.tempConvert(response.main.tempMin).toString() + "°C"
                })
            }
        }

        adresButton.setOnClickListener {
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            getLastLocation()
            viewModel.myResponse.observe(this, { response ->
                cityInput.text = response.name
                temperatureTV.text = viewModel.tempConvert(response.main.temp).toString() + "°C"
                pressureTV.text = response.main.pressure.toString() + "hPa"
                sunRise.text = viewModel.timeConvert((response.sys.sunrise).toLong())
                sunSet.text = viewModel.timeConvert((response.sys.sunset).toLong())
                viewModel.fetchWeather(response.weather.first(),this)
                weatherIcon.setImageResource(viewModel.icon!!)
                desc.text = viewModel.desc
                wilg.text = response.main.humidity.toString() + "%"
                tempOdcz.text = viewModel.tempConvert(response.main.feelsLike).toString() + "°C"
                temlDN.text = viewModel.tempConvert(response.main.tempMax).toString() + "°C" + " / " + viewModel.tempConvert(response.main.tempMin).toString() + "°C"
            })
        }

        changeModeBtn.setOnClickListener {
            if(cityInput.text != "Mountain View"){
                if(changeModeBtn.text == getString(R.string.advanced)){
                    temperatureTV.setTextAppearance(R.style.advanceVer)
                    cityInput.setTextAppearance(R.style.advanceVer)
                    temperatureTV.setTextAppearance(R.style.advanceVer)
                    pressureTV.setTextAppearance(R.style.advanceVer)
                    sunRise.setTextAppearance(R.style.advanceVer)
                    sunSet.setTextAppearance(R.style.advanceVer)
                    desc.setTextAppearance(R.style.advanceVer)
                    timeTV.setTextAppearance(R.style.advanceVer)
                    date.setTextAppearance(R.style.advanceVer)
                    wilg.visibility = View.VISIBLE
                    tempOdcz.visibility = View.VISIBLE
                    temlDN.visibility = View.VISIBLE
                    tv2.visibility = View.VISIBLE
                    tv3.visibility = View.VISIBLE
                    tv4.visibility = View.VISIBLE
                }
                else{
                    temperatureTV.setTextAppearance(R.style.basicVer)
                    cityInput.setTextAppearance(R.style.basicVer)
                    temperatureTV.setTextAppearance(R.style.basicVer)
                    pressureTV.setTextAppearance(R.style.basicVer)
                    sunRise.setTextAppearance(R.style.basicVer)
                    sunSet.setTextAppearance(R.style.basicVer)
                    desc.setTextAppearance(R.style.basicVer)
                    timeTV.setTextAppearance(R.style.basicVer)
                    date.setTextAppearance(R.style.basicVer)
                    wilg.setTextAppearance(R.style.advanceVer)
                    wilg.visibility = View.INVISIBLE
                    tempOdcz.visibility = View.INVISIBLE
                    temlDN.visibility = View.INVISIBLE
                    tv2.visibility = View.INVISIBLE
                    tv3.visibility = View.INVISIBLE
                    tv4.visibility = View.INVISIBLE
                }
                changeModeBtn.text = changeMode(changeModeBtn.text as String)
            }
        }

        mainHandler = Handler(Looper.getMainLooper())
        updateTextTask.run()
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    val location: Location? = task.result
                    if(location == null){
                        getNewLocation()
                    }
                    else{
                        lat = location.latitude.toString()
                        lon = location.longitude.toString()
                        viewModel.getWeather(lat, lon)
                    }
                }
            }
            else{
                Toast.makeText(this,"Please Enable your Location service",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()!!
        )
    }
    private val locationCallback = object : LocationCallback(){
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(p0: LocationResult) {}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime():String{
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate():String{
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    private fun changeMode(string: String):String{

        return if(string == getString(R.string.basic)){
            getString(R.string.advanced)
        } else{
            getString(R.string.basic)
        }
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug", "You have the permission")
            }
        }
    }
}

