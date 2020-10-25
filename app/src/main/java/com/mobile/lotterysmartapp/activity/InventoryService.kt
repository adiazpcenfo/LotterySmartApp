package com.mobile.lotterysmartapp.activity

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.model.Inventory


class InventoryService {

    private lateinit var database: DatabaseReference
    private val FRACTIONS_NUMBER: Int = 10
    private val STATUS_ACT = "ACT"

    fun addSellerInventory(inventory: Inventory) {

        val iterator = (0..99).iterator()
        inventory.number = 0

        iterator.forEach {
            inventory.Id = getInventoryKey()
            inventory.number = it
            inventory.fractions = FRACTIONS_NUMBER
            inventory.availableFractions = FRACTIONS_NUMBER
            inventory.state = STATUS_ACT

            database.child("Inventory").child(inventory.Id).setValue(inventory)
        }

    }

    private fun getInventoryKey(): String {
        database = Firebase.database.reference
        val key = database.child("Inventory").push().key
        return key.toString()

    }

}