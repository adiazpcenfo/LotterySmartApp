package com.mobile.lotterysmartapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mobile.lotterysmartapp.model.Provider
import kotlinx.android.synthetic.main.activity_authentication.*
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        //Setup activity
        setup()
    }

    /**
     * Setup process for restoring password
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Login"

        //Setup Restore Password button
        buttonRestorePassword.setOnClickListener {
            if (!editTextEmailForgetPassword.text.isNullOrBlank()) {
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(editTextEmailForgetPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            alert(
                                "Realizado!",
                                "Revise su correo para seguir con el proceso de reestablecimiento de contraseña."
                            )
                            this.finish()
                        } else {
                            alert(
                                "Error",
                                "No se pudo realizar el proceso de restableblecimiento de contraseña. Intente de nuevo."
                            )
                        }
                    }
            } else {
                alert("Error", "Ingrese un usuario existente.")
            }
        }
    }

    /**
     * Create an alert.
     *
     * @param title String
     * @param message String
     * @author Franklin Cardenas
     */
    private fun alert(title : String?, message : String?) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = alertBuilder.create()
        dialog.show()
    }
}