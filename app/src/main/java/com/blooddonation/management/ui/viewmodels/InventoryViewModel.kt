package com.blooddonation.management.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blooddonation.management.data.models.InventoryItem
import com.blooddonation.management.data.repository.BloodDonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: BloodDonationRepository
) : ViewModel() {

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    private val _expiringItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val expiringItems: StateFlow<List<InventoryItem>> = _expiringItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInventoryItems()
        loadExpiringItems()
    }

    fun loadInventoryItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllInventoryItems().collect { items ->
                    _inventoryItems.value = items
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "حدث خطأ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadExpiringItems() {
        viewModelScope.launch {
            try {
                // البحث عن الأصناف التي ستنتهي صلاحيتها خلال شهر (30 يوم)
                val futureDate = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000)
                repository.getExpiringItems(futureDate).collect { items ->
                    _expiringItems.value = items
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تحميل الأصناف القريبة من الانتهاء"
            }
        }
    }

    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                repository.addInventoryItem(item)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل إضافة المخزون"
            }
        }
    }

    fun updateInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                repository.updateInventoryItem(item)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل تحديث المخزون"
            }
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                repository.deleteInventoryItem(item)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "فشل حذف المخزون"
            }
        }
    }

    fun searchInventory(query: String) {
        viewModelScope.launch {
            try {
                repository.searchInventory(query).collect { results ->
                    _inventoryItems.value = results
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
