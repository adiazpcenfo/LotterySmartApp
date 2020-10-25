package com.mobile.lotterysmartapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.mobile.lotterysmartapp.R
import kotlinx.android.synthetic.main.activity_select_type_user.*

class TypeUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_type_user)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Type User")
        analytics.logEvent("TypeUserScreen", bundle)

        selectType()
    }

    private fun selectType(){

        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        buttonBuyer.setOnClickListener{
           intent = Intent(this,RegisterUserActivity::class.java)
            startActivity(intent)
        }
        buttonSeller.setOnClickListener{
            intent =  Intent(this,RegisterSellerActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        val intentToBack = Intent(this,AuthenticationActivity::class.java)
        intentToBack.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToBack)
    }
}