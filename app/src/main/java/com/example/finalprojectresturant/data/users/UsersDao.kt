package com.example.finalprojectresturant.data.users

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface UsersDao {
    @Query("SELECT * FROM users WHERE id LIKE :id LIMIT 1")
    suspend fun getUserByUid(id: String): UserModel?

    @Query("SELECT id FROM users WHERE id IN (:ids)")
    suspend fun getExistingUserIds(ids: List<String>): List<String>

    @Upsert
    fun upsertAll(vararg users: UserModel)

    @Query("DELETE FROM users WHERE id = :id")
    fun deleteByUid(id: String)

    @Update
    fun updateUserData(user: UserModel)
}