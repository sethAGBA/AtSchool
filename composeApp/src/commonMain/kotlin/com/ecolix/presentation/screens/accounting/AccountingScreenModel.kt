package com.ecolix.presentation.screens.accounting

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountingScreenModel {
    private val _state = MutableStateFlow(AccountingUiState())
    val state: StateFlow<AccountingUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onTypeFilterChange(type: TransactionType?) {
        _state.value = _state.value.copy(selectedType = type)
    }

    fun onCategoryFilterChange(category: TransactionCategory?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    private fun loadMockData() {
        val transactions = listOf(
            FinancialTransaction(
                id = "1",
                description = "Paiement Scolarité Issa Traoré",
                amount = 150000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.TUITION,
                date = "26/01/2026",
                reference = "REF-2026-001"
            ),
            FinancialTransaction(
                id = "2",
                description = "Achat Fournitures Bureau",
                amount = 25000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.SUPPLIES,
                date = "25/01/2026",
                reference = "F0452"
            ),
            FinancialTransaction(
                id = "3",
                description = "Salaires Personnel Janvier",
                amount = 2450000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.SALARY,
                date = "25/01/2026"
            ),
            FinancialTransaction(
                id = "4",
                description = "Réparation Toiture Bloc A",
                amount = 75000.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.MAINTENANCE,
                date = "24/01/2026"
            ),
            FinancialTransaction(
                id = "5",
                description = "Inscription Nouvel Eleve",
                amount = 50000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.INSCRIPTION,
                date = "23/01/2026",
                reference = "REF-2026-002"
            ),
            FinancialTransaction(
                id = "6",
                description = "Facture CIE Janvier",
                amount = 42500.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.UTILITIES,
                date = "22/01/2026"
            ),
            FinancialTransaction(
                id = "7",
                description = "Paiement Scolarité Marie Kone",
                amount = 125000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.TUITION,
                date = "21/01/2026",
                reference = "REF-2026-003"
            )
        )

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        _state.value = _state.value.copy(
            transactions = transactions,
            summary = AccountingSummary(
                totalRevenue = totalIncome,
                totalExpenses = totalExpense,
                balance = totalIncome - totalExpense
            )
        )
    }
}
