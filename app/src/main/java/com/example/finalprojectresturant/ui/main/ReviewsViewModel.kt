package com.example.finalprojectresturant.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.example.finalprojectresturant.data.reviews.ReviewWithReviewer
import com.example.finalprojectresturant.data.reviews.ReviewsRepository
import com.example.finalprojectresturant.data.reviews.UserDTO
import com.example.finalprojectresturant.data.users.UserModel
import com.example.finalprojectresturant.data.users.UsersRepository
import com.firebase.ui.auth.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

data class ReviewsUiState(val reviewId: String = "")

class ReviewsViewModel : ViewModel() {
    private val repository = ReviewsRepository()
    private val usersRepository = UsersRepository()

    fun getAllReviews(): LiveData<List<ReviewWithReviewer>> {
        return this.repository.getReviewsList()
    }

    fun getAllReviewsByUserId(id: String): LiveData<List<ReviewWithReviewer>?> {
        return this.repository.getReviewsByUserId(id)
    }

    fun reFetchReviews() {
        viewModelScope.launch {
            repository.deleteReviews()
            repository.loadReviewsFromFirebase()
        }
    }

    fun getUserById(id: String): LiveData<UserModel?> {
        val userLiveData = MutableLiveData<UserModel?>()
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                usersRepository.getUserByUid(id)
            }
            userLiveData.postValue(user)
        }
        return userLiveData
    }

    fun updateUser(
        user: UserModel,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            usersRepository.upsertUser(user)
        }
    }

    fun addReview(
        review: ReviewModel,
    ) {
            viewModelScope.launch(Dispatchers.Main) {
                repository.addReview(review)
            }
        }

    fun saveReview(
        review: ReviewModel,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.editReview(review)
        }
    }

    fun deleteReviewById(
        reviewId: String,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.deleteReviewById(reviewId)
        }
    }
}