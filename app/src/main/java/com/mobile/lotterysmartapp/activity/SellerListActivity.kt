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
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class SellerListActivity : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var sellerList: ArrayList<Inventory>
    lateinit var listView: ListView
    lateinit var drawSpinner: Spinner
    lateinit var numberSpinner: Spinner
    lateinit var seekBarRange: SeekBar
    lateinit var query: Query
    var numSelectedValue: String = ""
    var drawSelectedValue: String = ""
    var latitudeValue = 0.0
    var longitudeValue = 0.0
    var rangeSelected: Int = 0
    var finalDistance = 0
    var queryListener: ValueEventListener? = null
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
        listView = findViewById(R.id.sellerList)
        drawSpinner = findViewById(R.id.drawSpinner)
        numberSpinner = findViewById(R.id.numberSpinner)
        seekBarRange = findViewById(R.id.seekBarRange)

        ref = FirebaseDatabase.getInstance().getReference("Inventory")
        ref2 = FirebaseDatabase.getInstance().getReference("Draw")
        ref3 = FirebaseDatabase.getInstance().getReference("User")

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

        drawSpinner()
        numberSpinner()
        rangeSeekBar()
        search()

    }

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            try {
                val geocoder = Geocoder(this@SellerListActivity, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                var latV = addresses[0].latitude
                var longV = addresses[0].longitude
                latitudeValue = latV
                longitudeValue = longV
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     *Load all data from inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */

    private fun loadTable() {

        query = ref.orderByChild("number").equalTo(numSelectedValue.toString())
        query = ref.orderByChild("drawName").equalTo(drawSelectedValue)

        queryListener?.let { query.addValueEventListener(it) }

        queryListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                sellerList.clear()
                for (s in snapshot.children) {
                    for (u in snapshot.children) {

                        val inventory = s.getValue(Inventory::class.java)
                        val user = u.getValue(User::class.java)

                        if (inventory != null && user != null && inventory.drawName == drawSelectedValue && inventory.number == numSelectedValue && getDistance() <= rangeSelected
                        ) {
                            sellerList.add(inventory)
                        } else {

                            alertNotExist()

                        }
                    val inventory = s.getValue(Inventory::class.java)
                    if (inventory != null && inventory.drawName == drawSelectedValue && inventory.number == numSelectedValue.toInt()) {
                        sellerList.add(inventory)
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
        }
    }

    fun getDistance():Int{

        ref3.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (u in snapshot.children) {
                        val user = u.getValue(User::class.java)
                        if (user != null) {
                            var distanceValue = distance(latitudeValue, longitudeValue, user.coordinatesX, user.coordinatesY)
                            finalDistance = distanceValue
                        }
                    }
                }
            }
        })

        return finalDistance

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



            if (numSelectedValue == "Número" || drawSelectedValue == "Sorteo" || rangeSelected == 0) {
                alert()
            } else {
                getDistance()
                Thread.sleep(5_000)
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

        var startPoint = 0
        var endPoint = 0

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
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
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

        ref2.addValueEventListener(object : ValueEventListener {

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
     *Create alert to validate empty draw and number spinner value
     *
     * @author Josue Calderón Varela
     */
    private fun alert() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Datos vacíos")
        builder.setMessage("Seleccionar sorteo y número para realizar la búsqueda.")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            Toast.makeText(
                applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()

        }

        builder.show()

    }

    /**
     *Create alert for when there are no results
     *
     * @author Josue Calderón Varela
     */
    private fun alertNotExist() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("No está disponible")
        builder.setMessage("El número buscado no se encuentra disponible.")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            Toast.makeText(
                applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()

        }

        builder.show()

    }

    fun distance(
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



