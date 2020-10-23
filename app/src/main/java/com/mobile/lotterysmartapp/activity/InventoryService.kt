package com.mobile.lotterysmartapp.activity

import android.content.Context
import android.content.Context.*
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.Provider
import kotlinx.android.synthetic.main.activity_authentication.*

class InventoryService (){

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val MESSAGE_ERROR :String = "Error"
    private val FRACTIONS_NUMBER: Int = 10
    private val STATUS_ACT ="ACT"

    fun addUserInventory(inventory:Inventory){



    }

    fun addSellerInventory(inventory:Inventory){

        var sellerInventory = inventory;
        val iterator =(0..99).iterator()
        sellerInventory.number=0

        iterator.forEach {
            sellerInventory.Id=getInventoryKey()

          //  if(sellerInventory.number>0 && it>0){
                sellerInventory.number = it

            //}else{
             //   sellerInventory.number=1
            //}
            sellerInventory.fractions = FRACTIONS_NUMBER
            sellerInventory.availableFractions = FRACTIONS_NUMBER
            sellerInventory.state=STATUS_ACT

            database.child("Inventory").child(sellerInventory.Id).setValue(sellerInventory)
        }

    }

    private fun getInventoryKey():String{
        database = Firebase.database.reference
        var key = database.child("Inventory").push().key
        return key.toString()

    }

    private fun getLastRegister(): Query {

        var query: Query = database.child("Inventory").limitToFirst(1)

        val json = query.toString()
        return query

    }


}