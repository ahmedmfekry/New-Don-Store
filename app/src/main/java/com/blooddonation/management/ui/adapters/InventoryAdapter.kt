package com.blooddonation.management.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.blooddonation.management.data.models.InventoryItem
import com.blooddonation.management.databinding.ItemInventoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryAdapter(
    items: List<InventoryItem>,
    private val onItemClick: (InventoryItem) -> Unit = {}
) : ListAdapter<InventoryItem, InventoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemInventoryBinding,
        private val onItemClick: (InventoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))

            binding.categoryName.text = item.categoryName
            binding.quantity.text = "الكمية: ${item.quantity} ${item.unit}"
            binding.lotNumber.text = "LOT: ${item.lotNumber}"
            binding.expireDate.text = "ينتهي: ${dateFormat.format(Date(item.expireDate))}"
            binding.status.text = item.status
            binding.notes.text = item.notes

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<InventoryItem>() {
            override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
                oldItem == newItem
        }
    }
}
