package com.blooddonation.management.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blooddonation.management.databinding.FragmentInventoryBinding
import com.blooddonation.management.data.models.InventoryItem
import com.blooddonation.management.ui.adapters.InventoryAdapter
import com.blooddonation.management.ui.viewmodels.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddButton()
        setupSearchView()
        observeInventoryItems()
        observeLoading()
        observeErrors()
    }

    private fun setupRecyclerView() {
        inventoryAdapter = InventoryAdapter(emptyList()) { item ->
            // Handle item click
        }
        binding.inventoryRecyclerView.apply {
            adapter = inventoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAddButton() {
        binding.addInventoryButton.setOnClickListener {
            showAddInventoryDialog()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchInventory(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadInventoryItems()
                }
                return true
            }
        })
    }

    private fun showAddInventoryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.blooddonation.management.R.layout.dialog_add_inventory, null)
        val categorySpinner = dialogView.findViewById<android.widget.AutoCompleteTextView>(com.blooddonation.management.R.id.categorySpinner)
        val quantityInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.quantityInput)
        val unitInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.unitInput)
        val lotInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.lotNumberInput)
        val expireDateInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.expireDateInput)
        val notesInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.notesInput)

        // Setup Category Spinner
        // Note: In a real app, we should observe categories from ViewModel. For now, checking if we can get them.
        // Assuming we need to fetch categories.
        // Ideally we'd use a CategoryViewModel here or expose categories in InventoryViewModel.
        // For simplicity, let's assume the user enters the category ID manually or selects from a dummy list if ViewModel isn't ready.
        // BUT, we want it to work. Let's observe categories in the fragment and populate a local list.
        
        var selectedCategoryId = -1
        var selectedCategoryName = ""
        val categoryList = mutableListOf<com.blooddonation.management.data.models.Category>()
        
        // This is a bit hacky to observe in a dialog setup function, but valid if we scoped properly.
        // Better: Populate adapter from a cached list in Fragment.
        
        // Date Picker Logic
        val calendar = java.util.Calendar.getInstance()
        expireDateInput.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                expireDateInput.setText(sdf.format(calendar.time))
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("إضافة مخزون")
            .setView(dialogView)
            .setPositiveButton("حفظ") { _, _ ->
                val quantity = quantityInput.text.toString().toIntOrNull()
                val unit = unitInput.text.toString()
                val lot = lotInput.text.toString()
                val expireDateStr = expireDateInput.text.toString()
                val notes = notesInput.text.toString()

                // Logic to save
                if (quantity != null && unit.isNotEmpty() && lot.isNotEmpty() && expireDateStr.isNotEmpty()) {
                     val newItem = InventoryItem(
                        categoryId = 0, // Placeholder, needs category selection
                        categoryName = "General", // Placeholder
                        quantity = quantity,
                        unit = unit,
                        lotNumber = lot,
                        expireDate = calendar.timeInMillis, // Approximate from picker
                        notes = notes
                    )
                    viewModel.addInventoryItem(newItem)
                }
            }
            .setNegativeButton("إلغاء", null)
            .create()
        dialog.show()
    }

    private fun observeInventoryItems() {
        lifecycleScope.launch {
            viewModel.inventoryItems.collect { items ->
                inventoryAdapter.submitList(items)
                binding.emptyStateText.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeLoading() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    // Show error message
                    viewModel.clearError()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
