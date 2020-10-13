package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Application started")
        analytics.logEvent("InitScreen", bundle)

        setup()

        session()
    }

    /**
     * Makes authentication layout visible.
     *
     * @author Franklin Cardenas
     */
    override fun onStart() {
        super.onStart()
        authenticationLayout.visibility = View.VISIBLE
    }

    /**
     * Setup process for authorization activity
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Login"
        setupRegisterButton()
        setupLoginButton()
        setupForgetPasswordButton()
    }

    /**
     * Setup for Forget My Password Button.
     *
     * @author Franklin Cardenas
     */
    private fun setupForgetPasswordButton() {
        buttonForgetPassword.setOnClickListener {
            val forgetPasswordIntent = Intent(this, ForgetPasswordActivity::class.java).apply {}
            startActivity(forgetPasswordIntent)
        }
    }

    /**
     * Setup for Login Button.
     *
     * @author Franklin Cardenas
     */
    private fun setupLoginButton() {
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
                }.addOnFailureListener {
                    alertUtil.simpleAlert(error, it.message, this)
                }
            } else {
                alertUtil.simpleAlert(error, userAndPasswordNotPresent, this)
            }
        }
    }

    /**
     * Setup for Register Button.
     *
     * @author Jimena Vega
     */
    private fun setupRegisterButton() {
        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    /**
     * Show home screen and pass it the user credentials.
     *
     * @param email user email
     * @param provider Account provider
     * @author Franklin Cardenas
     */
    private fun showHome(email: String?, provider: Provider) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider.toString())
        }
        startActivity(homeIntent)
    }


    /**
     * Checks if a session is active. If it is active, redirects the user to home page
     *
     * @author Franklin Cardenas
     */
    private fun session() {
        val preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        val email = preferences.getString(Constants.EMAIL, null)
        val provider = preferences.getString(Constants.PROVIDER, null)

        if (email != null && provider != null) {
            authenticationLayout.visibility = View.INVISIBLE
            showHome(email, Provider.valueOf(provider))
        }

    }

//    private fun showDrawMantain(email : String?, provider : Provider) {
//        val homeIntent = Intent(this, DrawActivity::class.java).apply {
//            putExtra(Constants.EMAIL, email)
//            putExtra(Constants.PROVIDER, provider)
//        }
//        startActivity(homeIntent)
//    }
}

