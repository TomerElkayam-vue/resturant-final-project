package com.example.finalprojectresturant.data.reviews

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.finalprojectresturant.data.users.UserModel
import java.util.UUID

@Entity(
    tableName = "reviews",
    foreignKeys = [ForeignKey(
        entity = UserModel::class,
        parentColumns = ["id"],
        childColumns = ["reviewer_id"]
    )]
)
data class ReviewModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "reviewer_id") val reviewer_id: String,
    @ColumnInfo(name = "restaurant_name") val restaurant_name: String,
    @ColumnInfo(name = "image") val image : String? = null,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "review") val review: String
) {

    fun toReviewDto(): ReviewDTO {
        return ReviewDTO(
            id = id,
            restaurant_name = restaurant_name,
            reviewer_id = reviewer_id,
            image = image,
            rating = rating,
            review =  review
        )
    }
}


