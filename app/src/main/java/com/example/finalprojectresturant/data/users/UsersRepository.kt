package com.example.finalprojectresturant.data.users

import android.util.Log
import com.example.finalprojectresturant.data.reviews.UserDTO
import com.example.finalprojectresturant.room.DatabaseHolder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsersRepository {
    private val usersDao = DatabaseHolder.getDatabase().usersDao()
    private val firestoreHandle = Firebase.firestore.collection("users")

    suspend fun upsertUser(user: UserModel) = withContext(Dispatchers.IO) {
        firestoreHandle.document(user.id).set(user).await()
        usersDao.upsertAll(user)
    }

    suspend fun cacheUsersIfNotExisting(uids: List<String>) = withContext(Dispatchers.IO) {
        val existingUserIds = usersDao.getExistingUserIds(uids)
        val nonExistingUserIds = uids.filter { !existingUserIds.contains(it) }
   
        this@UsersRepository.getUsersFromFirebase(nonExistingUserIds)
    }

    suspend fun getUserByUid(uid: String): UserModel? = withContext(Dispatchers.IO) {
        val cachedResult = usersDao.getUserByUid(uid)
        if (cachedResult != null) return@withContext cachedResult

        return@withContext this@UsersRepository.getUserFromFirebase(uid)
    }

    private suspend fun getUserFromFirebase(uid: String): UserModel? =
        withContext(Dispatchers.IO) {
            val user =
                firestoreHandle.document(uid).get().await().toObject(UserDTO::class.java)?.toUserModel()
            if (user != null) {
                usersDao.upsertAll(user)
            }
            return@withContext user
        }

    suspend fun getUsersFromFirebase(uids: List<String>): List<UserModel> =
        withContext(Dispatchers.IO) {
            val usersQuery =
                if (uids.isNotEmpty()) firestoreHandle.whereIn("id", uids) else firestoreHandle
            val users = usersQuery.get().await().toObjects(UserDTO::class.java).map {it.toUserModel()}
            if (users.isNotEmpty()) {
                usersDao.upsertAll(*users.toTypedArray())
            }
            return@withContext users
        }
}