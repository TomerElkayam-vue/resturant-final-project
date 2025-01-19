package com.example.finalprojectresturant.data.reviews

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectresturant.data.users.UserModel

data class ReviewWithReviewer(
    @Embedded val review: ReviewModel,
    @Relation(
        entity = UserModel::class,
        parentColumn = "reviewer_id",
        entityColumn = "id"
    ) val reviewer: UserModel
) {
}