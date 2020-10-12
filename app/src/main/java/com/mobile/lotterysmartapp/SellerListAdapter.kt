package com.mobile.lotterysmartapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.User

class SellerListAdapter(
    val mCtx: Context,
    val layoutId: Int,
    val sellerList: MutableList<Inventory>
) :
    ArrayAdapter<Inventory>(mCtx, layoutId, sellerList) {

    lateinit var ref: DatabaseReference
    lateinit var userList: MutableList<User>


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        userList = mutableListOf()


        val name = view.findViewById<TextView>(R.id.nameTextView)
        val email = view.findViewById<TextView>(R.id.emailTextView)

        val seller = sellerList[position]

        ref = FirebaseDatabase.getInstance().getReference("User")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userList.clear()
                    for (s in snapshot.children) {
                        val user = s.getValue(User::class.java)
                        if (user != null && seller.userEmail == user.email) {
                            userList.add(user)

                            name.text = user.name
                            email.text = seller.userEmail
                        }
                    }
                }
            }
        })
        return view
    }
}