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
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadReviewsFromRemoteSource(50, 0)
        }
    }

    fun getAllReviews(): LiveData<List<ReviewWithReviewer>> {
        return this.repository.getReviewsList(50, 0, viewModelScope)
    }


    fun invalidateReviews() {
        viewModelScope.launch {
            repository.loadReviewsFromRemoteSource(50, 0)
        }
    }

}