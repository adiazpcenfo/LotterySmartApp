package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Inventory
import java.util.ArrayList

class BuyerReservedNumberActivity : AppCompatActivity()  {

    private lateinit var inventoryReference: DatabaseReference
    private lateinit var inventoryList: ArrayList<Inventory>
    private lateinit var reservedList: ArrayList<Inventory>
    private lateinit var listView: ListView
    private lateinit var preferences: SharedPreferences
    private var email: String? = null

    /**
     *On Create method for BuyerReservedNumbersActivity.
     *
     * @author Allan Diaz
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_reserved_numbers)

        inventoryList = arrayListOf()
        reservedList = arrayListOf()
        listView = findViewById(R.id.buyerReservedList)

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

                    if (inventory.state=="RSV" && inventory.userEmail == email) {

                        reservedList.add(inventory)

                    }
                }
                if(reservedList.size>0){
                    val adapter =
                        BuyerReservedListAdapter(
                            this@BuyerReservedNumberActivity,
                            R.layout.buyer_reserved_numbers,
                            reservedList,
                            inventoryList
                        )

                    listView.adapter = adapter
                }


            }
        })
    }

}