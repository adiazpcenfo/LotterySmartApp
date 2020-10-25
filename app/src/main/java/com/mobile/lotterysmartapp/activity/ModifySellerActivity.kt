package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.User
import kotlinx.android.synthetic.main.activity_modify_seller.*
import kotlinx.android.synthetic.main.activity_modify_user.*

class ModifySellerActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private var queryListener: ValueEventListener? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var query: Query
    private lateinit var tempUser: User
    private var email: String? = null
    private val userType = com.mobile.lotterysmartapp.model.UserType.SELLER.type
    private val nullData = "No se ha modificado la información."
    private val nullAlert = "¡No hay datos para modificar!"
    private val saveData = "Se guardaron los nuevos datos."
    private val successAlert = "¡Éxito!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_seller)

        //Initialize variables
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        email = preferences.getString(Constants.EMAIL, null)

        ref = FirebaseDatabase.getInstance().getReference("User")

        //Setup for the view
        loadData()
        getData()
    }


    /**
     * Load the data of the profile
     *
     * @author Jimena Vega
     */
    private fun loadData() {
        query = ref.orderByChild("email").equalTo(email)

        queryListener?.let { query.addValueEventListener(it) }

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) {
                        tempUser = user
                        textNameModifySeller.hint = tempUser.name
                    }
                }
            }
        })
    }

    /**
     * Gets the data to save
     *
     * @author Jimena Vega
     */
    private fun getData() {
        buttonModifyUser.setOnClickListener {
            if (!textNameModifySeller.text.toString()
                    .isNullOrBlank() && !textMiddleNameModifyUser.text.toString().isNullOrBlank()
            ) {
                modifyNameSeller()
                toProfile()
            } else {
                alert(nullAlert, nullData)
            }

        }
    }

    /**
     * Saves the new name
     *
     * @author Jimena Vega
     */
    private fun modifyNameSeller() {
        ref.child(tempUser.id).setValue(
            User(
                tempUser.id,
                tempUser.email,
                textNameModifySeller.text.toString(),
                tempUser.middleName,
                userType,
                tempUser.coordinatesX,
                tempUser.coordinatesY
            )
        )
        alert(successAlert, saveData)
    }

    /**
     * Show alert in case the new account could not be created.
     *
     * @param title a string with the title to show
     * @param message a string with the message to show
     *
     * @author Franklin Cardenas
     */
    private fun alert(title: String?, message: String?) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = alertBuilder.create()
        dialog.show()
    }

    /**
     * Clear the inputs from the form.
     *
     * @author Jimena Vega
     */
    private fun clearForm() {
        textNameModifySeller.text.clear()
    }

    /**
     * Sends the user to the profile view.
     *
     * @author Jimena Vega
     */
    private fun toProfile() {
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        clearForm()
        intent = Intent(this, ProfileSellerActivity::class.java)
        startActivity(intent)
        finish()
    }
}