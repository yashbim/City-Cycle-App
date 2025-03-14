package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder> {

    private List<Promotion> promotions;

    public PromotionsAdapter(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        public TextView promoTitle;
        public TextView promoDescription;
        public TextView promoDiscount;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            promoTitle = itemView.findViewById(R.id.textPromoTitle);
            promoDescription = itemView.findViewById(R.id.textPromoDescription);
            promoDiscount = itemView.findViewById(R.id.textPromoDiscount);
        }
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {
        Promotion promotion = promotions.get(position);
        holder.promoTitle.setText(promotion.getTitle());
        holder.promoDescription.setText(promotion.getDescription());
        holder.promoDiscount.setText("Discount: " + promotion.getDiscount());
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }
}