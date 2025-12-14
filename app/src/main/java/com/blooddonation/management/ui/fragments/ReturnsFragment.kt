package com.blooddonation.management.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blooddonation.management.databinding.FragmentReturnsBinding
import com.blooddonation.management.ui.adapters.ReturnAdapter
import com.blooddonation.management.ui.viewmodels.ReturnViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReturnsFragment : Fragment() {

    private var _binding: FragmentReturnsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReturnViewModel by viewModels()
    private lateinit var returnAdapter: ReturnAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReturnsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddButton()
        setupSearchView()
        observeReturns()
        observeLoading()
        observeErrors()
    }

    private fun setupRecyclerView() {
        returnAdapter = ReturnAdapter(emptyList())
        binding.returnsRecyclerView.apply {
            adapter = returnAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAddButton() {
        binding.addReturnButton.setOnClickListener {
            showAddReturnDialog()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchReturns(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadReturns()
                }
                return true
            }
        })
    }

    private fun showAddReturnDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.blooddonation.management.R.layout.dialog_add_return, null)
        val itemSpinner = dialogView.findViewById<android.widget.AutoCompleteTextView>(com.blooddonation.management.R.id.itemSpinner)
        val quantityInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.quantityInput)
        val reasonInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.reasonInput)

        // TODO: Populate itemSpinner with inventory items.

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("إضافة مرتجع")
            .setView(dialogView)
            .setPositiveButton("حفظ") { _, _ ->
                val quantity = quantityInput.text.toString().toIntOrNull()
                val reason = reasonInput.text.toString()

                if (quantity != null && reason.isNotEmpty()) {
                    val returnItem = com.blooddonation.management.data.models.ReturnItem(
                        itemId = 0, // Placeholder
                        itemName = "Returned Item", // Placeholder
                        quantity = quantity,
                        reason = reason,
                        date = System.currentTimeMillis()
                    )
                    viewModel.addReturn(returnItem)
                }
            }
            .setNegativeButton("إلغاء", null)
            .create()
        dialog.show()
    }

    private fun observeReturns() {
        lifecycleScope.launch {
            viewModel.returns.collect { returns ->
                returnAdapter.submitList(returns)
                binding.emptyStateText.visibility = if (returns.isEmpty()) View.VISIBLE else View.GONE
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
