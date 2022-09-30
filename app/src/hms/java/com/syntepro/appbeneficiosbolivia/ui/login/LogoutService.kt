package com.syntepro.appbeneficiosbolivia.ui.login

import com.anotherdev.firebase.auth.FirebaseAuthRest
import com.google.firebase.FirebaseApp

object LogoutService {
    fun firebaseLogout() {
        val app = FirebaseApp.getInstance()
        val mAuth = FirebaseAuthRest.getInstance(app)
        mAuth.signOut()
    }
}