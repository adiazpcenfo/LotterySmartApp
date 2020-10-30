package com.mobile.lotterysmartapp.activity

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Inventory
import java.time.LocalDateTime


/**
 * @author Allan Diaz
 * **/
class InventoryService (){

    private lateinit var database: DatabaseReference
    private lateinit var databaseInv: DatabaseReference


    private val MESSAGE_ERROR :String = "Error"
    private val FRACTIONS_NUMBER: Int = 10
    private val STATUS_ACT ="ACT"
    private val STATUS_RSV ="RSV"
    private val SEARCHES =0
    private val CERO = 0


    /**
     * @author Allan Diaz
     * Save Buyer  number reserve
     *
     * **/
    private fun addUserInventoryReserve(inventory:Inventory,fractions: Int,email:String){

        inventory.Id = getInventoryKey()
        inventory.availableFractions=CERO
        inventory.fractions=fractions
        inventory.searches=CERO
        inventory.state=STATUS_RSV
        inventory.userEmail = email
        inventory.reserveDate = LocalDateTime.now().toString()

        databaseInv.child(inventory.Id).setValue(inventory)
    }


    /**
     * @author Allan Diaz
     * Calculate number of fraction rest to object, save seller number, create new register to buyer with reserve number and fractions
     * */
    fun reserveNumber(inventory: Inventory,fractions:Int,email:String):Boolean{

        databaseInv= FirebaseDatabase.getInstance().getReference("Inventory")

        inventory.availableFractions= inventory.availableFractions-fractions

        databaseInv.child(inventory.Id).setValue(inventory)

        addUserInventoryReserve(inventory,fractions,email)

        return true

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
            sellerInventory.sellerEmail = sellerInventory.userEmail

            database.child("Inventory").child(sellerInventory.Id).setValue(sellerInventory)
        }

    }

    private fun getInventoryKey():String{
        database = Firebase.database.reference
        var key = database.child("Inventory").push().key

        return key.toString()
    }

}