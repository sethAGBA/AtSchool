package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

// Finance Enums
enum class ExpenseStatus {
    PAID,
    PENDING,
    CANCELLED,
    PLANNED;

    fun toFrench(): String = when (this) {
        PAID -> "Payé"
        PENDING -> "En attente"
        CANCELLED -> "Annulé"
        PLANNED -> "Prévu"
    }
}

enum class ExpenseCategory {
    SALARIES,
    MAINTENANCE,
    UTILITIES,
    SUPPLIES,
    RENT,
    TAXES,
    OTHER;

    fun toFrench(): String = when (this) {
        SALARIES -> "Salaires"
        MAINTENANCE -> "Maintenance"
        UTILITIES -> "Services"
        SUPPLIES -> "Fournitures"
        RENT -> "Loyer"
        TAXES -> "Taxes"
        OTHER -> "Autre"
    }
}

// Inventory Enums
enum class ItemCondition {
    NEW,
    GOOD,
    FAIR,
    POOR,
    BROKEN;

    fun toFrench(): String = when (this) {
        NEW -> "Neuf"
        GOOD -> "Bon"
        FAIR -> "Moyen"
        POOR -> "Mauvais"
        BROKEN -> "Hors service"
    }
}

enum class ItemCategory {
    FURNITURE,
    ELECTRONICS,
    STATIONERY,
    CLEANING,
    SPORTS,
    LABORATORY,
    OTHER;

    fun toFrench(): String = when (this) {
        FURNITURE -> "Mobilier"
        ELECTRONICS -> "Électronique"
        STATIONERY -> "Papeterie"
        CLEANING -> "Nettoyage"
        SPORTS -> "Sports"
        LABORATORY -> "Laboratoire"
        OTHER -> "Autre"
    }
}

enum class InventoryViewMode {
    FINANCE,
    CLASS_FINANCE,
    INVENTORY,
    PROCUREMENT,
    FORM
}

// Data Classes
data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: String,
    val status: ExpenseStatus,
    val recipient: String? = null,
    val note: String? = null
)

data class InventoryItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val quantity: Int,
    val minThreshold: Int, // For low stock alerts
    val condition: ItemCondition,
    val location: String?,
    val purchaseDate: String?,
    val value: Double?
)

data class BudgetSummary(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val projectedBudget: Double
)

data class ClassFinanceSummary(
    val classroomId: String,
    val classroomName: String,
    val totalExpected: Double,
    val totalCollected: Double,
    val outstanding: Double,
    val paymentRate: Float // 0 to 100
)

data class InventoryUiState(
    val viewMode: InventoryViewMode = InventoryViewMode.FINANCE,
    val expenses: List<Expense> = emptyList(),
    val items: List<InventoryItem> = emptyList(),
    val classFinances: List<ClassFinanceSummary> = emptyList(),
    val budgetSummary: BudgetSummary = BudgetSummary(0.0, 0.0, 0.0, 0.0),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
