package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.User
import kotlinx.android.synthetic.main.activity_modify_user.*

class ModifyUserActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private var queryListener: ValueEventListener? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var query: Query
    private lateinit var tempUser: User
    private var email: String? = null
    private val userType = com.mobile.lotterysmartapp.model.userType.BUYER.type
    private val saveData = "Se guardaron los nuevos datos."
    private val successAlert = "¡Éxito!"
    private val nullData = "No se ha modificado la información."
    private val nullAlert = "¡No hay datos para modificar!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user)

        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        email = preferences.getString(Constants.EMAIL, null)

        ref = FirebaseDatabase.getInstance().getReference("User")

        //Makes the setup for the view
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
                        textNameModifyUser.hint = tempUser.name
                        textMiddleNameModifyUser.hint = tempUser.middleName
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
            if (!textNameModifyUser.text.toString()
                    .isNullOrBlank() && !textMiddleNameModifyUser.text.toString().isNullOrBlank()
            ) {
                modifyBoth()
            } else if (!textMiddleNameModifyUser.text.toString().isNullOrBlank()) {
                modifyOnlyMiddleName()
            } else if (!textNameModifyUser.text.toString().isNullOrBlank()) {
                modifyOnlyName()
            } else if (textNameModifyUser.text.toString()
                    .isNullOrBlank() or textMiddleNameModifyUser.text.toString().isNullOrBlank()
            ) {
                alert(nullAlert, nullData)
            }
        }
    }

    /**
     * Saves the new name
     *
     * @author Jimena Vega
     */
    private fun modifyOnlyName() {
        ref.child(tempUser.id).setValue(
            User(
                tempUser.id,
                tempUser.email,
                textNameModifyUser.text.toString(),
                tempUser.middleName,
                userType,
                0.0,
                0.0
            )
        )
        alert(successAlert, saveData)
    }

    /**
     * Saves the new middle name
     *
     * @author Jimena Vega
     */
    private fun modifyOnlyMiddleName() {
        ref.child(tempUser.id).setValue(
            User(
                tempUser.id,
                tempUser.email,
                tempUser.name,
                textMiddleNameModifyUser.text.toString(),
                userType,
                0.0,
                0.0
            )
        )
        alert(successAlert, saveData)
    }

    /**
     * Saves both, name and middle name
     *
     * @author Jimena Vega
     */
    private fun modifyBoth() {
        ref.child(tempUser.id).setValue(
            User(
                tempUser.id,
                tempUser.email,
                textNameModifyUser.text.toString(),
                textMiddleNameModifyUser.text.toString(),
                userType,
                0.0,
                0.0
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
        textNameModifyUser.text.clear()
        textMiddleNameModifyUser.text.clear()
    }

    /**
     * Send the user to the home view when the back button is used
     *
     * @author Jimena Vega
     */
//    override fun onBackPressed() {
//        clearForm()
//        val intentToBack = Intent(this, HomeActivity::class.java)
//        intentToBack.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intentToBack)
//    }

}