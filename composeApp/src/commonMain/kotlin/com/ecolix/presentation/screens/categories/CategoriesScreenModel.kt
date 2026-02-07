package com.ecolix.presentation.screens.categories

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.AcademicApiService
import com.ecolix.atschool.api.SubjectCategoryDto
import com.ecolix.data.models.Category
import com.ecolix.data.models.CategoriesUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesScreenModel(
    private val academicApiService: AcademicApiService
) : StateScreenModel<CategoriesUiState>(CategoriesUiState()) {

    init {
        loadCategories()
    }

    fun loadCategories() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            academicApiService.getAllCategories().onSuccess { dtos ->
                mutableState.update { it.copy(
                    categories = dtos.map { dto -> dto.toCategory() }.sortedBy { it.order },
                    isLoading = false
                ) }
            }.onFailure {
                mutableState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        mutableState.update { it.copy(searchQuery = query) }
    }

    fun onSelectCategory(category: Category?) {
        mutableState.update { it.copy(selectedCategory = category) }
    }

    fun saveCategory(category: Category) {
        screenModelScope.launch {
            val dto = category.toDto()
            val result = if (category.id.isEmpty() || category.id.startsWith("CAT_")) {
                academicApiService.createCategory(dto)
            } else {
                academicApiService.updateCategory(category.id.toInt(), dto)
            }
            
            result.onSuccess {
                loadCategories()
                mutableState.update { it.copy(selectedCategory = null) }
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        screenModelScope.launch {
            val idInt = categoryId.toIntOrNull() ?: return@launch
            academicApiService.deleteCategory(idInt).onSuccess {
                loadCategories()
            }
        }
    }

    fun seedDefaultCategories() {
        println("CategoriesScreenModel: Requesting default categories seeding...")
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            academicApiService.seedDefaultCategories().onSuccess {
                println("CategoriesScreenModel: Seeding successful, refreshing list.")
                loadCategories()
            }.onFailure { e ->
                println("CategoriesScreenModel: Seeding failed: ${e.message}")
                e.printStackTrace()
                mutableState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(isDarkMode = isDark) }
    }

    // Mappings
    private fun SubjectCategoryDto.toCategory() = Category(
        id = id?.toString() ?: "",
        name = name,
        description = description,
        colorHex = colorHex,
        order = sortOrder
    )

    private fun Category.toDto() = SubjectCategoryDto(
        id = id.toIntOrNull(),
        name = name,
        description = description,
        colorHex = colorHex,
        sortOrder = order
    )
}
