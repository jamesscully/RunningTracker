package com.scullyapps.mdprunningtracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        main_btn_pasttrips.setOnClickListener {
            startActivity(Intent(this, ViewTripsActivity::class.java))
        }
    }




}
