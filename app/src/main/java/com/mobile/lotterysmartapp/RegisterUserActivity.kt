package com.mobile.lotterysmartapp

import android.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.util.PatternsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobile.lotterysmartapp.model.User
import com.mobile.lotterysmartapp.model.userType
import kotlinx.android.synthetic.main.activity_register_user.*


class RegisterUserActivity : AppCompatActivity() {
    private val ACCOUNT_SUCCESS = "Su cuenta se registró correctamente."
    private val VERIFY_PASSWORDS = "Verifique que la contraseña tenga más de 6 caracteres."
    private val VERIFY_INPUTS = "Por favor verifique que todos los datos sean correctos."
    private val ACCOUNT_ERROR = "Lo sentimos, su cuenta no se registró correctamente, por favor intente de nuevo."
    private val ERROR = "¡Error!"
    private val SUCCES = "¡Éxito!"

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
        val userType =userType.BUYER.type
        buttonRegisterUser.setOnClickListener {
            try {
                register(database, userType)
            }catch (e : Exception){
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
    private fun register(database: DatabaseReference, userType: String){
       if(verifyPasswordLength()){
           if(verifyEmail() && verifyPasswords()){
               FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                   textEmail.text.toString(),
                   textPassword.text.toString()
               ).addOnCompleteListener {
                   if (it.isSuccessful) {
                       database.child(getRandomString(10)).setValue(
                           User(
                               textEmail.text.toString(),
                               textName.text.toString(),
                               textMiddleName.text.toString(),
                               userType,
                               "0",
                               "0"
                           )
                       )
                       alert(SUCCES, ACCOUNT_SUCCESS)
                   }
               }.addOnFailureListener{
                   alert(ERROR, VERIFY_INPUTS)
               }
           }else{
               alert(ERROR, ACCOUNT_ERROR )
           }
       }else{
           alert(ERROR, VERIFY_PASSWORDS)
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
    private fun verifyPasswordLength(): Boolean{
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
     * Makes a random sequence of Strings
     *
     * @param length length of the sequence of Strings
     *
     * @author Jimena Vega
     */
    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }
}


