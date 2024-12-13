package com.dicoding.retinova.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.retinova.databinding.ItemAlarmBinding

class AlarmAdapter(
    private val onDeleteClickListener: (AlarmItem) -> Unit
) : ListAdapter<AlarmItem, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    class AlarmViewHolder(
        private val binding: ItemAlarmBinding,
        private val onDeleteClickListener: (AlarmItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: AlarmItem) {
            binding.tvMedicineName.text = alarm.medicineName
            binding.tvAlarmTime.text = String.format("%02d:%02d", alarm.hour, alarm.minute)
            binding.tvMealTiming.text = alarm.mealTiming

            binding.btnDelete.setOnClickListener {
                onDeleteClickListener(alarm)
            }
        }
    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<AlarmItem>() {
        override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding, onDeleteClickListener)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}