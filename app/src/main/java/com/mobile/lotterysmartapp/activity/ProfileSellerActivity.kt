package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.User
import kotlinx.android.synthetic.main.activity_profile_seller.*
import kotlinx.android.synthetic.main.activity_profile_user.*

class ProfileSellerActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private var queryListener: ValueEventListener? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var query: Query
    private lateinit var tempUser: User
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_seller)

        //Initialize variables
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        email = preferences.getString(Constants.EMAIL, null)
        ref = FirebaseDatabase.getInstance().getReference("User")

        //Setup for the view
        loadData()
        modifyUserSeller()
    }

    /**
     * Load the modify view when pressed
     *
     * @author Jimena Vega
     */
    private fun modifyUserSeller(){
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        buttonModifySellerProfile.setOnClickListener{
            intent = Intent(this, ModifySellerActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Load and set the data of the profile
     *
     * @author Jimena Vega
     */
    private fun loadData() {

        query = ref.orderByChild("email").equalTo(email)

        queryListener?.let { query.addValueEventListener(it) }

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) {
                        tempUser = user
                        nameSellerValueLbl.text = tempUser.name
                        emailSellerValueLbl.text = tempUser.email
                    }
                }
            }
        })
    }
}