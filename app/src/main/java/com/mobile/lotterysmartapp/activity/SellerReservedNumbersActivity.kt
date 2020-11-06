package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Inventory
import java.util.ArrayList

class SellerReservedNumbersActivity : AppCompatActivity() {

    private lateinit var inventoryReference: DatabaseReference
    private lateinit var inventoryList: ArrayList<Inventory>
    private lateinit var reservedList: ArrayList<Inventory>
    private lateinit var listView: ListView
    private lateinit var preferences: SharedPreferences
    private var email: String? = null

    /**
     *On Create method for SellerReservedNumbersActivity.
     *
     * @author Josue Calderón Varela
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_reserved_numbers)

        inventoryList = arrayListOf()
        reservedList = arrayListOf()
        listView = findViewById(R.id.reservedList)

        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        email = preferences.getString(Constants.EMAIL, null)

        inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")

        loadTable()
    }

    /**
     *Load inventory using Realtime Database with specific filter
     *
     * @author Josue Calderón Varela
     */
    private fun loadTable() {

        inventoryReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                inventoryList.clear()
                reservedList.clear()

                for (inventory in snapshot.children) {

                    val inventoryData = inventory.getValue(Inventory::class.java)

                    if (inventoryData != null) {

                        inventoryList.add(inventoryData)

                    }
                }

                for (inventory in inventoryList) {

                    if (inventory.state=="RSV" && inventory.sellerEmail == email) {

                        reservedList.add(inventory)

                    }
                }

                val adapter =
                    ReservedListAdapter(
                        this@SellerReservedNumbersActivity,
                        R.layout.reserved_numbers,
                        reservedList
                    )

                listView.adapter = adapter

            }
        })
    }
}