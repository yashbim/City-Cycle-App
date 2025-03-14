package com.example.city_cycle_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingHistoryAdapter(private val bookings: List<Map<String, String>>) :
    RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationName: TextView = itemView.findViewById(R.id.textStation)
        val startTime: TextView = itemView.findViewById(R.id.textStartTime)
        val duration: TextView = itemView.findViewById(R.id.textDuration)
        val price: TextView = itemView.findViewById(R.id.textPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.stationName.text = "Station: ${booking["station"]}"
        holder.startTime.text = "Start Time: ${booking["startTime"]}"
        holder.duration.text = "Duration: ${booking["duration"]} mins"
        holder.price.text = "Price: $${booking["price"]}"
    }

    override fun getItemCount() = bookings.size
}
