package com.example.notesapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.data.AppDatabase
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteDao
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.fragments.create.SaveFragment
import com.example.notesapp.utilities.exportXLFile
import com.example.notesapp.utilities.importXLFile
import com.example.notesapp.utilities.utilFilter
import com.example.notesapp.utilities.utilMatch
import com.example.notesapp.utilities.utilSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SaveFragment.SaveNoteListener,
    NoteDetailsAdapter.NoteDetailsClickListener, ExcelFragment.ExcelListener {
    private lateinit var binding: ActivityMainBinding
    private var noteDao: NoteDao? = null
    private lateinit var adapter: NoteDetailsAdapter

    // vars for algorithm
    private var sortOption = "Title (A-Z)"
    private var searchOption = "Title and Note"
    private var sortAlgorithm = "Quick Sort"
    private var matchAlgorithm = "KMP"
    private var filterCategory = ""
    private var query = ""

    // var for excel import-export
    private lateinit var excelType : String
    private lateinit var excelName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        initVars()
        attachUIListener()
        populateNotesDisplay()

        binding.setting.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun populateNotesDisplay() {
        lifecycleScope.launch {
            if (query.isEmpty()) {
                noteDao?.readAllNotes()?.collect { noteList ->
                    val sorted = utilSort(sortAlgorithm, sortOption, noteList)
                    if (filterCategory.isEmpty()) {
                        adapter.submitList(sorted)
                    } else {
                        val filtered = utilFilter(matchAlgorithm, filterCategory, sorted)
                        adapter.submitList(filtered)
                    }
                }
            } else {
                noteDao?.readAllNotes()?.collect { noteList ->
                    val matched = utilMatch(matchAlgorithm, searchOption, query, noteList)
                    val sorted = utilSort(sortAlgorithm, sortOption, matched)
                    if (filterCategory.isEmpty()) {
                        adapter.submitList(sorted)
                    } else {
                        val filtered = utilFilter(matchAlgorithm, filterCategory, sorted)
                        adapter.submitList(filtered)
                    }
                }
            }
        }
    }

    private fun initVars() {
        noteDao = AppDatabase.getDatabase(this).noteDao()
        binding.noteList.setHasFixedSize(true)
        binding.noteList.layoutManager = LinearLayoutManager(this)
        adapter = NoteDetailsAdapter(this)
        binding.noteList.adapter = adapter
    }

    private fun attachUIListener() {
        binding.plusButton.setOnClickListener {
            showCreateUpdateDialog()
        }

        binding.excelButton.setOnClickListener {
            showExcelDialog()
        }

        binding.search.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    onQueryChanged(query)
                }

                return true
            }

        })
    }

    private fun showCreateUpdateDialog(note: Note? = null) {
        val fragment = SaveFragment.newInstance(note)
        fragment.setListener(this)
        fragment.show(supportFragmentManager, SaveFragment.TAG)
    }

    private fun showExcelDialog() {
        val fragment = ExcelFragment(this,this)
        fragment.show(supportFragmentManager, ExcelFragment.TAG)
    }

    private fun onQueryChanged(query: String) {
        this.query = query
        populateNotesDisplay()
    }

    private fun showSettingsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_setting)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val filterEntry = dialog.findViewById<EditText>(R.id.filterCategory)
        filterEntry.setText(filterCategory)

        val closeButton = dialog.findViewById<Button>(R.id.close)
        closeButton.setOnClickListener {
            populateNotesDisplay()
            filterCategory = filterEntry.text.toString().trim()
            dialog.dismiss()
        }

        val sortDropdown = dialog.findViewById<AutoCompleteTextView>(R.id.sortDropdown)
        val sortOptions = resources.getStringArray(R.array.sortOptions)
        val sortOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, sortOptions)
        sortDropdown.setText(sortOption)
        sortDropdown.setAdapter(sortOptionsAdapter)
        sortDropdown.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->

            sortOption = adapterView.getItemAtPosition(i).toString()
        }

        val searchOptionsDropdown =
            dialog.findViewById<AutoCompleteTextView>(R.id.searchOptionsDropdown)
        val searchOptions = resources.getStringArray(R.array.searchOptions)
        val searchOptionsAdapter = ArrayAdapter(this, R.layout.dropdown, searchOptions)
        searchOptionsDropdown.setText(searchOption)
        searchOptionsDropdown.setAdapter(searchOptionsAdapter)
        searchOptionsDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->

                searchOption = adapterView.getItemAtPosition(i).toString()
            }

        val sortAlgorithmDropdown =
            dialog.findViewById<AutoCompleteTextView>(R.id.sortAlgorithmDropdown)
        val sortAlgorithmOptions = resources.getStringArray(R.array.sortAlgorithmOptions)
        val sortAlgorithmOptionsAdapter =
            ArrayAdapter(this, R.layout.dropdown, sortAlgorithmOptions)
        sortAlgorithmDropdown.setText(sortAlgorithm)
        sortAlgorithmDropdown.setAdapter(sortAlgorithmOptionsAdapter)
        sortAlgorithmDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->

                sortAlgorithm = adapterView.getItemAtPosition(i).toString()
            }

        val matchAlgorithmDropdown =
            dialog.findViewById<AutoCompleteTextView>(R.id.matchAlgorithmDropdown)
        val matchAlgorithmOptions = resources.getStringArray(R.array.matchAlgorithmOptions)
        val matchAlgorithmOptionsAdapter =
            ArrayAdapter(this, R.layout.dropdown, matchAlgorithmOptions)
        matchAlgorithmDropdown.setText(matchAlgorithm)
        matchAlgorithmDropdown.setAdapter(matchAlgorithmOptionsAdapter)
        matchAlgorithmDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->

                matchAlgorithm = adapterView.getItemAtPosition(i).toString()
            }

        dialog.show()
    }

    override fun onSaveButtonClicked(isUpdate: Boolean, note: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (isUpdate) {
                noteDao?.updateNote(note)
            } else {
                noteDao?.createNote(note)
            }
        }
    }

    override fun editButtonClicked(note: Note) {
        showCreateUpdateDialog(note)
    }

    override fun deleteButtonClicked(note: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            noteDao?.deleteNote(note)
        }
    }

    override fun importExcel(name: String, type: String) {
        excelName = name
        excelType = type
        checkAndRequestPermissionRead()
    }

    override fun exportExcel(name: String, type: String) {
        excelName = name
        excelType = type
        checkAndRequestPermissionWrite()
    }

    private fun checkAndRequestPermissionRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        } else {
            readFile()
        }
    }

    private fun checkAndRequestPermissionWrite() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            writeFile()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeFile()
                }

                return
            }

            2 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    readFile()
                }
            }
        }
    }

    private fun readFile() {
        lifecycleScope.launch {
            noteDao?.deleteAllNotes()
            val list = importXLFile(excelName, excelType)
            for (i in 0 until list.size) {
                noteDao?.createNote(list[i])
            }
        }
        Toast.makeText(this@MainActivity, "Import successful", Toast.LENGTH_SHORT).show()
    }

    private fun writeFile() {
        lifecycleScope.launch {
            val list = noteDao?.readAllNotes()?.first()
            exportXLFile(excelName, excelType, list)
        }
        Toast.makeText(this@MainActivity, "Export successful", Toast.LENGTH_SHORT).show()
    }

}