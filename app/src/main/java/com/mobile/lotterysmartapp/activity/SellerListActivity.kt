package com.mobile.lotterysmartapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.User

class SellerListActivity : AppCompatActivity() {


    lateinit var ref: DatabaseReference
    lateinit var sellerList: MutableList<User>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_list)

        sellerList = mutableListOf()
        listView = findViewById(R.id.sellerList)
        ref = FirebaseDatabase.getInstance().getReference("User")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sellerList.clear()
                    for (s in snapshot.children) {
                        val seller = s.getValue(User::class.java)
                        if (seller != null) {
                            sellerList.add(seller)
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
}