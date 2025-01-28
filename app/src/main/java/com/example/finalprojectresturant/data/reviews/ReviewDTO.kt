package com.example.finalprojectresturant.data.reviews

import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.google.firebase.firestore.GeoPoint

data class ReviewDTO(
    val restaurant_name: String = "",
    val rating: Int = 0,
    val image: String? = null,
    val review: String? = null,
    val reviewer_id: String? = null,
    val country: String? = null,
    val id: String? = null
) {
    fun toReviewModel(): ReviewModel {
        return ReviewModel(
            id=id ?: "",
            restaurant_name = restaurant_name,
            rating = rating,
            review = review ?: "",
            reviewer_id = reviewer_id ?: "",
            image = image,
            country = country
        )
    }
}