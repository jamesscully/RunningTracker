package com.scullyapps.mdprunningtracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.scullyapps.mdprunningtracker.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        supportActionBar?.hide()


    }
}
