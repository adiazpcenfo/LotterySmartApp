package com.mobile.lotterysmartapp.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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
import com.mobile.lotterysmartapp.model.UserType
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
    private var inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")

    /**
     * On Create method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get user and provider from login or register
        val bundle = intent.extras
        val email = bundle?.getString(Constants.EMAIL)
        val provider = bundle?.getString(Constants.PROVIDER)
        val userType = bundle?.getString(Constants.USERTYPE)

        // Save email and password for the session
        saveCredentials(email!!, provider!!, userType!!)

        //Set Layout based on User Type
        when (userType) {
            UserType.BUYER.name -> {
                setContentView(R.layout.activity_home)
            }
            UserType.SELLER.name -> {
                setContentView(R.layout.activity_home_seller)
            }
            UserType.ADMIN.name -> {
                setContentView(R.layout.activity_home_admin)
            }
        }

        setSupportActionBar(findViewById(R.id.toolbar))

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
        this.title = "Lottery Smart"

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

            //---------------------------Buyer Items----------------------------------

            R.id.profile -> {
                startActivity(Intent(this, ProfileUserActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.list_sellers -> {
                startActivity(Intent(this, SellerListActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.list_reserved_nums -> {
                startActivity(Intent(this, BuyerReservedNumberActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            //---------------------------Seller Items----------------------------------

            R.id.profileSeller -> {
                startActivity(Intent(this, ProfileSellerActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.list_sellerLottery -> {
                startActivity(Intent(this, SellerLotteryActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.list_reservedNumbers -> {
                startActivity(Intent(this, SellerReservedNumbersActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            //---------------------------Admin Items----------------------------------

            R.id.registerDraw -> {
                startActivity(Intent(this, RegisterDrawActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.modifyDraw -> {
                startActivity(Intent(this, ModifyDrawResultsActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.deleteDraw -> {
                startActivity(Intent(this, DeleteDrawActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            //---------------------------All user Items----------------------------------

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
    private fun saveCredentials(email: String, provider: String, userType: String?) {
        val preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE).edit()
        preferences.putString(Constants.EMAIL, email)
        preferences.putString(Constants.PROVIDER, provider)
        preferences.putString(Constants.USERTYPE, userType)
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