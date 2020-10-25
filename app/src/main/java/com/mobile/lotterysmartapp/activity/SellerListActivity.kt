package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import kotlinx.android.synthetic.main.activity_seller_list.*


class SellerListActivity : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var sellerList: ArrayList<Inventory>
    lateinit var listView: ListView
    lateinit var drawSpinner: Spinner
    lateinit var numberSpinner: Spinner
    lateinit var query: Query
    var numSelectedValue: String = ""
    var drawSelectedValue: String = ""
    var queryListener: ValueEventListener? = null

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
        ref = FirebaseDatabase.getInstance().getReference("Inventory")
        ref2 = FirebaseDatabase.getInstance().getReference("Draw")

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Seller List")
        analytics.logEvent("SellerListScreen", bundle)

        drawSpinner()
        numberSpinner()
        search()

    }


    /**
     *Load all data from inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */

    fun loadTable() {

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

                    val inventory = s.getValue(Inventory::class.java)
                    if (inventory != null && inventory.drawName == drawSelectedValue && inventory.number == numSelectedValue.toInt()) {
                        sellerList.add(inventory)
                    }
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

    /**
     *Fill spinner with numbers (0...99) and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    fun numberSpinner() {

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
    fun search() {

        buttonSearch.setOnClickListener() {

            if (numSelectedValue == "Número" || drawSelectedValue == "Sorteo") {
                alert()
            } else {
                loadTable()
            }

        }
    }

    /**
     *Fill spinner with draw name data and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    private fun drawSpinner() {

        var drawOptions = arrayListOf("Sorteo")

        ref2.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                drawOptions = arrayListOf("Sorteo")
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
}



