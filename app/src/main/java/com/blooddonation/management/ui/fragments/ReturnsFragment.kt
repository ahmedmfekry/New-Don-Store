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
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("إضافة مرتجع")
            .setMessage("سيتم فتح نموذج إضافة المرتجعات")
            .setPositiveButton("تابع") { _, _ ->
                // TODO: Open add return dialog
            }
            .setNegativeButton("إلغاء", null)
            .show()
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
