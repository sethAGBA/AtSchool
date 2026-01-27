package com.ecolix.presentation.screens.inventory

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventoryScreenModel {
    private val _state = MutableStateFlow(InventoryUiState())
    val state: StateFlow<InventoryUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onViewModeChange(mode: InventoryViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun loadMockData() {
        val mockExpenses = listOf(
            Expense("E1", "Salaires Janvier - Enseignants", 4500000.0, ExpenseCategory.SALARIES, "25/01/2026", ExpenseStatus.PAID, "Personnel Enseignant"),
            Expense("E2", "Facture Électricité", 125000.0, ExpenseCategory.UTILITIES, "20/01/2026", ExpenseStatus.PENDING, "CIE"),
            Expense("E3", "Réparation Toiture Bloc B", 350000.0, ExpenseCategory.MAINTENANCE, "15/01/2026", ExpenseStatus.PAID, "Artisan Local"),
            Expense("E4", "Achat Craies et Marqueurs", 45000.0, ExpenseCategory.SUPPLIES, "10/01/2026", ExpenseStatus.PAID, "Librairie de France"),
            Expense("E5", "Abonnement Internet Mensuel", 25000.0, ExpenseCategory.UTILITIES, "28/01/2026", ExpenseStatus.PLANNED, "Orange CI")
        )

        val mockItems = listOf(
            InventoryItem("I1", "Bancs-Pupilles Biplaces", ItemCategory.FURNITURE, 120, 20, ItemCondition.GOOD, "Salles de Classe", "12/09/2023", 1200000.0),
            InventoryItem("I2", "Ordinateurs Portables HP", ItemCategory.ELECTRONICS, 15, 5, ItemCondition.NEW, "Salle Informatique", "15/12/2025", 4500000.0),
            InventoryItem("I3", "Ballons de Football", ItemCategory.SPORTS, 8, 10, ItemCondition.FAIR, "Magasin Sport", "05/10/2024", 80000.0),
            InventoryItem("I4", "Microscopes Binoculaires", ItemCategory.LABORATORY, 12, 2, ItemCondition.GOOD, "Labo SVT", "20/02/2024", 1800000.0),
            InventoryItem("I5", "Climatiseurs Split 1.5 CV", ItemCategory.ELECTRONICS, 6, 1, ItemCondition.POOR, "Bureaux Admin", "10/05/2021", 1500000.0)
        )

        val mockClassFinances = listOf(
            ClassFinanceSummary("C1", "6ème A", 8400000.0, 7200000.0, 1200000.0, 85.7f),
            ClassFinanceSummary("C2", "6ème B", 8000000.0, 6800000.0, 1200000.0, 85.0f),
            ClassFinanceSummary("C3", "5ème A", 7600000.0, 7000000.0, 6000000.0, 92.1f),
            ClassFinanceSummary("C4", "Terminale S", 9600000.0, 9000000.0, 600000.0, 93.7f)
        )

        val summary = BudgetSummary(
            totalBalance = 8500000.0,
            monthlyIncome = 12000000.0,
            monthlyExpenses = 7500000.0,
            projectedBudget = 4500000.0
        )

        _state.value = _state.value.copy(
            expenses = mockExpenses,
            items = mockItems,
            classFinances = mockClassFinances,
            budgetSummary = summary
        )
    }
}
