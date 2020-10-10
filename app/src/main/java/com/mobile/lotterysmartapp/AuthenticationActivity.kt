package com.mobile.lotterysmartapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Provider
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * Class in charge of Authentication Activity.
 *
 * @author Franklin Cardenas
 */
class AuthenticationActivity : AppCompatActivity() {

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
     * Set process for login
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Login"

        //Setup register button
        buttonRegister.setOnClickListener {
            /*if (editTextEmail != null && editTextPassword != null) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email, Provider.APPLICATION)
                    } else {
                        alert()
                    }
                }
            }else{
                val user = "adiaz"
                val email="adiazp@ucenfo.ac.cr"
                showHome("adiazp@ucenfo.ac.cr" , Provider.APPLICATION)
            }*/
            showDrawMantain("adiazp@ucenfo.ac.cr" , Provider.APPLICATION)
        }

        //Setup Log In Button
        buttonLogIn.setOnClickListener {
            if (editTextEmail != null && editTextPassword != null) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email, Provider.APPLICATION)
                    } else {
                        alert()
                    }
                }
            }
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

    private fun showDrawMantain(email : String?, provider : Provider) {
        val homeIntent = Intent(this, DrawActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider)
        }
        startActivity(homeIntent)
    }

    /**
     * Show alert in case the new account could not be created.
     *
     * @author Franklin Cardenas
     */
    private fun alert() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("Error")
        alertBuilder.setMessage("Ocurrio un error al procesar su nueva cuenta, por favor intentelo de nuevo.")
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = alertBuilder.create()
        dialog.show()
    }

}

//val user = User("Franko")
//val database = Firebase.database.reference
//database.child("users").child("username").setValue(user.name)

