package com.blooddonation.management.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.blooddonation.management.data.models.Distribution
import com.blooddonation.management.databinding.ItemDistributionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DistributionAdapter(
    items: List<Distribution>,
    private val onItemClick: (Distribution) -> Unit = {}
) : ListAdapter<Distribution, DistributionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDistributionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemDistributionBinding,
        private val onItemClick: (Distribution) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(distribution: Distribution) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))

            binding.categoryName.text = distribution.categoryName
            binding.campaignName.text = "الحملة: ${distribution.campaignName}"
            binding.quantity.text = "الكمية: ${distribution.quantity} ${distribution.unit}"
            binding.lotNumber.text = "LOT: ${distribution.lotNumber}"
            binding.distributionDate.text = "الصرف: ${dateFormat.format(Date(distribution.distributionDate))}"
            binding.status.text = distribution.status

            binding.root.setOnClickListener { onItemClick(distribution) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Distribution>() {
            override fun areItemsTheSame(oldItem: Distribution, newItem: Distribution) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Distribution, newItem: Distribution) =
                oldItem == newItem
        }
    }
}
