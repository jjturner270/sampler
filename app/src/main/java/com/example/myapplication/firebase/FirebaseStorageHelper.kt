package com.example.myapplication.firebase

import android.content.Context
import android.os.Environment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

// Fetch list of .wav files from Firebase Storage
fun fetchWavFiles(callback: (List<String>) -> Unit) {
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("sounds/") // Adjust folder path as needed

    storageRef.listAll()
        .addOnSuccessListener { listResult ->
            val fileNames = listResult.items.map { it.name }
            callback(fileNames)  // Return list of filenames
        }
        .addOnFailureListener {
            callback(emptyList()) // Return empty list if an error occurs
        }
}

// Download selected .wav file to local storage
fun downloadFile(context: Context, fileName: String, onComplete: (String?) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("sounds/$fileName")

    // Define the local storage path
    val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

    storageRef.getFile(localFile)
        .addOnSuccessListener {
            onComplete(localFile.absolutePath)  // Return file path when download completes
        }
        .addOnFailureListener {
            onComplete(null)  // Handle failure
        }
}
