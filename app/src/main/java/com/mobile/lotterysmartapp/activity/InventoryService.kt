package com.mobile.lotterysmartapp.activity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.mobile.lotterysmartapp.model.Inventory

class InventoryService (){

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val MESSAGE_ERROR :String = "Error"
    private val FRACTIONS_NUMBER: Int = 10
    private val STATUS_ACT ="ACT"

    public fun addUserInventory(inventory:Inventory){



    }

    public fun addSellerInventory(inventory:Inventory){

        var sellerInventory = inventory;
        val iterator =(0..99).iterator()
        sellerInventory.number=0

        iterator.forEach {
            sellerInventory.Id=getInventoryKey()

            if(sellerInventory.number>0){
                sellerInventory.number = it

            }
            sellerInventory.fractions = FRACTIONS_NUMBER
            sellerInventory.availableFractions = FRACTIONS_NUMBER
            sellerInventory.state=STATUS_ACT

            database.child("Inventory").child(sellerInventory.Id).setValue(sellerInventory)
        }

    }

    private fun getInventoryKey():String{
        var key = database.child("Inventory").push().key
        return key.toString()

    }

    private fun getLastRegister(): Query {

        var query: Query
        query = database.child("Inventory").limitToFirst(1)

        val json = query.toString()
        return query

    }
}