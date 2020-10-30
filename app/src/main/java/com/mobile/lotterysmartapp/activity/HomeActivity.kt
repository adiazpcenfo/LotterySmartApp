package com.mobile.lotterysmartapp.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Inventory
import kotlinx.android.synthetic.main.activity_home.*

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
    private lateinit var textView: TextView
    private var inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")

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

        //Setup function for the menu
        setupMenu()


        mostSearchedNumber()
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
        toolbar = findViewById(R.id.toolbar)
        this.title="Lottery Smart"

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
     * @param menuItem stores the value of the option selected
     * @author Jimena Vega
     */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var intentMenu = Intent()
        intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        when (menuItem.itemId) {

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
                intentMenu = Intent(this, DrawActivity::class.java)
                startActivity(intentMenu)
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            //Send to Lottery Vendor option
            R.id.list_sellerLottery -> {
                intentMenu = Intent(this, SellerLotteryActivity::class.java)
                startActivity(intentMenu)
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            //Send to profile option
            R.id.profile -> {
                intentMenu = Intent(this, ProfileUserActivity::class.java)
                startActivity(intentMenu)
            }

            //Logout user option
            R.id.log_out -> {
                logout()
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
        val homeIntent = Intent(this, AuthenticationActivity::class.java)
        startActivity(homeIntent)
    }

    /**
     * Ask in the database for the most searched number
     *
     * @author Franklin Cardenas
     */
    private fun mostSearchedNumber() {
        inventoryReference.orderByChild("number")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {}

                /**
                 * This method will be called with a snapshot of the data at this location. It will also be called
                 * each time that data changes.
                 *
                 * @param snapshot The current data at the location
                 * @author Franklin Cardenas
                 */
                override fun onDataChange(snapshot: DataSnapshot) {
                    val numberMap = HashMap<String, Int>()

                    for (i in 0..99) {
                        numberMap[i.toString()] = 0
                    }

                    for (entry in snapshot.children) {
                        val inventory = entry.getValue(Inventory::class.java)
                        if (inventory != null) {
                            val key = inventory.number.toString()
                            numberMap[key] = numberMap[key]!! + inventory.searches
                        }
                    }
                    updateMostSearchedNumber(calculateMostSearchedNumber(numberMap))
                }
            })
    }

    /**
     * Update the text view that display the Most Searched Number.
     *
     * @param number Most searched Number.
     * @author Franklin Cardenas
     */
    private fun updateMostSearchedNumber(number: String) {
        mostSearchedNumberTextView.text = number
    }

    /**
     * Get the number with the highest searched amount.
     *
     * @param numberMap Map that contains all numbers with each searched amounts.
     * @return Most searched number.
     * @author Franklin Cardenas
     */
    private fun calculateMostSearchedNumber(numberMap: HashMap<String, Int>): String {
        var mostSearchedNumber = ""
        var highestSearches = 0
        for (key in numberMap.keys) {
            if (numberMap[key]!! > highestSearches) {
                mostSearchedNumber = key
                highestSearches = numberMap[key]!!
            }
        }
        return mostSearchedNumber
    }
}