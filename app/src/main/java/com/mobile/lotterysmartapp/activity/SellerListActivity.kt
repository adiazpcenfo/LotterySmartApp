package com.mobile.lotterysmartapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory

class SellerListActivity : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var sellerList: MutableList<Inventory>
    lateinit var drawList: MutableList<Draw>
    lateinit var listView: ListView
    lateinit var drawSpinnerSelect: Spinner
    lateinit var numberSpinner: Spinner

    /**
     *On Create method for SellerListActivity.
     *
     * @author Josue Calderón Varela
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_list)

        sellerList = mutableListOf()
        drawList = mutableListOf()
        listView = findViewById(R.id.sellerList)
        drawSpinnerSelect = findViewById(R.id.drawSpinner)
        numberSpinner = findViewById(R.id.numberSpinner)
        ref = FirebaseDatabase.getInstance().getReference("Inventory")
        ref2 = FirebaseDatabase.getInstance().getReference("Draw")
        loadTable()
        spinner()
    }

    /**
     *Load all data from inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */

    fun loadTable() {

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                var numbersOptions = arrayListOf("Seleccionar número")

                for (i: Number in 0 until 100) {
                    numbersOptions.add(i.toString())
                }

                numberSpinner.adapter = ArrayAdapter<String>(
                    this@SellerListActivity,
                    android.R.layout.simple_list_item_1,
                    numbersOptions
                )

                if (snapshot.exists()) {
                    sellerList.clear()
                    for (s in snapshot.children) {
                        val inventory = s.getValue(Inventory::class.java)
                        if (inventory != null) {
                            sellerList.add(inventory)
                        }

                        val adapter =
                            SellerListAdapter(
                                this@SellerListActivity,
                                R.layout.seller_list,
                                sellerList
                            )
                        listView.adapter = adapter
                    }
                }
            }
        })


    }

    /**
     *Fill spinner with draw name data
     *
     * @author Josue Calderón Varela
     */
    fun spinner() {

        var ar = arrayListOf("Seleccionar sorteo")

        ref2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {
                    for (d in snapshot.children) {
                        val draw = d.getValue(Draw::class.java)
                        if (draw != null) {
                            ar.add(draw.name)
                            drawSpinnerSelect.adapter = ArrayAdapter<String>(
                                this@SellerListActivity,
                                android.R.layout.simple_list_item_1,
                                ar
                            )

                        }
                    }
                }
            }
        })
    }
}