package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.notesapp.databinding.FragmentExcelBinding

class ExcelFragment(private val context: Context, private val listener: ExcelListener) : DialogFragment() {
    private lateinit var binding: FragmentExcelBinding
    private var excelOption = "XLSX"
    private var fileOption = "Internal Storage"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExcelBinding.inflate(layoutInflater)

        binding.close.setOnClickListener {
            dismiss()
        }

        val excelOptions = resources.getStringArray(R.array.excelOptions)
        val excelOptionsAdapter = ArrayAdapter(context, R.layout.dropdown, excelOptions)
        binding.excelType.setText(excelOption)
        binding.excelType.setAdapter(excelOptionsAdapter)
        binding.excelType.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->

            excelOption = adapterView.getItemAtPosition(i).toString()
        }

        val storageOptions = resources.getStringArray(R.array.storageOptions)
        val storageOptionsAdapter = ArrayAdapter(context, R.layout.dropdown, storageOptions)
        binding.storageLoc.setText(fileOption)
        binding.storageLoc.setAdapter(storageOptionsAdapter)
        binding.storageLoc.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->

            fileOption = adapterView.getItemAtPosition(i).toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachUIListener()
    }

    private fun attachUIListener() {
        binding.importExcel.setOnClickListener {
            val fileName = binding.fileName.text.toString()

            if (fileName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a File Name!", Toast.LENGTH_SHORT).show()
            } else {
                listener.importExcel(fileName, excelOption, fileOption)
            }
        }

        binding.exportExcel.setOnClickListener {
            val fileName = binding.fileName.text.toString()

            if (fileName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a File Name!", Toast.LENGTH_SHORT).show()
            } else {
                listener.exportExcel(fileName, excelOption, fileOption)
            }
        }
    }

    companion object {
        const val TAG = "ExcelFragment"
    }

    interface ExcelListener {
        fun importExcel(name: String, type: String, loc: String)
        fun exportExcel(name: String, type: String, loc: String)
    }
}