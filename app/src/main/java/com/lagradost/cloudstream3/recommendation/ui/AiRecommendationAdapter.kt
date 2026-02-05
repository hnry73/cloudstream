package com.lagradost.cloudstream3.recommendation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lagradost.cloudstream3.databinding.ItemAiRecommendationBinding
import com.lagradost.cloudstream3.recommendation.logic.RecommendationResult

class AiRecommendationAdapter(
    private val onClick: (RecommendationResult) -> Unit
) : RecyclerView.Adapter<AiRecommendationAdapter.AiRecommendationViewHolder>() {
    private val items = mutableListOf<RecommendationResult>()

    fun submitList(data: List<RecommendationResult>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiRecommendationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AiRecommendationViewHolder(ItemAiRecommendationBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AiRecommendationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AiRecommendationViewHolder(
        private val binding: ItemAiRecommendationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendationResult) {
            binding.aiRecommendationTitle.text = item.item.name
            binding.aiRecommendationExplanation.text = item.explanation
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
