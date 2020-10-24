package com.mobile.lotterysmartapp.activity

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.User
import kotlinx.android.synthetic.main.activity_seller_list.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class SellerListActivity : AppCompatActivity() {
    lateinit var inventoryReference: DatabaseReference
    lateinit var drawReference: DatabaseReference
    lateinit var userReference: DatabaseReference
    lateinit var sellerList: ArrayList<Inventory>
    lateinit var userList: ArrayList<User>
    lateinit var listView: ListView
    lateinit var drawSpinner: Spinner
    lateinit var numberSpinner: Spinner
    lateinit var seekBarRange: SeekBar
    var numSelectedValue: String = ""
    var drawSelectedValue: String = ""
    var latitudeValue = 0.0
    var longitudeValue = 0.0
    var rangeSelected: Int = 0
    var userInventory: User? = null
    var finalDistance = 0
    private var locationManager: LocationManager? = null


    /**
     *On Create method for SellerListActivity.
     *
     * @author Josue Calderón Varela
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_list)

        sellerList = arrayListOf()
        userList = arrayListOf()
        listView = findViewById(R.id.sellerList)
        drawSpinner = findViewById(R.id.drawSpinner)
        numberSpinner = findViewById(R.id.numberSpinner)
        seekBarRange = findViewById(R.id.seekBarRange)

        inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")
        drawReference = FirebaseDatabase.getInstance().getReference("Draw")
        userReference = FirebaseDatabase.getInstance().getReference("User")

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

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
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

                , 100
            )
        }

        sellerData()
        drawSpinner()
        numberSpinner()
        rangeSeekBar()
        search()

    }

    /**
     *Get my current latitude and longitude
     *
     * @author Josue Calderón Varela
     */
    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            try {
                val geocoder = Geocoder(this@SellerListActivity, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                latitudeValue = addresses[0].latitude
                longitudeValue = addresses[0].longitude
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     *Load all users from database
     *
     * @author Josue Calderón Varela
     */
    private fun sellerData() {

        userReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
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
     *Get final distance between two points
     *
     * @author Josue Calderón Varela
     */
    fun getDistance(): Int {


        var distanceValue =
            userInventory?.coordinatesY?.let {
                userInventory?.coordinatesX?.let { it1 ->
                    distance(
                        latitudeValue, longitudeValue, it1,
                        it
                    )
                }
            }

        if (distanceValue != null) {
            finalDistance = distanceValue
        }

        return finalDistance
    }


    /**
     *Load all data from inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */
    private fun loadTable() {

        inventoryReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                sellerList.clear()

                for (s in snapshot.children) {

                    val inventory = s.getValue(Inventory::class.java)

                    for (user in userList) {

                        if (user.email == inventory?.userEmail) {

                            userInventory = user

                        }
                    }

                    if (inventory != null && userInventory != null && inventory.drawName == drawSelectedValue && inventory.number == numSelectedValue.toInt() && getDistance() <= rangeSelected
                        && userInventory!!.userType == "Vendedor") {

                        sellerList.add(inventory)

                    } else if (sellerList.isEmpty()){

                        alert("No está disponible", "El número buscado no se encuentra disponible.")

                    }


                    val adapter =
                        SellerListAdapter(
                            this@SellerListActivity,
                            R.layout.seller_list,
                            sellerList
                        )
                    listView.adapter = adapter
                    adapter.notifyDataSetChanged()

                }
            }

        })
    }

    /**
     *Fill spinner with numbers (0...99) and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    private fun numberSpinner() {

        var numbersOptions = arrayListOf("Número")

        for (i: Number in 0 until 100) {
            numbersOptions.add(i.toString())
        }

        numberSpinner.adapter = ArrayAdapter<String>(
            this@SellerListActivity,
            android.R.layout.simple_list_item_1,
            numbersOptions
        )

        numberSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    numSelectedValue =
                        parent!!.getItemAtPosition(position).toString()

                }
            }


    }

    /**
     *Apply filter and search data
     *
     * @author Josue Calderón Varela
     */
    private fun search() {

        buttonSearch.setOnClickListener() {


            if (numSelectedValue == "Número") {

                alert(
                    "Seleccionar número",
                    "Seleccionar un número para poder realizar la búsqueda."
                )

            } else if (drawSelectedValue == "Sorteo") {

                alert(
                    "Seleccionar sorteo",
                    "Seleccionar un sorteo para poder realizar la búsqueda."
                )

            } else if (rangeSelected == 0) {

                alert(
                    "Seleccionar rango",
                    "El rango seleccionado es inválido, por favor aumentar rango de distancia."
                )

            } else {

                loadTable()

            }

        }
    }

    /**
     *Select range to find a number
     *
     * @author Josue Calderón Varela
     */
    private fun rangeSeekBar() {

        seekBarRange.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {

                rangeTxt.text = ("$progress km")
                rangeSelected = progress

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress
            }

        })

    }

    /**
     *Fill spinner with draw name data and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    private fun drawSpinner() {

        val drawOptions = arrayListOf("Sorteo")

        drawReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (d in snapshot.children) {

                        val draw = d.getValue(Draw::class.java)

                        if (draw != null) {

                            drawOptions.add(draw.name)

                        }
                    }

                    drawSpinner.adapter = ArrayAdapter<String>(
                        this@SellerListActivity,
                        android.R.layout.simple_list_item_1,
                        drawOptions
                    )

                }

                drawSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            drawSelectedValue =
                                parent!!.getItemAtPosition(position).toString()

                        }
                    }
            }

        })
    }

    /**
     *Create custom alerts
     *
     * @author Josue Calderón Varela
     *
     * @param title alert title
     * @param message alert message
     */
    private fun alert(title: String, message: String) {

        val builder = AlertDialog.Builder(this)

        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            Toast.makeText(
                applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()

        }

        builder.show()

    }

    /**
     *Get distance between two points
     *
     *@author Josue Calderón Varela
     *
     * @param lat1 current latitude
     * @param lon1 current longitude
     * @param lat2 seller latitude
     * @param lon2 seller longitude
     *
     * @return distance
     */
    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Int {

        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        val format = DecimalFormat("#")

        return format.format(dist).toInt()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}