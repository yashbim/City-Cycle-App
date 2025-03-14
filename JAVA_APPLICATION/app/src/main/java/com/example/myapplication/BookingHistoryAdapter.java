package com.example.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private List<Map<String, String>> bookings;

    public BookingHistoryAdapter(List<Map<String, String>> bookings) {
        this.bookings = bookings;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        public TextView stationName;
        public TextView startTime;
        public TextView duration;
        public TextView price;

        public BookingViewHolder(View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.textStation);
            startTime = itemView.findViewById(R.id.textStartTime);
            duration = itemView.findViewById(R.id.textDuration);
            price = itemView.findViewById(R.id.textPrice);
        }
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Map<String, String> booking = bookings.get(position);
        holder.stationName.setText("Station: " + booking.get("station"));
        holder.startTime.setText("Start Time: " + booking.get("startTime"));
        holder.duration.setText("Duration: " + booking.get("duration") + " mins");
        holder.price.setText("Price: $" + booking.get("price"));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
}
