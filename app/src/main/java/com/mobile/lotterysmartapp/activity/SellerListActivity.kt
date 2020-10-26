package com.mobile.lotterysmartapp.activity

import android.Manifest
import android.annotation.SuppressLint
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


class SellerListActivity : AppCompatActivity() {
    private lateinit var inventoryReference: DatabaseReference
    private lateinit var drawReference: DatabaseReference
    private lateinit var userReference: DatabaseReference
    private lateinit var sellerList: ArrayList<Inventory>
    private lateinit var userList: ArrayList<User>
    private lateinit var inventoryList: ArrayList<Inventory>
    private lateinit var listView: ListView
    private lateinit var drawSpinner: Spinner
    private lateinit var numberSpinner: Spinner
    private lateinit var seekBarRange: SeekBar
    private var numSelectedValue: String = ""
    private var drawSelectedValue: String = ""
    private var latitudeValue = 0.0
    private var longitudeValue = 0.0
    private var rangeSelected: Int = 0
    private var userInventory: User? = null
    private var finalDistance = 0
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
        inventoryList = arrayListOf()
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
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
        }

        inventoryData()
        sellerData()
        getDistance()
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
    private val locationListener: LocationListener = object : LocationListener {
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
     *Load all inventories from database
     *
     * @author Josue Calderón Varela
     */
    private fun inventoryData() {

        inventoryReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                inventoryList.clear()

                for (inventory in snapshot.children) {

                    val inventoryData = inventory.getValue(Inventory::class.java)

                    if (inventoryData != null) {

                        inventoryList.add(inventoryData)

                    }
                }
            }
        })
    }

    /**
     *Load inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */
    @SuppressLint("SetTextI18n")
    private fun loadTable() {

        sellerList.clear()

        for (inventory in inventoryList) {

            for (user in userList) {

                if (user.email == inventory.userEmail) {

                    userInventory = user

                }
            }

            if (userInventory != null && inventory.drawName == drawSelectedValue && inventory.number == numSelectedValue.toInt() && getDistance() <= rangeSelected
                && userInventory!!.userType == "Vendedor"
            ) {

                sellerList.add(inventory)
                searchesCount(inventory)

            }
        }
    }

    /**
     *Fill custom list with table data
     *
     * @author Josue Calderón Varela
     */
    private fun fillCustomList() {

        val adapter =
            SellerListAdapter(
                this@SellerListActivity,
                R.layout.seller_list,
                sellerList
            )

        listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /**
     *Get final distance between two points
     *
     * @author Josue Calderón Varela
     */
    private fun getDistance(): Int {

        val distanceValue =
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
     *Update the number of inventory searches for each search
     *
     * @author Josue Calderón Varela
     * @param inventory all inventory data
     */
    private fun searchesCount(inventory: Inventory) {

        inventoryReference.child(inventory.Id).setValue(
            Inventory(
                inventory.Id,
                inventory.drawName,
                inventory.userEmail,
                inventory.state,
                inventory.number,
                inventory.series,
                inventory.fractions,
                inventory.availableFractions,
                inventory.searches + 1
            )
        )
    }

    /**
     *Fill spinner with numbers (0...99) and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    private fun numberSpinner() {

        val numbersOptions = arrayListOf("Número")

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

                override fun onNothingSelected(parent: AdapterView<*>?) {}

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

        buttonSearch.setOnClickListener {

            when {
                numSelectedValue == "Número" -> {
                    alert(
                        "Seleccionar número",
                        "Seleccionar un número para poder realizar la búsqueda."
                    )
                }
                drawSelectedValue == "Sorteo" -> {

                    alert(
                        "Seleccionar sorteo",
                        "Seleccionar un sorteo para poder realizar la búsqueda."
                    )

                }
                rangeSelected == 0 -> {

                    alert(
                        "Seleccionar rango",
                        "El rango seleccionado es inválido, por favor aumentar rango de distancia."
                    )

                }
                else -> {

                    loadTable()
                    fillCustomList()

                    if (sellerList.size < 1) {

                        alert(
                            "No hay resultados",
                            "No se encontraron números con los datos indicados."
                        )

                    }
                }
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

            override fun onCancelled(error: DatabaseError) {}

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

                        override fun onNothingSelected(parent: AdapterView<*>?) {}

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

        builder.setPositiveButton("Aceptar") { _, _ ->

            Toast.makeText(
                applicationContext,
                "Aceptar", Toast.LENGTH_SHORT
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
     * @param long1 current longitude
     * @param lat2 seller latitude
     * @param long2 seller longitude
     *
     * @return distance
     */
    private fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Int {

        val locationA = Location("punto A")

        locationA.latitude = lat1
        locationA.longitude = long1

        val locationB = Location("punto B")

        locationB.latitude = lat2
        locationB.longitude = long2

        val format = DecimalFormat("#")

        return format.format(locationA.distanceTo(locationB) / 1000).toInt()

    }
}