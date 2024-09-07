package com.example.rentalapp.data

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class UserViewModel: ViewModel() {
    private var _currentUser: FirebaseUser? = null
    val currentUser: FirebaseUser?
        get() = _currentUser

    fun setCurrentUser(user: FirebaseUser?) {
        _currentUser = user
    }
}