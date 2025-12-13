package com.blooddonation.management.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blooddonation.management.data.models.Distribution
import com.blooddonation.management.data.repository.BloodDonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DistributionViewModel @Inject constructor(
    private val repository: BloodDonationRepository
) : ViewModel() {

    private val _distributions = MutableStateFlow<List<Distribution>>(emptyList())
    val distributions: StateFlow<List<Distribution>> = _distributions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDistributions()
    }

    fun loadDistributions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllDistributions().collect { distributions ->
                    _distributions.value = distributions
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "حدث خطأ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addDistribution(distribution: Distribution) {
        viewModelScope.launch {
            try {
                repository.addDistribution(distribution)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تسجيل الصرف"
            }
        }
    }

    fun updateDistribution(distribution: Distribution) {
        viewModelScope.launch {
            try {
                repository.updateDistribution(distribution)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تحديث الصرف"
            }
        }
    }

    fun deleteDistribution(distribution: Distribution) {
        viewModelScope.launch {
            try {
                repository.deleteDistribution(distribution)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل حذف الصرف"
            }
        }
    }

    fun searchDistributions(query: String) {
        viewModelScope.launch {
            try {
                repository.searchDistributions(query).collect { results ->
                    _distributions.value = results
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
