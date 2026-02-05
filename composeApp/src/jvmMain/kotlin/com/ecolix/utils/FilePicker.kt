package com.ecolix.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual object FilePicker {
    actual suspend fun pickFile(): FileData? = withContext(Dispatchers.IO) {
        try {
            // Using FileDialog (AWT) for native feel on macOS/Windows
            // Need to wrap in runCatching or similar if UI thread concerns arise,
            // but FileDialog is modal and AWT/Swing interop usually handles this.
            // On some platforms, AWT dialogs should be invoked on the EDT? 
            // However, `pickFile` is suspend, so usually we offload. 
            // AWT FileDialog calls might block the calling thread. 
            // Dispatchers.IO is good for blocking IO, but for UI dialogs, 
            // sometimes it needs Dispatchers.Main (Swing) or specific handling.
            // Given the previous code was just a function call, let's keep it simple first
            // but ensuring it matches the signature.
            // Wait, previous file `FilePicker.jvm.kt` used `withContext(Dispatchers.IO)`.
            // Let's stick to `Dispatchers.IO` to match the expected suspend behavior safely 
            // for file reading, though the dialog itself might block that thread.
            
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
