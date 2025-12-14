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
            val now = System.currentTimeMillis()
            val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000
            val expireDate = item.expireDate
            val context = binding.root.context

            binding.categoryName.text = item.categoryName
            binding.quantity.text = "${item.quantity} ${item.unit}"
            binding.lotNumber.text = item.lotNumber
            binding.expireDate.text = dateFormat.format(Date(expireDate))
            
            // Status Logic
            if (expireDate < now) {
                // Expired
                binding.cardView.setCardBackgroundColor(context.getColor(com.blooddonation.management.R.color.status_expired_bg))
                binding.cardView.strokeColor = context.getColor(com.blooddonation.management.R.color.status_expired_text)
                binding.status.text = "منتهي الصلاحية"
                binding.status.setTextColor(context.getColor(com.blooddonation.management.R.color.status_expired_text))
                binding.expireDate.setTextColor(context.getColor(com.blooddonation.management.R.color.status_expired_text))
            } else if (expireDate - now < thirtyDaysInMillis) {
                // Near Expiry
                binding.cardView.setCardBackgroundColor(context.getColor(com.blooddonation.management.R.color.status_near_expiry_bg))
                binding.cardView.strokeColor = context.getColor(com.blooddonation.management.R.color.status_near_expiry_text)
                binding.status.text = "قريب الانتهاء"
                binding.status.setTextColor(context.getColor(com.blooddonation.management.R.color.status_near_expiry_text))
                binding.expireDate.setTextColor(context.getColor(com.blooddonation.management.R.color.status_near_expiry_text))
            } else {
                // Valid
                binding.cardView.setCardBackgroundColor(context.getColor(com.blooddonation.management.R.color.white))
                binding.cardView.strokeColor = context.getColor(com.blooddonation.management.R.color.divider)
                binding.status.text = "صالح"
                binding.status.setTextColor(context.getColor(com.blooddonation.management.R.color.status_valid_text))
                binding.expireDate.setTextColor(context.getColor(com.blooddonation.management.R.color.text_primary))
            }

            if (item.notes.isNotEmpty()) {
                binding.notes.visibility = android.view.View.VISIBLE
                binding.notes.text = item.notes
            } else {
                binding.notes.visibility = android.view.View.GONE
            }

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
