package com.blooddonation.management.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.blooddonation.management.data.models.ReturnItem
import com.blooddonation.management.databinding.ItemReturnBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReturnAdapter(
    items: List<ReturnItem>,
    private val onItemClick: (ReturnItem) -> Unit = {}
) : ListAdapter<ReturnItem, ReturnAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemReturnBinding,
        private val onItemClick: (ReturnItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(returnItem: ReturnItem) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))

            binding.categoryName.text = returnItem.categoryName
            binding.quantity.text = "الكمية: ${returnItem.quantity} ${returnItem.unit}"
            binding.lotNumber.text = "LOT: ${returnItem.lotNumber}"
            binding.returnDate.text = "الإرجاع: ${dateFormat.format(Date(returnItem.returnDate))}"
            binding.returnReason.text = "السبب: ${returnItem.reason}"
            binding.notes.text = returnItem.notes

            binding.root.setOnClickListener { onItemClick(returnItem) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReturnItem>() {
            override fun areItemsTheSame(oldItem: ReturnItem, newItem: ReturnItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ReturnItem, newItem: ReturnItem) =
                oldItem == newItem
        }
    }
}
