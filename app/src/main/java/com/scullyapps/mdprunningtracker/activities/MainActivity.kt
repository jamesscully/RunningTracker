package com.scullyapps.mdprunningtracker.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scullyapps.mdprunningtracker.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hide our action bar; it isn't needed here (yet)
        supportActionBar?.hide()

        setupButtons()

    }

    fun setupButtons() {

        main_btn_startstop.setOnClickListener {
            startActivity(Intent(this, TrackActivity::class.java))
        }

        main_btn_pasttrips.setOnClickListener {
            startActivity(Intent(this, ListTripsActivity::class.java))
        }

        main_btn_exit.setOnClickListener {
            finish()
        }
    }

}
