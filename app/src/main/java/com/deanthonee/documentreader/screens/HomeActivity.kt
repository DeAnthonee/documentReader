package com.deanthonee.documentreader.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.deanthonee.documentreader.R

class HomeActivity : AppCompatActivity() {

    lateinit var firebaseButton:Button
    lateinit var cogButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initViews()
        onClicks()
    }

    private fun onClicks() {
        firebaseButton.setOnClickListener {
            startActivity(FirebaseActivity.newIntent(this))
        }

        cogButton.setOnClickListener {
            startActivity(CogServicesActivity.newIntent(this))
        }
    }

    private fun initViews() {
        firebaseButton = findViewById(R.id.firebase_button)
        cogButton = findViewById(R.id.microsoft_button)
    }
}