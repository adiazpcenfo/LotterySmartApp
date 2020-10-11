package com.mobile.lotterysmartapp.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
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
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))


        //Get user and provider from login or register
        val bundle = intent.extras
        val email = bundle?.getString(Constants.EMAIL)
        val provider = bundle?.getString(Constants.PROVIDER)

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
}