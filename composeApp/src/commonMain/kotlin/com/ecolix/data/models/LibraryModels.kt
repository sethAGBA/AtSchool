package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

enum class BookStatus {
    AVAILABLE,
    BORROWED,
    RESERVED,
    LOST,
    DAMAGED
}

enum class LibraryViewMode {
    CATALOG,
    BORROWINGS,
    CATEGORIES,
    BOOK_DETAILS,
    BORROW_FORM
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String?,
    val category: String,
    val status: BookStatus,
    val publisher: String? = null,
    val year: Int? = null,
    val coverUrl: String? = null,
    val quantity: Int = 1,
    val availableQuantity: Int = 1
)

data class Borrowing(
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val studentId: String,
    val studentName: String,
    val classroom: String,
    val borrowDate: String,
    val dueDate: String,
    val returnDate: String? = null,
    val status: BorrowingStatus = BorrowingStatus.ONGOING
)

enum class BorrowingStatus {
    ONGOING,
    RETURNED,
    OVERDUE
}

data class LibraryCategory(
    val id: String,
    val name: String,
    val bookCount: Int,
    val color: Color
)

data class LibraryUiState(
    val viewMode: LibraryViewMode = LibraryViewMode.CATALOG,
    val books: List<Book> = emptyList(),
    val borrowings: List<Borrowing> = emptyList(),
    val categories: List<LibraryCategory> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val selectedBookId: String? = null
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
