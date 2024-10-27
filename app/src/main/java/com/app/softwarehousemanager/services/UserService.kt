package com.app.softwarehousemanager.services

import com.app.softwarehousemanager.models.User
import com.app.softwarehousemanager.models.UserGroup
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlin.coroutines.suspendCoroutine

class UserService {
    private lateinit var database: DatabaseReference

    fun saveUserData(name: String, email: String, uid: String, saveResp: (Boolean) -> Unit){
        database = Firebase.database.reference
        val user = User(
            userId = uid,
            name =name,
            email=email
        )
        database.child("users").child(user.userId).setValue(user)
            .addOnSuccessListener {

                saveResp(true)
            }
            .addOnFailureListener {
                saveResp(false)
            }
    }

    fun getAllUsers(onUsersReceived: (List<User>) -> Unit, onError: (DatabaseError) -> Unit) {
        database = Firebase.database.reference

        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if(user.userId != AuthService().getCurrentUserId()) {
                            userList.add(it)
                        }
                    }
                }

                onUsersReceived(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }
}