package com.mobile.lotterysmartapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.util.AlertUtil
import kotlinx.android.synthetic.main.activity_forget_password.*
import java.lang.Exception

/**
 * Class in charge of Forget Password Activity.
 *
 * @author Franklin Cardenas
 */
class ForgetPasswordActivity : AppCompatActivity() {

    private val alertUtil = AlertUtil()
    private val success = "Proceso completado"
    private val error = "Error"
    private val checkEmail =
        "Revise su correo para seguir con el proceso de reestablecimiento de contraseña."
    private val couldNotRestartPawword =
        "No se pudo realizar el proceso de restableblecimiento de contraseña. Intente de nuevo."
    private val notExistingUser = "Ingrese un usuario existente."
    private val unregisteredUser = "El usuario no esta registrado en Lottery Smart."
    private val badlyFormattedEmail = "El correo ingresado no tiene un formato válido"

    /**
     * On Create method for Authentication Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        //Setup activity
        setup()
    }

    /**
     * Setup process for restoring password.
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Login";

        //Setup Restore Password button
        buttonRestorePassword.setOnClickListener {
            if (!editTextEmailForgetPassword.text.isNullOrBlank()) {
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(editTextEmailForgetPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            alertUtil.simpleAlert(success, checkEmail, this)
                        } else {
                            displayError(it.exception!!)
                        }
                    }
            } else {
                alertUtil.simpleAlert(error, notExistingUser, this)
            }
        }
    }

    /**
     * Error handler when Password reset email is not successful.
     *
     * @param exception - exception received
     * @author Franklin Cardenas
     */
    private fun displayError(exception : Exception) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> alertUtil.simpleAlert(
                error,
                unregisteredUser,
                this
            )
            is FirebaseAuthInvalidCredentialsException -> alertUtil.simpleAlert(
                error,
                badlyFormattedEmail,
                this
            )
            else -> alertUtil.simpleAlert(error, couldNotRestartPawword, this
            )
        }
    }
}