package com.example.bookinghotel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookinghotel.R
import com.example.bookinghotel.model.Review

class ReviewAdapter(private var data: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.text.text = item.review
        holder.ratingBar.rating = item.totalStarGiven!!.toFloat()
        holder.date.text = item.Date.toString()

    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_nameReview)
        val text: TextView = itemView.findViewById(R.id.tv_textReview)
        val ratingBar: RatingBar = itemView.findViewById(R.id.total_star_rating)
        val date: TextView = itemView.findViewById(R.id.tv_dateReview)

    }

}