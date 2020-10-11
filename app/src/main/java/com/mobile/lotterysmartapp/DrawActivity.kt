package com.mobile.lotterysmartapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.TextKeyListener.clear
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mobile.lotterysmartapp.model.Constants
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Provider
import kotlinx.android.synthetic.main.activity_draw_maintenance.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class DrawActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val draws: MutableList<Draw> = mutableListOf()

    private val MESSAGE_ERROR :String = "Error"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_maintenance)

        database = Firebase.database.reference

        //Set up startup analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Draw Maintenance")
        analytics.logEvent("DrawMaintenanceScreen", bundle)

        calendarMode()
        mantainDraw()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun mantainDraw(){
        this.title = "Draw Mantainance"


        button_drawSave.setOnClickListener {

            getLastRegister()

            val draw = Draw("","ACT",editTextName.text.toString(),
                editTextResult1.text.toString(),
                editTextResult2.text.toString(),
                editTextResult3.text.toString(),
                editTextDrawDate.text.toString()
            )

            if(validateForm(draw)){


                //obtiene el id unico de la BD, de la entidad con la que esta trabajando
                val key = database.child("Draw").push().key


                if (key != null) {
                    draw.Id = key
                    //guarda el valor
                    database.child("Draw").child(key).setValue(draw)
                }

                clearForm()

            }else{
                return@setOnClickListener
            }
        }

        button_drawCancel.setOnClickListener{
            clearForm()

        }

    }

    private fun validateForm(draw:Draw):Boolean{
        var isValidForm =true
        var alertMessage: String=""

        if(isValidForm && draw.name.isEmpty()){
            alert("Debe ingresar el nombre del sorteo",MESSAGE_ERROR)
            isValidForm=false
        }
        if(isValidForm && draw.result1.isEmpty()){
            alert("Debe ingresar el primer premio",MESSAGE_ERROR)
            isValidForm=false
        }
        if(isValidForm && draw.result2.isEmpty()){
            alert("Debe ingresar el segundo premio",MESSAGE_ERROR)
            isValidForm=false
        }
        if(isValidForm && draw.result3.isEmpty()){
            alert("Debe ingresar el tercer premio",MESSAGE_ERROR)
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

    private fun getLastRegister(): Query {

        var query:Query
        query = database.child("Draw").limitToFirst(1)

        val json = query.toString()
        return query

    }

    private fun clearForm(){

        editTextName.text.clear()
        editTextResult1.text.clear()
        editTextResult2.text.clear()
        editTextResult3.text.clear()

    }
    private fun calendarMode(){


        editTextDrawDate.setOnClickListener {

            closeKeyBoard()

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            var drawDate=""

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                var monthNum = monthOfYear.toInt()+1

                 drawDate ="" + dayOfMonth + "/" + monthNum + "/" + year
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


    private fun convertStringToDate(dateS:String): LocalDate{
      //  val string = "May 31, 2020"

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy ", Locale.ENGLISH)
        val date = LocalDate.parse(dateS, formatter)
        return date//java.sql.Timestamp.valueOf(date.toString())
    }
}