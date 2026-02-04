package com.ecolix.utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual object FilePicker {
    actual fun pickFile(): FileData? {
        return try {
            // Using FileDialog (AWT) for native feel on macOS/Windows
            val dialog = FileDialog(null as Frame?, "Choisir une image", FileDialog.LOAD).apply {
                file = "*.jpg;*.jpeg;*.png;*.webp" // Hint for some platforms
                setFilenameFilter { _, name ->
                    val lower = name.lowercase()
                    lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
                    lower.endsWith(".png") || lower.endsWith(".webp")
                }
                isVisible = true
            }

            val directory = dialog.directory
            val fileName = dialog.file

            if (directory != null && fileName != null) {
                val file = File(directory, fileName)
                FileData(
                    name = file.name,
                    bytes = file.readBytes()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ Error picking file: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
