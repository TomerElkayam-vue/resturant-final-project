package com.example.finalprojectresturant.data.reviews

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface ReviewsDao {
    @Query("SELECT * FROM reviews")
    fun getAll(): LiveData<List<ReviewWithReviewer>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    fun findById(id: String): LiveData<ReviewWithReviewer?>

    @Query("SELECT * FROM reviews WHERE reviewer_id = :id")
    fun findByUserId(id: String): LiveData<List<ReviewWithReviewer>?>

    @Upsert
    fun upsertAll(vararg review: ReviewModel)

    @Delete
    fun delete(review: ReviewModel)

    @Query("DELETE FROM reviews")
    fun deleteAll()

    @Query("DELETE FROM reviews WHERE id = :id")
    fun deleteById(id: String)

    @Update
    fun update(review: ReviewModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(posts: List<ReviewModel>)

    @Query("DELETE FROM reviews WHERE id NOT IN (:reviwsId)")
    suspend fun deleteReviewsNotIn(reviwsId: List<String>)

    @Query("SELECT * FROM reviews")
    suspend fun getAllReviews(): List<ReviewModel>
}