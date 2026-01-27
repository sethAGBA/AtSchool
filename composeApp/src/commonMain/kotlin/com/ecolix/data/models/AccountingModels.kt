package com.ecolix.data.models

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory {
    TUITION,
    INSCRIPTION,
    SALARY,
    SUPPLIES,
    MAINTENANCE,
    UTILITIES,
    OTHER;

    fun toFrench(): String = when (this) {
        TUITION -> "Scolarité"
        INSCRIPTION -> "Inscription"
        SALARY -> "Salaires"
        SUPPLIES -> "Fournitures"
        MAINTENANCE -> "Maintenance"
        UTILITIES -> "Factures"
        OTHER -> "Autre"
    }
}

data class FinancialTransaction(
    val id: String,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val date: String,
    val reference: String? = null
)

data class AccountingSummary(
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)

data class AccountingUiState(
    val transactions: List<FinancialTransaction> = emptyList(),
    val summary: AccountingSummary = AccountingSummary(),
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategory: TransactionCategory? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
