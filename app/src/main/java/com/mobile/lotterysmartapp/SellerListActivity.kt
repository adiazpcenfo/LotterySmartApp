package com.mobile.lotterysmartapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.User

class SellerListActivity : AppCompatActivity() {


    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var sellerList: MutableList<Inventory>
    lateinit var userList: MutableList<User>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_list)

        sellerList = mutableListOf()
        userList = mutableListOf()
        listView = findViewById(R.id.sellerList)
        ref = FirebaseDatabase.getInstance().getReference("Inventory")
        ref2 = FirebaseDatabase.getInstance().getReference("User")
        loadTable()

    }

    fun loadTable() {

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sellerList.clear()
                    for (s in snapshot.children) {
                        val inventory = s.getValue(Inventory::class.java)
                        if (inventory != null) {
                            sellerList.add(inventory)
                        }

                        val adapter = SellerListAdapter(
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
}