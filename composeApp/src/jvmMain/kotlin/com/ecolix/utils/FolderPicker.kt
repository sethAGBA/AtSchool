package com.ecolix.utils

import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager

/**
 * Utilitaire pour ouvrir un sélecteur de dossier
 * Utilise Swing JFileChooser pour la plateforme JVM
 */
actual object FolderPicker {
    
    /**
     * Ouvre un dialogue de sélection de dossier
     * @param initialDirectory Dossier initial à afficher (optionnel)
     * @return Le chemin du dossier sélectionné, ou null si l'utilisateur a annulé
     */
    actual fun selectFolder(initialDirectory: String?): String? {
        return try {
            // Utiliser le look and feel du système pour une meilleure intégration
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            
            val fileChooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialogTitle = "Choisir le dossier de destination"
                
                // Définir le dossier initial si fourni
                if (initialDirectory != null) {
                    currentDirectory = File(initialDirectory)
                } else {
                    // Par défaut, ouvrir dans le dossier Documents
                    val userHome = System.getProperty("user.home")
                    currentDirectory = File(userHome, "Documents")
                }
            }
            
            val result = fileChooser.showDialog(null, "Sélectionner")
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fileChooser.selectedFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ Error opening folder picker: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
