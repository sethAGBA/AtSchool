package com.ecolix.presentation.screens.categories

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.data.models.Category
import com.ecolix.data.models.CategoriesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CategoriesScreenModel : ScreenModel {
    private val _state = MutableStateFlow(CategoriesUiState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        // Mock data fetch - replace with DatabaseService call later
        val mockCategories = listOf(
            Category("1", "Scientifique", "Sciences exactes", "#3B82F6", 1),
            Category("2", "Littéraire", "Langues et littérature", "#EC4899", 2),
            Category("3", "Général", "Matières communes", "#6366F1", 3),
            Category("4", "Sports", "Education physique", "#10B981", 4)
        )
        
        _state.update { it.copy(categories = mockCategories) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onSelectCategory(category: Category?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun saveCategory(category: Category) {
        // Mock save - replace with DatabaseService call
        val currentCategories = _state.value.categories.toMutableList()
        val index = currentCategories.indexOfFirst { it.id == category.id }
        
        if (index >= 0) {
            currentCategories[index] = category
        } else {
            currentCategories.add(category.copy(id = "CAT_${Random.nextInt(1000, 9999)}"))
        }

        _state.update { it.copy(
            categories = currentCategories.sortedBy { c -> c.order },
            selectedCategory = null
        )}
    }

    fun deleteCategory(categoryId: String) {
        // Mock delete
        val currentCategories = _state.value.categories.filterNot { it.id == categoryId }
        _state.update { it.copy(categories = currentCategories) }
    }

    fun onDarkModeChange(isDark: Boolean) {
        _state.update { it.copy(isDarkMode = isDark) }
    }
}
