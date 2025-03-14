package com.example.city_cycle_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PromotionsAdapter(private val promotions: List<Promotion>) :
    RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder>() {

    class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val promoTitle: TextView = itemView.findViewById(R.id.textPromoTitle)
        val promoDescription: TextView = itemView.findViewById(R.id.textPromoDescription)
        val promoDiscount: TextView = itemView.findViewById(R.id.textPromoDiscount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.promoTitle.text = promotion.title
        holder.promoDescription.text = promotion.description
        holder.promoDiscount.text = "Discount: ${promotion.discount}"
    }

    override fun getItemCount() = promotions.size
}
