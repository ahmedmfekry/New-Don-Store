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
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("إضافة مخزون")
            .setMessage("سيتم فتح نموذج إضافة المخزون")
            .setPositiveButton("تابع") { _, _ ->
                // TODO: Open add inventory dialog
            }
            .setNegativeButton("إلغاء", null)
            .show()
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
