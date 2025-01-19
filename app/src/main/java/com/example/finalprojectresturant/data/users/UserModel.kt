package com.example.finalprojectresturant.data.users

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.finalprojectresturant.data.reviews.UserDTO
import com.google.firebase.auth.FirebaseAuth

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey val id: String = "",
    val name: String,
    val email: String,
    val profile_picture: String?
) {
    fun toUserDto() : UserDTO {
        return UserDTO(
            id = id,
            name = name,
            email = email,
            profilePicture = profile_picture
        )
    }
    companion object {
        fun fromFirebaseAuth(): UserModel {
            val user = FirebaseAuth.getInstance().currentUser

            return UserModel(
                id = user?.uid!!,
                email = user.email!!,
                name = user.displayName!!,
                profile_picture = ""
            )
        }
    }
}
