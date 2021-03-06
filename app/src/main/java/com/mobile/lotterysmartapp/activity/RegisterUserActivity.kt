package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.util.PatternsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Provider
import com.mobile.lotterysmartapp.model.User
import com.mobile.lotterysmartapp.model.UserType
import kotlinx.android.synthetic.main.activity_register_user.*


class RegisterUserActivity : AppCompatActivity() {
    private val verifyPassword = "Verifique que la contraseña tenga más de 6 caracteres."
    private val verifyInputs = "Por favor verifique que todos los datos sean correctos."
    private val accountError =
        "Lo sentimos, su cuenta no se registró correctamente, por favor intente de nuevo."
    private val errorAlert = "¡Error!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Application started")
        analytics.logEvent("InitScreen", bundle)

        //setup register view
        setUp()
    }

    /**
     * Makes the setup for the button to register an user
     *
     * @author Jimena Vega
     *
     */
    private fun setUp() {
        //setup of registerUserButton
        val database = FirebaseDatabase.getInstance().getReference("User")
        val userType = UserType.BUYER.type
        buttonRegisterUser.setOnClickListener {
            try {
                register(database, userType)
            } catch (e: Exception) {
                println(e.message)
            }

        }
    }

    /**
     * Register an user
     *
     * @author Jimena Vega
     * @param database the reference to the database
     * @param userType the type of user can be: seller or buyer
     *
     */
    private fun register(database: DatabaseReference, userType: String) {
        if (verifyPasswordLength()) {
            if (verifyEmail() && verifyPasswords()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    textEmail.text.toString(),
                    textPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val id = getRandomString()
                        database.child(id).setValue(
                            User(
                                id,
                                textEmail.text.toString(),
                                textName.text.toString(),
                                textMiddleName.text.toString(),
                                userType,
                                0.0,
                                0.0
                            )
                        )
                        showHome(textEmail.text.toString(), Provider.APPLICATION, UserType.BUYER)
                        clearForm()
                    }
                }.addOnFailureListener {
                    alert(errorAlert, verifyInputs)
                }
            } else {
                alert(errorAlert, accountError)
            }
        } else {
            alert(errorAlert, verifyPassword)
        }
    }

    /**
     * Verifies that the email provided by the user its valid
     *
     * @author Jimena Vega
     */
    private fun verifyEmail(): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(textEmail.text).matches()
    }

    /**
     * Verifies that the password provided by the user has the amount of digits required
     *
     * @author Jimena Vega
     */
    private fun verifyPasswordLength(): Boolean {
        return textPassword.text.length > 6
    }

    /**
     * Verifies that the password and the confirmation of the password are the same
     *
     * @author Jimena Vega
     */
    private fun verifyPasswords(): Boolean {
        return textPassword.text.toString() == textConfirmPassword.text.toString()
    }

    /**
     * Show alert in case the new account could not be created.
     *
     * @param title a string with the title to show
     * @param message a string with the message to show
     *
     * @author Franklin Cardenas
     */
    private fun alert(title: String?, message: String?) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = alertBuilder.create()
        dialog.show()
    }

    /**
     * Clear the inputs from the form.
     *
     * @author Jimena Vega
     */
    private fun clearForm() {
        textName.text.clear()
        textMiddleName.text.clear()
        textEmail.text.clear()
        textPassword.text.clear()
        textConfirmPassword.text.clear()
    }

    /**
     * Makes a random sequence of String
     *
     * @author Jimena Vega
     */
    private fun getRandomString(): String {
        val length = 10
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    /**
     * Show home screen and pass it the user credentials.
     *
     * @author Franklin Cardenas
     * @param email user email
     * @param provider Account provider
     */
    private fun showHome(email: String?, provider: Provider, userType: UserType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider.toString())
            putExtra(Constants.USERTYPE, userType.name)
        }
        startActivity(homeIntent)
        finish()
    }

    /**
     * Send the user to the type of user view when the back button is used
     *
     * @author Jimena Vega
     */
    override fun onBackPressed() {
        clearForm()
        val intentToBack = Intent(this, TypeUserActivity::class.java)
        intentToBack.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToBack)
    }
}


