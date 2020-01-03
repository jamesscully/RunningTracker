package com.scullyapps.mdprunningtracker.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.SupportMapFragment
import com.scullyapps.mdprunningtracker.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {


    val PERMISSIONS_NEEDED = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        supportActionBar?.hide()


        // if we've already got our permissions, then we don't need to be here
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
           ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            launchMain()
        }



        if(Build.VERSION.SDK_INT < 23) {

        }



        intro_accept.setOnClickListener {
            ActivityCompat.requestPermissions(this, PERMISSIONS_NEEDED, 1337)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if(requestCode == 1337) {

            if(grantResults.size == 3) {
                launchMain()
            }

            if(grantResults.isEmpty()) {
                // we've not got them, do something
            }

        }

    }

    fun launchMain() {
        val startMain : Intent = Intent(this, MainActivity::class.java)
        startActivity(startMain)
        finish()
    }
}
