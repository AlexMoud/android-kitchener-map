package gr.hua.it21533.kitchenerMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.*

class PermissionsActivity : AppCompatActivity() {

    private val TAG = "PERMISSIONS_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permissions_layout)
        checkForPermissions()
    }

    private val REQUEST_LOCATION_STORAGE_PERMISSIONS = 1

    private fun checkForPermissions() {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_STORAGE_PERMISSIONS
            )
        } else {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_STORAGE_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsGranted = true
                    grantResults.forEach { i ->
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false
                        }
                    }
                    if (permissionsGranted) {
                        val intent = Intent(applicationContext, MapsActivity::class.java)
                        startActivity(intent)
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}