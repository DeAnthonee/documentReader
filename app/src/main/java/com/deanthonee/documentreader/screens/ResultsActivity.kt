package com.deanthonee.documentreader.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deanthonee.documentreader.R

class ResultsActivity : AppCompatActivity() {

    lateinit var resultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        initViews()
        displayData()
    }

    private fun displayData() {
        resultsTextView.text = intent.getStringExtra(RESULTS_KEY)
    }

    private fun initViews() {
         resultsTextView = findViewById(R.id.results_textview)
    }

    companion object {
        fun newIntent(results: String, callingActivity: Context): Intent {
            return Intent(callingActivity, ResultsActivity::class.java).apply { putExtra(RESULTS_KEY, results) }
        }

        const val RESULTS_KEY = "results_key"
    }
}