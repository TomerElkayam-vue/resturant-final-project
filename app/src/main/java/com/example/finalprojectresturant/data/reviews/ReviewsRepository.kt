package com.example.finalprojectresturant.data.reviews

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.finalprojectresturant.data.users.UsersRepository
import com.example.finalprojectresturant.room.DatabaseHolder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReviewsRepository() {
    private val reviewsDao = DatabaseHolder.getDatabase().reviewsDao()
    private val usersRepository = UsersRepository()
    private val firestoreHandle = Firebase.firestore.collection("reviews")

    suspend fun addReview(vararg reviews: ReviewModel) = withContext(Dispatchers.IO) {
        val batchHandle = Firebase.firestore.batch()
        reviews.forEach {
            batchHandle.set(firestoreHandle.document(it.id), it)
        }

        batchHandle.commit().await()
        reviewsDao.upsertAll(*reviews)
    }

    suspend fun editReview(review: ReviewModel) = withContext(Dispatchers.IO) {
        firestoreHandle.document(review.id).set(review).await()
        reviewsDao.update(review)
    }

    suspend fun deleteReviewById(id: String) = withContext(Dispatchers.IO) {
        firestoreHandle.document(id).delete().await()
        reviewsDao.deleteById(id)
    }

    fun getReviewsByUserId(id: String) : LiveData<List<ReviewWithReviewer>?> {
        return reviewsDao.findByUserId(id)
    }

    fun getReviewsList(): LiveData<List<ReviewWithReviewer>> {
        return reviewsDao.getAll()
    }

    suspend fun deleteReviews() = withContext(Dispatchers.IO)  {
        reviewsDao.deleteAll()
    }

    suspend fun syncReviewsBetweenFirebaseAnDao(firebaseReviews: List<ReviewModel>) {
        val remoteReviewsId = firebaseReviews.map { it.id }
        reviewsDao.insertOrUpdate(firebaseReviews)
        reviewsDao.deleteReviewsNotIn(remoteReviewsId)
    }

    suspend fun loadReviewsFromFirebase() =
        withContext(Dispatchers.IO) {
            val reviews = firestoreHandle
                .get().await().toObjects(ReviewDTO::class.java).map { it.toReviewModel() }

            usersRepository.cacheUsersIfNotExisting(reviews.map { it.reviewer_id })
            syncReviewsBetweenFirebaseAnDao(reviews)
        }
}