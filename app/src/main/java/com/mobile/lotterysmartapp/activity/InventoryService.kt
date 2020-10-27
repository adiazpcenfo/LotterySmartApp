package com.mobile.lotterysmartapp.activity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.model.Inventory



/**
 * @author Allan Diaz
 * **/
class InventoryService (){

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val MESSAGE_ERROR :String = "Error"
    private val FRACTIONS_NUMBER: Int = 10
    private val STATUS_ACT ="ACT"
    private val SEARCHES =0
    private val CERO = 0


    /**
     * @author Allan Diaz
     * Save Buyer  number reserve
     *
     * **/
    private fun addUserInventory(inventory:Inventory,fractions: Int){

        inventory.Id = getInventoryKey()
        inventory.availableFractions=CERO
        inventory.fractions=fractions
        inventory.searches=CERO
        inventory.state=STATUS_ACT

        database.child("Inventory").child(inventory.Id).setValue(inventory)
    }

    /**
     * @author Allan Diaz
     * Calculate number of fraction rest to object, save seller number, create new register to buyer with reserve number and fractions
     * */
    fun reserveNumber(inventory: Inventory,fractions:Int){


        inventory.fractions= inventory.fractions-fractions

        database.child("Inventory").child(inventory.Id).setValue(inventory)

        addUserInventory(inventory,fractions)

    }

    /**
     * @author Allan Diaz
     * create set of numbers for Seller from series
     * */

    fun addSellerInventory(inventory:Inventory){

        var sellerInventory = inventory;
        val iterator =(0..99).iterator()
        sellerInventory.number=0

        iterator.forEach {
            sellerInventory.Id=getInventoryKey()

            sellerInventory.number = it
            sellerInventory.searches=SEARCHES
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

   /* private fun getLastRegister(): Query {

        var query: Query = database.child("Inventory").limitToFirst(1)

        val json = query.toString()
        return query

    }*/


}