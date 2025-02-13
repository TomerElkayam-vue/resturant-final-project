package com.example.finalprojectresturant.data.reviews

import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.example.finalprojectresturant.data.users.UserModel
import com.google.firebase.firestore.GeoPoint

data class UserDTO(

    val email: String = "",
    val name: String = "",
    val profile_picture : String? = null,
    val id: String? = null
) {
    fun toUserModel(): UserModel {
        return UserModel(
            id=id ?: "",
            email = email,
            name = name,
            profile_picture = profile_picture
        )
    }
}