package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants


/**
 * Class in charge of Home Activity.
 *
 * @author Franklin Cardenas
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    /**
     * On Create method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))

        //Get user and provider from login or register
        val bundle = intent.extras
        val email = bundle?.getString(Constants.EMAIL)
        val provider = bundle?.getString(Constants.PROVIDER)

        // Save email and password for the session
        saveCredentials(email!!, provider!!)

        //Setup process for Home Activity
        setup()
    }

    /**
     * Setup method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Inicio"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Save user credentials inside a preference file.
     *
     * @param email - user email
     * @param provider - authentication provider
     * @author Franklin Cardenas
     */
    private fun saveCredentials(email: String, provider: String) {
        val preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE).edit()
        preferences.putString(Constants.EMAIL, email)
        preferences.putString(Constants.PROVIDER, provider)
        preferences.apply()

    }

    /**
     * Logout of the app.
     * Delete user credentials from preferences file.
     *
     * @author Franklin Cardenas
     */
    private fun logout() {
        val preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE).edit()
        preferences.clear()
        preferences.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }

}