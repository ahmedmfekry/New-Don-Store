package com.blooddonation.management.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blooddonation.management.databinding.FragmentDistributionBinding
import com.blooddonation.management.ui.adapters.DistributionAdapter
import com.blooddonation.management.ui.viewmodels.DistributionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DistributionFragment : Fragment() {

    private var _binding: FragmentDistributionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DistributionViewModel by viewModels()
    private lateinit var distributionAdapter: DistributionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistributionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddButton()
        setupSearchView()
        observeDistributions()
        observeLoading()
        observeErrors()
    }

    private fun setupRecyclerView() {
        distributionAdapter = DistributionAdapter(emptyList())
        binding.distributionRecyclerView.apply {
            adapter = distributionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAddButton() {
        binding.addDistributionButton.setOnClickListener {
            showAddDistributionDialog()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchDistributions(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadDistributions()
                }
                return true
            }
        })
    }

    private fun showAddDistributionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.blooddonation.management.R.layout.dialog_add_distribution, null)
        val itemSpinner = dialogView.findViewById<android.widget.AutoCompleteTextView>(com.blooddonation.management.R.id.itemSpinner)
        val quantityInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.quantityInput)
        val recipientInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.recipientInput)
        val notesInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.notesInput)

        // TODO: Populate itemSpinner with inventory items.

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("صرف من المخزون")
            .setView(dialogView)
            .setPositiveButton("حفظ") { _, _ ->
                val quantity = quantityInput.text.toString().toIntOrNull()
                val recipient = recipientInput.text.toString()
                val notes = notesInput.text.toString()

                if (quantity != null && recipient.isNotEmpty()) {
                    val distribution = com.blooddonation.management.data.models.Distribution(
                        inventoryId = 0, // Placeholder
                        categoryName = "Dispensed Item", // Placeholder
                        quantity = quantity,
                        unit = "Unit", // Placeholder
                        lotNumber = "N/A", // Placeholder
                        expireDate = System.currentTimeMillis(), // Placeholder
                        campaignName = recipient, // Using recipient input as campaign/recipient
                        notes = notes,
                        distributionDate = System.currentTimeMillis()
                    )
                    viewModel.addDistribution(distribution)
                }
            }
            .setNegativeButton("إلغاء", null)
            .create()
        dialog.show()
    }

    private fun observeDistributions() {
        lifecycleScope.launch {
            viewModel.distributions.collect { distributions ->
                distributionAdapter.submitList(distributions)
                binding.emptyStateText.visibility = if (distributions.isEmpty()) View.VISIBLE else View.GONE
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
