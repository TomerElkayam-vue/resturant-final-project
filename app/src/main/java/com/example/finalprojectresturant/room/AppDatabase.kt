package com.example.finalprojectresturant.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.example.finalprojectresturant.data.reviews.ReviewsDao
import com.example.finalprojectresturant.data.users.UserModel
import com.example.finalprojectresturant.data.users.UsersDao

@Database(
    entities = [ReviewModel::class, UserModel::class], version = 4, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reviewsDao(): ReviewsDao
    abstract fun usersDao(): UsersDao
}