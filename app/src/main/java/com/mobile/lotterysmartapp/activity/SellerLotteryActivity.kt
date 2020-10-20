package com.mobile.lotterysmartapp.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Inventory


/**
 * Class in charge of Forget Password Activity.
 *
 * @author Allan Diaz
 * **/
class SellerLotteryActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_seller_lottery)

        setupActivity()
    }

    /**
     * Metodo para incializar la pantalla
     * @author Allan Diaz
     * ***/
    fun setupActivity(){
        val inventoryService =InventoryService()
        var inventory =Inventory()


        inventoryService.addSellerInventory(inventory)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }


}