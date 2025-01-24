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
        Log.i("fafa", review.id)
        firestoreHandle.document(review.id).set(review).await()
        reviewsDao.update(review)
    }

    suspend fun deleteReviewById(id: String) = withContext(Dispatchers.IO) {
        firestoreHandle.document(id).delete().await()
        reviewsDao.deleteById(id)
    }

    fun getReviewById(id: String): LiveData<ReviewWithReviewer?> {
        return reviewsDao.findById(id)
    }

    fun getReviewsByUserId(id: String) : LiveData<List<ReviewWithReviewer>?> {
        return reviewsDao.findByUserId(id)
    }

    fun getReviewsList(
        limit: Int,
        offset: Int,
        scope: CoroutineScope
    ): LiveData<List<ReviewWithReviewer>> {
        return reviewsDao.getAllPaginated(limit, offset)
    }


    suspend fun loadReviewFromRemoteSource(id: String) = withContext(Dispatchers.IO) {
        val review =
            firestoreHandle.document(id).get().await().toObject(ReviewDTO::class.java)?.toReviewModel()
        if (review != null) {
            reviewsDao.upsertAll(review)
            usersRepository.cacheUserIfNotExisting(review.reviewer_id)
        }

        return@withContext reviewsDao.findById(id)
    }

    suspend fun loadReviewsFromRemoteSource(limit: Int, offset: Int) =

        withContext(Dispatchers.IO) {
         
            val reviews = firestoreHandle.orderBy("review").startAt(offset).limit(limit.toLong())
                .get().await().toObjects(ReviewDTO::class.java).map { it.toReviewModel() }
       
            if (reviews.isNotEmpty()) {
                usersRepository.cacheUsersIfNotExisting(reviews.map { it.reviewer_id })
                reviewsDao.upsertAll(*reviews.toTypedArray())

            }
        }
}