package com.example.gpslocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.ViewGroup




class MainActivity : AppCompatActivity(), LocationListener {
    val LOCATION_PERM_CODE = 2
    lateinit var adapter: ArrayAdapter<*>
    var locationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val providersList = findViewById<ListView>(R.id.providers)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.allProviders

        // запрашиваем разрешения на доступ к геопозиции
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // переход в запрос разрешений
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERM_CODE)
        } else {
            locationPermissionGranted = true
        }
        if (locationPermissionGranted) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

            adapter = object : ArrayAdapter<Any?>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                providers as List<Any?>
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val text1 = view.findViewById<View>(android.R.id.text1) as TextView
                    val text2 = view.findViewById<View>(android.R.id.text2) as TextView
                    val provider = providers[position]
                    text1.text = provider
                    if (locationManager.isProviderEnabled(provider)) {
                        text2.text = "Enabled"
                        text2.setTextColor(Color.parseColor("#00FF00"));
                    } else {
                        text2.text = "Disabled"
                        text2.setTextColor(Color.parseColor("#FF0000"));
                    }
                    return view
                }
            }
        } else {
            val requestDenied = arrayOf("Location unavailable, access not granted")
            adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, requestDenied)
            providersList.adapter = adapter
        }

        providersList.adapter = adapter

        val prv = locationManager.getBestProvider(Criteria(), true)
        Log.d("my", locationManager.allProviders.toString())
        if (prv != null) {
            val location = locationManager.getLastKnownLocation(prv)
            if (location != null)
                displayCoord(location.latitude, location.longitude)
                Log.d("mytag", "location set")
        }
    }

    override fun onLocationChanged(loc: Location) {
        val lat = loc.latitude
        val lng = loc.longitude
        displayCoord(lat, lng)
        Log.d("my", "lat " + lat + " long " + lng)
    }

    fun displayCoord(latitude: Double, longtitude: Double) {
        findViewById<TextView>(R.id.lat).text = String.format("%.5f", latitude)
        findViewById<TextView>(R.id.lng).text = String.format("%.5f", longtitude)
    }

    override fun onProviderDisabled(provider: String) {
        adapter.notifyDataSetChanged();
    }

    override fun onProviderEnabled(provider: String) {
        adapter.notifyDataSetChanged();
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERM_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("mytag","granted")
                    locationPermissionGranted = true
                } else {
                    Log.d("mytag","not granted")
                    locationPermissionGranted = false
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                return
            }
        }
    }

    // onProviderDisabled + onProviderEnabled

    // TODO: обработать возврат в активность onRequestPermissionsResult
}