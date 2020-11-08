package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Provider
import com.mobile.lotterysmartapp.model.User
import com.mobile.lotterysmartapp.model.UserType
import com.mobile.lotterysmartapp.util.AlertUtil
import kotlinx.android.synthetic.main.activity_authentication.*
import java.util.*

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
    private val googleSignInId = 100
    private val userReference = FirebaseDatabase.getInstance().getReference("User")
    private lateinit var userList: ArrayList<User>

    /**
     * On Create method for Authentication Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        userList = arrayListOf()

        session()

        loadData()

        setup()
    }


    /**
     * Makes authentication layout visible at startup.
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
        setupGoogleSignInButton()

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Application started")
        analytics.logEvent("InitScreen", bundle)
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
     * Setup for Forget My Password Button.
     *
     * @author Franklin Cardenas
     */
    private fun setupGoogleSignInButton() {
        buttonGoogleSignIn.setOnClickListener {
            val googleConfiguration =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build()
            val googleClient = GoogleSignIn.getClient(this, googleConfiguration)
            googleClient.signOut()
            authenticationLayout.visibility = View.INVISIBLE
            startActivityForResult(googleClient.signInIntent, googleSignInId)
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
     * Load the userlist with all users
     *
     * @author Jimena Vega
     */
    private fun loadData() {
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (seller in snapshot.children) {
                    val user = seller.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
            }
        })
    }

    /**
     * Setup for Register Button.
     *
     * @author Jimena Vega
     */
    private fun setupRegisterButton() {
        buttonRegister.setOnClickListener {
            val intent = Intent(this, TypeUserActivity::class.java)
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
        var userView: User? = null

        for (user in userList) {
            if (user.email == email) {
                userView = user
            }
        }

        when (userView?.userType) {
            UserType.BUYER.type -> {
                startActivity(getHomeActivityIntentByUserType(UserType.BUYER, email, provider))
            }
            UserType.SELLER.type -> {
                startActivity(getHomeActivityIntentByUserType(UserType.SELLER, email, provider))
            }
            UserType.ADMIN.type-> {
                startActivity(getHomeActivityIntentByUserType(UserType.ADMIN, email, provider))
            }
        }
    }

    /**
     * Select correct home activity based on User Type.
     *
     * @param userType User Type
     * @param email user email
     * @param provider Account provider
     * @author Franklin Cardenas
     */
    private fun getHomeActivityIntentByUserType(
        userType: UserType,
        email: String?,
        provider: Provider
    ): Intent? {
        return Intent(this, HomeActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider.toString())
            putExtra(Constants.USERTYPE, userType.name)
        }
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
        val userType = preferences.getString(Constants.USERTYPE, null)


        if (email != null && provider != null && userType != null) {
            authenticationLayout.visibility = View.INVISIBLE
            val homeActivity =
                getHomeActivityIntentByUserType(
                    UserType.valueOf(userType),
                    email,
                    Provider.valueOf(provider)
                )

            startActivity(homeActivity)
        }
    }

    /**
     * Checks the result from Google Sign In Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleSignInId) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            try {
                if (account != null) {
                    if (!isUserPreviouslyRegistered(account)) {
                        registerUser(account, UserType.BUYER)
                    }
                    val credential = GoogleAuthProvider
                        .getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", Provider.GOOGLE)
                            } else {
                                alertUtil.simpleAlert(error, couldNotLogin, this)
                            }
                        }
                }
            } catch (e: ApiException) {
                alertUtil.simpleAlert(error, couldNotLogin, this)
            }
        }
    }

    /**
     * Register a user to the database based on a Google Account.
     *
     * @param account Google Sign In Account.
     * @param userType User Type.
     * @author Franklin Cardenas
     */

    private fun registerUser(account: GoogleSignInAccount, userType: UserType) {
        val id = UUID.randomUUID().toString()
        userReference.child(id).setValue(
            User(
                id,
                account.email!!,
                account.givenName!!,
                account.familyName!!,
                userType.type,
                0.0,
                0.0
            )
        )
    }

    /**
     * Checks inside the userList if the account is already registered.
     *
     * @param account Google Sign In Account
     * @return True if the user is registered. False if the user is not registered.
     * @author Franklin Cardenas
     */
    private fun isUserPreviouslyRegistered(account: GoogleSignInAccount): Boolean {
        for (user in userList) {
            if (user.email == account.email) {
                return true
            }
        }
        return false
    }

    /**
     * Disable back press to prevent return after logout.
     *
     * @author Franklin Cardenas
     */
    override fun onBackPressed() {}
}

