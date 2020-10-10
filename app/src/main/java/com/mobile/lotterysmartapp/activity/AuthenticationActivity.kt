package com.mobile.lotterysmartapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Provider
import com.mobile.lotterysmartapp.util.AlertUtil
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * Class in charge of Authentication Activity.
 *
 * @author Franklin Cardenas
 */
class AuthenticationActivity : AppCompatActivity() {

    private val alertUtil = AlertUtil()
    private val couldNotLogin =
        "No se pudo iniciar sesion.\nEmail o constraseña Incorrecta.\nIntentelo de nuevo."
    private val error = "Error"
    private val userAndPasswordNotPresent = "Ingrese usuario y contraseña."

    /**
     * On Create method for Authentication Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Application started")
        analytics.logEvent("InitScreen", bundle)

        //Setup login page
        setup()

    }

    /**
     * Setup process for login
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Login"

        //Setup Log In Button
        buttonLogIn.setOnClickListener {
            if (!editTextEmail.text.isNullOrBlank() && !editTextPassword.text.isNullOrBlank()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email, Provider.APPLICATION)
                    } else {
                        alertUtil.simpleAlert(error, couldNotLogin, this)
                    }
                }
            } else {
                alertUtil.simpleAlert(error, userAndPasswordNotPresent, this)
            }

        }
        //Setup Forget Password Button
        buttonForgetPassword.setOnClickListener {
            val forgetPasswordIntent = Intent(this, ForgetPasswordActivity::class.java).apply {}
            startActivity(forgetPasswordIntent)
        }
    }

    /**
     * Show home screen and pass it the user credentials.
     *
     * @author Franklin Cardenas
     * @param email user email
     * @param provider Account provider
     */
    private fun showHome(email : String?, provider : Provider) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider)
        }
        startActivity(homeIntent)
    }
}

