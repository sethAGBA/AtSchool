package com.ecolix.utils

/**
 * Interface multiplateforme pour la sélection de dossier
 */
expect object FolderPicker {
    /**
     * Ouvre un dialogue de sélection de dossier
     * @param initialDirectory Dossier initial à afficher (optionnel)
     * @return Le chemin du dossier sélectionné, ou null si l'utilisateur a annulé
     */
    fun selectFolder(initialDirectory: String? = null): String?
}
