package com.example.myapplication.ui

import android.app.AlertDialog
import android.content.Context

// Show a file selection dialog to choose a .wav file
fun showFileSelectionDialog(context: Context, fileNames: List<String>, onFileSelected: (String) -> Unit) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Select a Sound")

    val fileArray = fileNames.toTypedArray()
    builder.setItems(fileArray) { _, which ->
        onFileSelected(fileArray[which])  // Return selected file
    }

    builder.create().show()
}
