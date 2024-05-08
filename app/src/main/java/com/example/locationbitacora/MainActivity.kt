package com.example.locationbitacora

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.locationbitacora.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val CODE_PERMISSION_BACKGROUND = 100
    private var isPermission = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val permissionsArray = permissions.toTypedArray()
        if(hasPermissions(permissionsArray)) {
            isPermission = true
            onPermissionsGranted()
        } else {
            askPermissions(permissionsArray)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            return ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun onPermissionsGranted() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it != null) {
                    printLocation(it)
                } else {
                    Toast.makeText(this, "No se obtuvo ubicación", Toast.LENGTH_SHORT).show()
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    for (location in p0.locations) {
                        printLocation(location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (_: SecurityException) {

        }
    }

    private fun askPermissions(permissions: Array<String>) {
        requestPermissions(
            permissions,
            CODE_PERMISSION_BACKGROUND
        )
    }

    private fun printLocation(location: Location) {
        // guardar coordenadas en room
        Log.d("GPS", "LAT: ${location.latitude} - LONG: ${location.longitude}")

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        val locationDao = db.location()

        var location = Location(
            0,
            location.latitude.toString(),
            location.longitude.toString())

        locationDao.insertAll(location)

       /* val list = locationDao.getAll()

        list.map {
            it.lat
            it.log
        }*/


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CODE_PERMISSION_BACKGROUND) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (grantResults.isNotEmpty() && allPermissionsGranted) {
                isPermission = true
                onPermissionsGranted()
            }
        }
    }
}