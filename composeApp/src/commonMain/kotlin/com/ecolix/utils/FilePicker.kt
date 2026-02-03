package com.ecolix.utils

/**
 * Interface multiplateforme pour la sélection de fichier
 */
expect object FilePicker {
    /**
     * Ouvre un dialogue de sélection de fichier
     * @return Les données du fichier (nom et bytes), ou null si l'utilisateur a annulé
     */
    fun pickFile(): FileData?
}

data class FileData(
    val name: String,
    val bytes: ByteArray
)
