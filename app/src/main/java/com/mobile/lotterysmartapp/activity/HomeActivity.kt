package com.mobile.lotterysmartapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mobile.lotterysmartapp.R


/**
 * Class in charge of Home Activity.
 *
 * @author Franklin Cardenas
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Declaration of the variables for the menu
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var textView: TextView


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
        //val bundle = intent.extras
        //val email = bundle?.getString(Constants.EMAIL)
        //val provider = bundle?.getString(Constants.PROVIDER)

        //Setup process for Home Activity
        setup()

        //Setup function for the menu
        setupMenu()

    }

    /**
     * Setup method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    private fun setup() {

    }

    /**
     * Setup method for the menu
     *
     * @author Jimena Vega
     */
    private fun setupMenu() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        textView = findViewById(R.id.textView)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    /**
     * Method that redirects the user to the view selected
     *
     *@param menuItem stores the value of the option selected
     *
     * @author Jimena Vega
     */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var intentMenu = Intent()
        intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        when (menuItem.itemId) {

            //Send to home option
            R.id.nav_home -> {
                intentMenu = Intent(this, HomeActivity::class.java)
                startActivity(intentMenu)
            }

            //Send to seller list option
            R.id.list_sellers -> {
                intentMenu = Intent(this, SellerListActivity::class.java)
                startActivity(intentMenu)
                drawerLayout.closeDrawer(GravityCompat.START)
            }


            //Send to reserved nums option
            R.id.list_reserved_nums -> {

            }

            //Send to nums winners option
            R.id.list_winners -> {

            }


            //Send to modify profile option
            R.id.modify_profile -> {

            }


            //Logout user option
            R.id.log_out -> {

            }

        }

        return true
    }

    /**
     * Hides the menu when pressed
     *
     * @author Jimena Vega
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finishAffinity()
        }
    }
}