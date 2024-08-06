package com.example.notesapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsButton = findViewById<Button>(R.id.setting)

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun showSettingsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.settings)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val closeButton = dialog.findViewById<Button>(R.id.close)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val sortDropdown = dialog.findViewById<AutoCompleteTextView>(R.id.sortDropdown)
        val sortOptions = resources.getStringArray(R.array.sortOptions)
        val sortOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, sortOptions)
        sortDropdown.setAdapter(sortOptionsAdapter)

        val searchOptionsDropdown = dialog.findViewById<AutoCompleteTextView>(R.id.searchOptionsDropdown)
        val searchOptions = resources.getStringArray(R.array.searchOptions)
        val searchOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, searchOptions)
        searchOptionsDropdown.setAdapter(searchOptionsAdapter)

        val sortAlgorithmDropdown = dialog.findViewById<AutoCompleteTextView>(R.id.sortAlgorithmDropdown)
        val sortAlgorithmOptions = resources.getStringArray(R.array.sortAlgorithmOptions)
        val sortAlgorithmOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, sortAlgorithmOptions)
        sortAlgorithmDropdown.setAdapter(sortAlgorithmOptionsAdapter)

        val matchAlgorithmDropdown = dialog.findViewById<AutoCompleteTextView>(R.id.matchAlgorithmDropdown)
        val matchAlgorithmOptions = resources.getStringArray(R.array.matchAlgorithmOptions)
        val matchAlgorithmOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, matchAlgorithmOptions)
        matchAlgorithmDropdown.setAdapter(matchAlgorithmOptionsAdapter)

        dialog.show()
    }

}