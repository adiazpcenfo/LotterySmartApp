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

    lateinit var drawerLayout : DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var menu: Menu
    lateinit var textView: TextView



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
        //val bundle = intent.extras
        //val email = bundle?.getString(Constants.EMAIL)
        //val provider = bundle?.getString(Constants.PROVIDER)

        //Setup process for Home Activity
        setup()
        setupMenu()

    }

    /**
     * Setup method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    private fun setup() {

    }

    private fun setupMenu(){
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        textView=findViewById(R.id.textView);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar)

        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onNavigationItemSelected( menuItem: MenuItem): Boolean {
        var intentMenu = Intent()
        intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        when (menuItem.itemId){

            //Send to home option
            R.id.nav_view->{
                
            }

            //Send to seller list option
            R.id.sellerList-> {


            }

            //Send to reserved nums option
            //Code here

            //Send to nums winners option
            //Code here

            //Send to modify profile option
            //Code here

            //Logout user option
            //Code here
        }
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else  {
            super.onBackPressed()
        }
    }
}