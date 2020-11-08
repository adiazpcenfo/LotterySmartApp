package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import kotlinx.android.synthetic.main.activity_register_draw.*
import java.util.*

class RegisterDrawActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val MESSAGE_ERROR: String = "Error"
    private val MESSAGE_ALERT: String = "Alert"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_draw)

        database = Firebase.database.reference

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Draw Maintenance")
        analytics.logEvent("DrawMaintenanceScreen", bundle)

        calendarMode()
        mantainDraw()
    }

    private fun mantainDraw() {
        this.title = "Draw Mantainance"

        button_drawSave.setOnClickListener {

            getLastRegister()

            val draw = Draw()
            draw.id = ""
            draw.name = editTextName.text.toString()
            draw.result1 = "00-000"
            draw.result2 = "00-000"
            draw.result3 = "00-000"
            draw.drawDate = editTextDrawDate.text.toString()
            draw.state = "ACT"

            if (validateForm(draw)) {

                //obtiene el id unico de la BD, de la entidad con la que esta trabajando
                val key = database.child("Draw").push().key

                if (key != null) {
                    draw.id = key
                    //guarda el valor
                    database.child("Draw").child(key).setValue(draw)
                }
                alert("Registro exitoso", MESSAGE_ALERT)
                clearForm()

            } else {
                return@setOnClickListener
            }
        }
    }

    private fun validateForm(draw: Draw): Boolean {
        var isValidForm = true

        if (isValidForm && draw.name.isEmpty()) {

            alert("Debe ingresar el nombre del sorteo", MESSAGE_ERROR)
            isValidForm = false
        }
        if (isValidForm && draw.result1.isEmpty()) {
            alert("Debe ingresar el primer premio", MESSAGE_ERROR)
            isValidForm = false
        }
        if (isValidForm && draw.result2.isEmpty()) {
            alert("Debe ingresar el segundo premio", MESSAGE_ERROR)
            isValidForm = false
        }
        if (isValidForm && draw.result3.isEmpty()) {
            alert("Debe ingresar el tercer premio", MESSAGE_ERROR)
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

    private fun getLastRegister(): Query {
        return database.child("Draw").limitToFirst(1)
    }

    private fun clearForm() {
        editTextName.text.clear()
        editTextDrawDate.text.clear()
    }

    private fun calendarMode() {
        editTextDrawDate.setOnClickListener {

            closeKeyBoard()

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            var drawDate: String

            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->

                val monthNum = monthOfYear + 1

                drawDate = "$dayOfMonth/$monthNum/$year"
                editTextDrawDate.setText(drawDate)

            }, year, month, day)

            dpd.show()
        }

    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}