package com.blooddonation.management.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blooddonation.management.data.models.ReturnItem
import com.blooddonation.management.data.repository.BloodDonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReturnViewModel @Inject constructor(
    private val repository: BloodDonationRepository
) : ViewModel() {

    private val _returns = MutableStateFlow<List<ReturnItem>>(emptyList())
    val returns: StateFlow<List<ReturnItem>> = _returns.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadReturns()
    }

    fun loadReturns() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllReturns().collect { returns ->
                    _returns.value = returns
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "حدث خطأ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReturn(returnItem: ReturnItem) {
        viewModelScope.launch {
            try {
                repository.addReturn(returnItem)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تسجيل المرتجع"
            }
        }
    }

    fun updateReturn(returnItem: ReturnItem) {
        viewModelScope.launch {
            try {
                repository.updateReturn(returnItem)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تحديث المرتجع"
            }
        }
    }

    fun deleteReturn(returnItem: ReturnItem) {
        viewModelScope.launch {
            try {
                repository.deleteReturn(returnItem)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل حذف المرتجع"
            }
        }
    }

    fun searchReturns(query: String) {
        viewModelScope.launch {
            try {
                repository.searchReturns(query).collect { results ->
                    _returns.value = results
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
