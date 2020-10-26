package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import kotlinx.android.synthetic.main.activity_seller_lottery.*


/**
 * Class in charge of Forget Password Activity.
 *
 * @author Allan Diaz
 * **/
class SellerLotteryActivity : AppCompatActivity() {

    private lateinit var databaseDraw: DatabaseReference
    private lateinit var databaseInventory: DatabaseReference
    private val MESSAGE_ERROR: String = "Error"
    private val MESSAGE_ALERT: String = "Alert"
    private val DRAW: String = "Sorteo"

    lateinit var spinnerDraw: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_seller_lottery)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Seller Lottery")
        analytics.logEvent("SellerLotteryScreen", bundle)

        spinnerDraw = findViewById(R.id.spinner_draw)
        databaseDraw = FirebaseDatabase.getInstance().getReference(Constants.DRAW)
        databaseInventory = FirebaseDatabase.getInstance().getReference(Constants.INVENTORY)

        loadDrawSpinner()
        setupActivity()
    }

    /**
     * Metodo para incializar la pantalla
     * @author Allan Diaz
     * ***/
    private fun setupActivity() {
        val inventoryService = InventoryService()
        val inventory = Inventory()


        button_sellerLotterySave.setOnClickListener {

            inventory.series = editTextSeriesNumber.text.toString()
            inventory.drawName = spinner_draw.selectedItem.toString()
            session(inventory)

            if (validateForm(inventory)) {
                inventoryService.addSellerInventory(inventory)
                alert("Registro exitoso", MESSAGE_ALERT)
                cleanForm()
            } else {
                return@setOnClickListener
            }
        }

        button_drawCancel2.setOnClickListener {
            cleanForm()
            var intentMenu = Intent()
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentMenu = Intent(this, HomeActivity::class.java)
            startActivity(intentMenu)
        }

    }

    private fun validateForm(inventory: Inventory): Boolean {
        var isValidForm = true


        if (isValidForm && inventory.drawName == DRAW) {
            alert("Debe seleccionar un Sorteo", MESSAGE_ERROR)
            isValidForm = false
        }

        if (isValidForm && inventory.series.isEmpty()) {

            alert("Debe ingresar la serie del sorteo", MESSAGE_ERROR)
            isValidForm = false
        }

        return isValidForm
    }

    private fun alert(messageAlert: String, alertType: String) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(alertType)
        alertBuilder.setMessage(messageAlert)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = alertBuilder.create()
        dialog.show()
    }


    private fun cleanForm() {

        editTextSeriesNumber.text.clear()
    }


    private fun session(inventory: Inventory) {
        val preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        val email = preferences.getString(Constants.EMAIL, null)


        inventory.userEmail = email.toString()
    }


    private fun loadDrawSpinner() {

        val drawOptions = arrayListOf(DRAW)
        databaseDraw.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (d in snapshot.children) {

                        val draw = d.getValue(Draw::class.java)

                        if (draw != null && draw.state == "ACT") {

                            drawOptions.add(draw.name)
                        }
                    }

                    spinnerDraw.adapter = ArrayAdapter<String>(
                        this@SellerLotteryActivity,
                        android.R.layout.simple_list_item_1,
                        drawOptions
                    )

                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

}