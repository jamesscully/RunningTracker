package com.scullyapps.mdprunningtracker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar

import com.scullyapps.mdprunningtracker.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hide our action bar; it isn't needed here (yet)
        supportActionBar?.hide()

        setupButtons()


        val extras = JSONObject()

        val ratingArr = JSONArray()
        val comments  = JSONArray()

        extras.put("rating", 2)
        comments.put(1, "thisisacomment1")
        comments.put(5, "thisisacomment5")
        comments.put(2, "thisisacomment2")
        comments.put(3, "thisisacomment3")


        extras.put("ratings", ratingArr)
        extras.put("comments", comments)

        val json = extras.toString()

        println(json)

        fromJSON(json)

    }

    fun fromJSON(json : String) {

        val extras = JSONObject(json)

        val comments = extras.get("comments") as JSONArray

        println("JSONArray Length: ${comments.length()}")

//        println(comments.get(5))

        val rating = extras.get("rating") as Int

        print(rating)

        for(x in 0 until comments.length()) {
            println(comments[x])
        }

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
