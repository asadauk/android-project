package com.app.softwarehousemanager.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthService {
    private lateinit var auth: FirebaseAuth


    fun loginUser(email: String, password: String, loginResp: (Boolean) -> Unit, ) {
          auth = Firebase.auth
          auth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      // Sign in success, update UI with the signed-in user's information
                      Log.d(TAG, "loginUserWithEmail:success")
                      loginResp(true)
                  } else {
                      loginResp(false)
                  }
              }

    }

    fun registerUser(email: String, password: String, name:String, registerResp: (Boolean) -> Unit){
          auth = Firebase.auth
          auth.createUserWithEmailAndPassword(email, password)

              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      // Sign in success, update UI with the signed-in user's information
                      Log.d(TAG, "createUserWithEmail:success")
                      val user = auth.currentUser
                      saveUserInDb(name, email, user!!.uid)
                      registerResp(true)
                  } else {
                      Log.d(TAG, "createUserWithEmail:failed")
                      Log.d(TAG, task.exception.toString())

                      registerResp(false)
                  }
      }
    }

    private fun saveUserInDb(name: String, email: String, uid: String) {
        UserService().saveUserData(name = name, email= email, uid=uid) {
            if (it) {
                Log.d("usersaved", "user save success")
            }
        }
    }

    fun getCurrentUserId() :String?{
        auth = Firebase.auth

        return auth.currentUser?.uid
    }

    fun logoutUser(loggedOut: () -> Unit) {
        auth = Firebase.auth
        auth.signOut()
        loggedOut()
    }
}