package com.blooddonation.management.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blooddonation.management.data.models.Category
import com.blooddonation.management.data.repository.BloodDonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: BloodDonationRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllCategories().collect { categories ->
                    _categories.value = categories
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "حدث خطأ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.addCategory(category)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل إضافة الصنف"
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تحديث الصنف"
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل حذف الصنف"
            }
        }
    }

    fun searchCategories(query: String) {
        viewModelScope.launch {
            try {
                repository.searchCategories(query).collect { results ->
                    _categories.value = results
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل البحث"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
