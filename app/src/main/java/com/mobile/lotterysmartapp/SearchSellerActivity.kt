package com.mobile.lotterysmartapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import com.google.firebase.database.DatabaseReference
import com.mobile.lotterysmartapp.model.User
import kotlinx.android.synthetic.main.activity_authentication.*

class SearchSellerActivity : AppCompatActivity() {

    lateinit var btnSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_seller)

        btnSearch = findViewById(R.id.buttonSearchSeller)

        btnSearch.setOnClickListener {
            val intent = Intent(this@SearchSellerActivity, SellerListActivity::class.java)
            startActivity(intent)
        }
    }
}