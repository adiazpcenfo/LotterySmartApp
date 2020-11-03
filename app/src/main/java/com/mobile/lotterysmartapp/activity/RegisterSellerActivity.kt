package com.mobile.lotterysmartapp.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.util.PatternsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Provider
import com.mobile.lotterysmartapp.model.User
import com.mobile.lotterysmartapp.model.UserType
import kotlinx.android.synthetic.main.activity_register_seller.*

import java.util.*

class RegisterSellerActivity : AppCompatActivity() {

    private val accountSuccess = "Su cuenta se registró correctamente."
    private val verifyPassword = "Verifique que la contraseña tenga más de 6 caracteres."
    private val verifyInputs = "Por favor verifique que todos los datos sean correctos."
    private val accountError =
        "Lo sentimos, su cuenta no se registró correctamente, por favor intente de nuevo."
    private val errorAlert = "¡Error!"
    private val successAlert = "¡Éxito!"
    private var locationManager: LocationManager? = null
    private var latitudeValue = 0.0
    private var longitudeValue = 0.0
    lateinit var mapFragment: SupportMapFragment
    lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_seller)

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Application started")
        analytics.logEvent("InitScreen", bundle)

        //Initialize variables
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            mMap = it

        })

        //setup location
        try {

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f,
                locationListener
            )
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getLocation()

        } else {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
        }


        //setup register view
        setUp()
    }

    /**
     * Makes the setup for the button to register an user
     *
     * @author Jimena Vega
     *
     */
    private fun setUp() {
        //setup of registerUserButton
        val database = FirebaseDatabase.getInstance().getReference("User")
        val userType = UserType.SELLER.type
        buttonRegisterUserSeller.setOnClickListener {
            try {
                register(database, userType)
            } catch (e: Exception) {
                println(e.message)
            }

        }
    }

    /**
     *Get my current latitude and longitude
     *
     * @author Josue Calderón Varela
     */
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            try {
                val geocoder = Geocoder(this@RegisterSellerActivity, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                latitudeValue = addresses[0].latitude
                longitudeValue = addresses[0].longitude
                val point = LatLng(latitudeValue, longitudeValue)
                mMap.addMarker(MarkerOptions().position(point).title("Ubicación de su puesto"))
                mMap.uiSettings.isZoomControlsEnabled = true
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 18F))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * Register an user
     *
     * @author Jimena Vega
     * @param database the reference to the database
     * @param userType the type of user can be: seller or buyer
     *
     */
    private fun register(database: DatabaseReference, userType: String) {
        if (verifyPasswordLength()) {
            if (verifyEmail() && verifyPasswords()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    textEmailSeller.text.toString(),
                    textPasswordSeller.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val id = getRandomString()
                        database.child(id).setValue(
                            User(
                                id,
                                textEmailSeller.text.toString(),
                                textNameSeller.text.toString(),
                                "",
                                userType,
                                latitudeValue,
                                longitudeValue
                            )
                        )
                        showHome(
                            textEmailSeller.text.toString(),
                            Provider.APPLICATION,
                            UserType.SELLER
                        )
                        clearForm()
                    }
                }.addOnFailureListener {
                    alert(errorAlert, verifyInputs)
                }
            } else {
                alert(errorAlert, accountError)
            }
        } else {
            alert(errorAlert, verifyPassword)
        }
    }


    /**
     * Verifies that the email provided by the user its valid
     *
     * @author Jimena Vega
     */
    private fun verifyEmail(): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(textEmailSeller.text).matches()
    }

    /**
     * Verifies that the password provided by the user has the amount of digits required
     *
     * @author Jimena Vega
     */
    private fun verifyPasswordLength(): Boolean {
        return textPasswordSeller.text.length > 6
    }

    /**
     * Verifies that the password and the confirmation of the password are the same
     *
     * @author Jimena Vega
     */
    private fun verifyPasswords(): Boolean {
        return textPasswordSeller.text.toString() == textConfirmPasswordSeller.text.toString()
    }

    /**
     * Show alert in case the new account could not be created.
     *
     * @param title a string with the title to show
     * @param message a string with the message to show
     *
     * @author Franklin Cardenas
     */
    private fun alert(title: String?, message: String?) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = alertBuilder.create()
        dialog.show()
    }

    /**
     * Clear the inputs from the form.
     *
     * @author Jimena Vega
     */
    private fun clearForm() {
        textNameSeller.text.clear()
        textEmailSeller.text.clear()
        textPasswordSeller.text.clear()
        textConfirmPasswordSeller.text.clear()
    }

    /**
     * Makes a random sequence of Strings
     *
     * @param length size of the sequence of Strings
     *
     * @author Jimena Vega
     */
    private fun getRandomString(): String {
        val length = 10
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    /**
     * Show home screen and pass it the user credentials.
     *
     * @author Franklin Cardenas
     * @param email user email
     * @param provider Account provider
     */
    private fun showHome(email: String?, provider: Provider, userType: UserType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra(Constants.EMAIL, email)
            putExtra(Constants.PROVIDER, provider.toString())
            putExtra(Constants.USERTYPE, userType.name)
        }
        startActivity(homeIntent)
        finish()
    }

    /**
     * Send the user to the type of user view when the back button is used
     *
     * @author Jimena Vega
     */
    override fun onBackPressed() {
        clearForm()
        val intentToBack = Intent(this, TypeUserActivity::class.java)
        intentToBack.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToBack)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Register Seller")
        analytics.logEvent("RegisterSellerScreen", bundle)
    }
}