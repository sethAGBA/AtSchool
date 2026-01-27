package com.ecolix.presentation.screens.library

import androidx.compose.ui.graphics.Color
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryScreenModel {
    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onViewModeChange(mode: LibraryViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onCategorySelect(id: String?) {
        _state.value = _state.value.copy(selectedCategoryId = id)
    }

    private fun loadMockData() {
        val mockCategories = listOf(
            LibraryCategory("1", "Littérature", 120, Color(0xFF6366F1)),
            LibraryCategory("2", "Sciences", 85, Color(0xFF10B981)),
            LibraryCategory("3", "Histoire", 45, Color(0xFFF59E0B)),
            LibraryCategory("4", "Dictionnaires", 30, Color(0xFFEC4899)),
            LibraryCategory("5", "BD & Mangas", 60, Color(0xFF8B5CF6))
        )

        val mockBooks = listOf(
            Book("B1", "Une si longue lettre", "Mariama Bâ", "978-2702901502", "Littérature", BookStatus.AVAILABLE, "NEA", 1979),
            Book("B2", "Le Petit Prince", "Antoine de Saint-Exupéry", "978-2070408504", "Littérature", BookStatus.BORROWED, "Gallimard", 1943),
            Book("B3", "Physique-Chimie 3ème", "Collectif", null, "Sciences", BookStatus.AVAILABLE, "Hachette", 2022, quantity = 5, availableQuantity = 3),
            Book("B4", "Dictionnaire Larousse", "Larousse", "978-2035938459", "Dictionnaires", BookStatus.AVAILABLE, "Larousse", 2024, quantity = 10, availableQuantity = 10),
            Book("B5", "L'Étranger", "Albert Camus", "978-2070360024", "Littérature", BookStatus.BORROWED, "Gallimard", 1942),
            Book("B6", "Astérix le Gaulois", "Goscinny & Uderzo", "978-2012101333", "BD & Mangas", BookStatus.AVAILABLE, "Dargaud", 1961, quantity = 3, availableQuantity = 3)
        )

        val mockBorrowings = listOf(
            Borrowing("BR1", "B2", "Le Petit Prince", "S1", "Binta Diallo", "6ème A", "15/01/2026", "29/01/2026"),
            Borrowing("BR2", "B5", "L'Étranger", "S4", "Amadou Barry", "Terminale S", "10/01/2026", "24/01/2026", status = BorrowingStatus.OVERDUE)
        )

        _state.value = _state.value.copy(
            books = mockBooks,
            borrowings = mockBorrowings,
            categories = mockCategories
        )
    }
}
