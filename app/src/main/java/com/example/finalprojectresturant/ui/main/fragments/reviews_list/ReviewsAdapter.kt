package com.example.finalprojectresturant.ui.main.fragments.reviews_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectresturant.R
import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.example.finalprojectresturant.data.reviews.ReviewWithReviewer
import com.example.finalprojectresturant.ui.main.fragments.review_details.ReviewDetailsFragment
import com.example.finalprojectresturant.utils.decodeBase64ToImage

class ReviewsAdapter(val onReviewClicked: (String) -> Unit) :
    RecyclerView.Adapter<ReviewsAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.resturant_name)
        val reviewerId: TextView = itemView.findViewById(R.id.reviewer_id)
        val restaurantRating  : RatingBar = itemView.findViewById(R.id.resturant_rating)
        val resturantImage : ImageView = itemView.findViewById(R.id.review_image)
    }

    private var reviews: List<ReviewWithReviewer> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_list_item, parent, false)
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val currentReview = reviews[position]
        holder.reviewerId.text = currentReview.reviewer.name
        holder.restaurantName.text = currentReview.review.restaurant_name

        holder.restaurantRating.rating = currentReview.review.rating.toFloat()

        currentReview.review.image?.let {
            val bitmap = decodeBase64ToImage(it)
            holder.resturantImage.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener {
            onReviewClicked(currentReview.review.id)

        }
    }

    fun updateReviews(newReviews: List<ReviewWithReviewer>) {
        this.reviews = newReviews
        notifyDataSetChanged();
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}