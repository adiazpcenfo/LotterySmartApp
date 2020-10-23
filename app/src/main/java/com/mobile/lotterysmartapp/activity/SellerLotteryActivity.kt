package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.Provider
import kotlinx.android.synthetic.main.activity_authentication.*
import kotlinx.android.synthetic.main.activity_draw_maintenance.*
import kotlinx.android.synthetic.main.activity_seller_lottery.*


/**
 * Class in charge of Forget Password Activity.
 *
 * @author Allan Diaz
 * **/
class SellerLotteryActivity : AppCompatActivity(){

    //private lateinit var database: DatabaseReference
    //private lateinit var auth: FirebaseAuth
    //private val preferences: SharedPreferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
    private val MESSAGE_ERROR :String = "Error"
    private val MESSAGE_ALERT :String = "Alert"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_seller_lottery)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Seller Lottery")
        analytics.logEvent("SellerLotteryScreen", bundle)


        setupActivity()
    }

    /**
     * Metodo para incializar la pantalla
     * @author Allan Diaz
     * ***/
    fun setupActivity(){
        val inventoryService =InventoryService()
        var inventory =Inventory()


        button_sellerLotterySave.setOnClickListener{

            inventory.series = editTextSeriesNumber.text.toString()
           session(inventory)

            if(validateForm(inventory)) {
                inventoryService.addSellerInventory(inventory)
                alert("Registro exitoso",MESSAGE_ALERT)
                cleanForm()
            }else{
                return@setOnClickListener
            }
        }

        button_drawCancel2.setOnClickListener{
            cleanForm()
            var intentMenu = Intent()
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentMenu = Intent(this, HomeActivity::class.java)
            startActivity(intentMenu)
        }

    }

    private fun validateForm(inventory: Inventory):Boolean{
        var isValidForm =true
        var alertMessage: String=""

        if(isValidForm && inventory.series.isEmpty()){

            alert("Debe ingresar la serie del sorteo",MESSAGE_ERROR)
            isValidForm=false
        }

        return isValidForm
    }

    private fun alert(messageAlert:String, alertType:String) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(alertType)
        alertBuilder.setMessage(messageAlert)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = alertBuilder.create()
        dialog.show()
    }


    private fun cleanForm(){

        editTextSeriesNumber.text.clear()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }
    private fun session(inventory: Inventory) {
       val preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        val email = preferences.getString(Constants.EMAIL, null)
        val provider = preferences.getString(Constants.PROVIDER, null)

        inventory.userEmail = email.toString()
    }

}