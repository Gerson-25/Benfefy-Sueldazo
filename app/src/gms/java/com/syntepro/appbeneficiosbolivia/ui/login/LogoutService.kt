package com.syntepro.appbeneficiosbolivia.ui.login

import com.google.firebase.auth.FirebaseAuth

object LogoutService {
    fun firebaseLogout() {
        FirebaseAuth.getInstance().signOut()
    }
}