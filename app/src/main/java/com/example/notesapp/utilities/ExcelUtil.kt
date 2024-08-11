package com.example.notesapp.utilities

import android.os.Environment
import com.example.notesapp.data.Note
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun exportXLFile(name: String, type: String, list : MutableList<Note>? = null) {
    // should either be XLS or XLSX
    val workbook = if (type == "XLS") {
        HSSFWorkbook()
    } else {
        XSSFWorkbook()
    }
    val worksheet = workbook.createSheet()

    if (list == null) {
        return
    }

    for ((i, note) in list.withIndex()) {
        val row = worksheet.createRow(i)
        row.createCell(0).setCellValue(note.title)
        row.createCell(1).setCellValue(note.note)
        row.createCell(2).setCellValue(note.category)
        row.createCell(3).setCellValue(note.createdAt)
        row.createCell(4).setCellValue(note.updatedAt)
    }

    for (j in 0 until 5) {
        worksheet.setColumnWidth(j, (30*200))
    }
    try {
        val dir = File(Environment.getExternalStorageDirectory(), "MyFiles")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        // should either be XLS or XLSX
        val path = if (type == "XLS") {
            File(dir, "$name.xls")
        } else {
            File(dir, "$name.xlsx")
        }
//        println("CALLED HERE ${path.absolutePath}")

        val outputStream = FileOutputStream(path)
        workbook.write(outputStream)
        outputStream.close()
        workbook.close()
    } catch (e: Exception) {
        println(e.message)
    }
}

fun importXLFile(name: String, type: String) : MutableList<Note> {
    // should either be XLS or XLSX
    val file = if (type == "XLS") {
        File(Environment.getExternalStorageDirectory(), "MyFiles/$name.xls")
    } else {
        File(Environment.getExternalStorageDirectory(), "MyFiles/$name.xlsx")
    }
    val inputStream = FileInputStream(file)

    // should either be XLS or XLSX
    val workbook = if (type == "XLS") {
        HSSFWorkbook(inputStream)
    } else {
        XSSFWorkbook(inputStream)
    }
    val worksheet = workbook.getSheetAt(0)
    val list = mutableListOf<Note>()

    for (row in worksheet) {
        val cellIterator: Iterator<Cell> = row.cellIterator()

        val title = cellIterator.next().stringCellValue
        val note = cellIterator.next().stringCellValue
        val category = cellIterator.next().stringCellValue
        val created = cellIterator.next().stringCellValue
        val updated = cellIterator.next().stringCellValue
        println("Import note $title $note $category $created $updated")

        val newNote = Note(0, title, note, category, created, updated)

        list.add(newNote)
    }

    return list
}

