package com.blooddonation.management.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blooddonation.management.databinding.FragmentCategoriesBinding
import com.blooddonation.management.data.models.Category
import com.blooddonation.management.ui.adapters.CategoryAdapter
import com.blooddonation.management.ui.viewmodels.CategoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddButton()
        observeCategories()
        observeLoading()
        observeErrors()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(emptyList(),
            onEdit = { category -> showEditDialog(category) },
            onDelete = { category -> viewModel.deleteCategory(category) }
        )
        binding.categoriesRecyclerView.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAddButton() {
        binding.addCategoryButton.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(com.blooddonation.management.R.layout.dialog_add_category, null)
        val nameInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.categoryNameInput)
        val descriptionInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.blooddonation.management.R.id.categoryDescriptionInput)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("إضافة صنف جديد")
            .setView(dialogView)
            .setPositiveButton("حفظ") { _, _ ->
                val name = nameInput.text.toString()
                val description = descriptionInput.text.toString()

                if (name.isNotEmpty()) {
                    val category = Category(name = name, description = description)
                    viewModel.addCategory(category)
                }
            }
            .setNegativeButton("إلغاء", null)
            .create()
        dialog.show()
    }

    private fun showEditDialog(category: Category) {
        // TODO: Implement edit dialog
    }

    private fun createCategoryInputView(): View {
        // TODO: Create input view for category
        return View(requireContext())
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                categoryAdapter.submitList(categories)
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
