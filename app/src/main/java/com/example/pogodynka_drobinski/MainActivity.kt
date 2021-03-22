package com.example.pogodynka_drobinski

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pogodynka_drobinski.repository.Repository
import com.google.android.gms.location.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var mainHandler: Handler
    lateinit var timeTV: TextView

    private val updateTextTask = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            timeTV.text = getTime()
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
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val button = findViewById<Button>(R.id.button)
        val adresButton = findViewById<Button>(R.id.Adres)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        val cityNameTV = findViewById<TextView>(R.id.cityName)
        val temperatureTV = findViewById<TextView>(R.id.temperature)
        val pressureTV = findViewById<TextView>(R.id.pressure)
        val sunRiseSet = findViewById<TextView>(R.id.sun_rise_set)
        val weatherIcon = findViewById<ImageView>(R.id.weatherIcon)
        timeTV = findViewById(R.id.time)


        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        getLastLocation()
        viewModel.myResponse.observe(this, { response ->
            cityNameTV.text = response.name
            temperatureTV.text = viewModel.tempConvert(response.main.temp).toString() + "°C"
            pressureTV.text = response.main.pressure.toString()
            sunRiseSet.text = viewModel.timeConvert((response.sys.sunrise).toLong()) + " " + viewModel.timeConvert((response.sys.sunset).toLong())
            viewModel.fetchWeather(response.weather.first(),this)
            weatherIcon.setImageResource(viewModel.icon!!)
        })

        button.setOnClickListener {
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val cityTV: String = findViewById<EditText>(R.id.cityInput).text.toString()
            viewModel.getWeather(cityTV)
            viewModel.myResponse.observe(this, { response ->
                cityNameTV.text = response.name
                temperatureTV.text = viewModel.tempConvert(response.main.temp).toString() + "°C"
                pressureTV.text = response.main.pressure.toString()
                sunRiseSet.text = viewModel.timeConvert((response.sys.sunrise).toLong()) + " " + viewModel.timeConvert((response.sys.sunset).toLong())
            })
        }

        adresButton.setOnClickListener {
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            getLastLocation()
            viewModel.myResponse.observe(this, { response ->
                cityNameTV.text = response.name
                temperatureTV.text = viewModel.tempConvert(response.main.temp).toString()
                pressureTV.text = response.main.pressure.toString()
                sunRiseSet.text = viewModel.timeConvert((response.sys.sunrise).toLong()) + " " + viewModel.timeConvert((response.sys.sunset).toLong())
            })
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
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime():String{
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
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

