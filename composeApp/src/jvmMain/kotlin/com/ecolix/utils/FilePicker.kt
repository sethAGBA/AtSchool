package com.ecolix.utils

import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

actual object FilePicker {
    actual fun pickFile(): FileData? {
        return try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            
            val fileChooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.FILES_ONLY
                dialogTitle = "Choisir une image"
                fileFilter = FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "webp")
            }
            
            val result = fileChooser.showOpenDialog(null)
            
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = fileChooser.selectedFile
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
