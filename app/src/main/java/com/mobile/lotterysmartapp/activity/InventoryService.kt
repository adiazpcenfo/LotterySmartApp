package com.mobile.lotterysmartapp.activity

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.ArrayList


/**
 * @author Allan Diaz
 * **/
class InventoryService (){

    private lateinit var database: DatabaseReference
    private lateinit var databaseInv: DatabaseReference
    private lateinit var inventoryReference: DatabaseReference
    private lateinit var drawReference: DatabaseReference
    private lateinit var inventoryReservedList: ArrayList<Inventory>
    private lateinit var inventoryActiveList: ArrayList<Inventory>
    //private lateinit var drawInventoryList: ArrayList<Inventory>


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
        inventory.reserveDate = LocalDate.now().toString()

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


    /**
     * @author Allan Diaz
     * Calculate if reserve number is expired
     * **/
    private fun validateReserveDates(reservedDate:String):Boolean{

        //Date format yyyy-MM-dd
        val date1 = LocalDate.parse(reservedDate)
        val date2 = LocalDate.parse(LocalDate.now().toString())

        var result:Boolean=false

        val period = Period.between(date1, date2)

        var numPeriod = period.toString().substring(1,2)
        var dateType = period.toString().substring(2,3)

        if(dateType=="M"|| dateType=="Y"){
            result = true
        }else{
            if(numPeriod.toInt()>2){
                result = true
            }

        }
        return result
    }

    /**
     * @author Allan Diaz
     *
     * Get Inventories Reserved and Active
     */

    fun validateReservedNumbers(inventoryList : ArrayList<Inventory>) {

        inventoryActiveList= arrayListOf()
        inventoryReservedList= arrayListOf()

        databaseInv= FirebaseDatabase.getInstance().getReference("Inventory")


        for (inventoryData in inventoryList) {

           // val inventoryData = inventory.getValue(Inventory::class.java)

            if (inventoryData != null) {

                if(inventoryData.state==STATUS_ACT) inventoryActiveList.add(inventoryData)

                if(inventoryData.state==STATUS_RSV) inventoryReservedList.add(inventoryData)

            }

        }

        if(inventoryReservedList.size>0){
            for(inventoryO in inventoryReservedList){

                if(inventoryO.state==STATUS_RSV && validateReserveDates(inventoryO.reserveDate)){


                    for(activeInventory in inventoryActiveList){
                        if(activeInventory.userEmail == inventoryO.sellerEmail && activeInventory.number==inventoryO.number &&
                            activeInventory.drawName == inventoryO.drawName
                        ){

                            var totalFractions =  activeInventory.availableFractions + inventoryO.fractions
                            activeInventory.availableFractions= totalFractions

                            databaseInv.child(activeInventory.Id).setValue(activeInventory)

                            inventoryO.state="DEL"
                            databaseInv.child(inventoryO.Id).setValue(inventoryO)
                            break
                        }
                    }

                }
            }
            inventoryReservedList.clear()
            inventoryActiveList.clear()
        }


    }
    /***
     * @author Allan Diaz
     * Method for buyer return reserved number to seller
     * **/
    public fun returnReservedNumber(buyerReservedNumber:Inventory,inventoryList : ArrayList<Inventory>){
        databaseInv= FirebaseDatabase.getInstance().getReference("Inventory")
        if(buyerReservedNumber.state==STATUS_RSV){


            for(activeInventory in inventoryList){
                if(activeInventory.userEmail == buyerReservedNumber.sellerEmail && activeInventory.number==buyerReservedNumber.number
                    && activeInventory.series==buyerReservedNumber.series && activeInventory.drawName==buyerReservedNumber.drawName){

                    var totalFractions =  activeInventory.availableFractions + buyerReservedNumber.fractions
                    activeInventory.availableFractions= totalFractions

                    databaseInv.child(activeInventory.Id).setValue(activeInventory)

                    buyerReservedNumber.state="DEL"
                    databaseInv.child(buyerReservedNumber.Id).setValue(buyerReservedNumber)
                    break
                }
            }

        }
    }

    /**
     *Delete Draw inventory from database
     * Delete Draw from Database
     *
     * @author Allan Diaz
     */
    fun deleteDrawInventory(drawName:String){
        deleteDrawInventoryData(drawName)
        deleteDrawData(drawName)
    }

    /**
     *Load all inventories from database
     *
     * @author Allan Diaz
     */
    private fun deleteDrawInventoryData(drawDes:String) {

        inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")

        inventoryReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                //drawInventoryList.clear()

                for (inventory in snapshot.children) {


                    val inventoryData = inventory.getValue(Inventory::class.java)

                    if (inventoryData != null && inventoryData.drawName==drawDes) {

                        //drawInventoryList.add(inventoryData)
                        inventory.ref.removeValue()

                    }
                }
            }
        })
    }

    private fun deleteDrawData(drawDes:String) {

        drawReference = FirebaseDatabase.getInstance().getReference("Draw")

        drawReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                for (draw in snapshot.children) {


                    val drawData = draw.getValue(Draw::class.java)

                    if (drawData != null && drawData.name==drawDes) {

                        //drawInventoryList.add(inventoryData)
                        draw.ref.removeValue()

                    }
                }
            }
        })
    }

}