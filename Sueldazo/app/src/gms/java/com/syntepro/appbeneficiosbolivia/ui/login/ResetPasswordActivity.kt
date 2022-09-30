package com.syntepro.appbeneficiosbolivia.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.syntepro.appbeneficiosbolivia.R
import kotlinx.android.synthetic.gms.activity_reset_password.*

class ResetPasswordActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        btn_resetPass.setOnClickListener {
            progress_circular.visibility = View.VISIBLE
            btn_resetPass.visibility = View.GONE
            if (!validateEmail()) {
                progress_circular.visibility = View.GONE
                btn_resetPass.visibility = View.VISIBLE
            } else resetPassEmail(emailField.text.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun validateEmail(): Boolean {
        if (emailField.text.isNullOrEmpty()) return false
        val email = emailField.text.toString().trim { it <= ' ' }
        return if (email.isEmpty()) {
            emailLayout.error = getString(R.string.c_required)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = getString(R.string.e_invalid)
            false
        } else {
            emailLayout.error = null
            true
        }
    }

    private fun resetPassEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                emailLayout.editText!!.setText("")
                progress_circular.visibility = View.GONE
                btn_resetPass!!.visibility = View.VISIBLE
                Toast.makeText(applicationContext, getString(R.string.send_email), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                emailLayout.error = it.message ?: "Ocurrió un error."
                progress_circular.visibility = View.GONE
                btn_resetPass!!.visibility = View.VISIBLE
            }
    }
}