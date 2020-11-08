package com.mobile.lotterysmartapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.User
import com.mobile.lotterysmartapp.util.AlertUtil
import kotlinx.android.synthetic.main.activity_modify_draw_results.*
import java.lang.Exception
import java.util.ArrayList

/**
 * Class in charge of Modify Draw Results Activity.
 *
 * @author Franklin Cardenas
 */
class ModifyDrawResultsActivity : AppCompatActivity() {
    private val drawReference = FirebaseDatabase.getInstance().getReference("Draw")
    private var drawSelectedValue: String = ""
    private var selectedDraw: Draw? = null
    private var drawList: ArrayList<Draw> = arrayListOf()
    private val draw = "Sorteo"
    private val alertUtil = AlertUtil()
    private val success = "Éxito"
    private val error = "Error"
    private val errorMessage = "Sucedió un error al guardar. Por favor inténtelo de nuevo."
    private val sucessfullSave = "Se han guardado los resultados de "
    private lateinit var drawSpinner: Spinner


    /**
     * On Create method for Modify Draw Results Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_draw_results)
        drawSpinner = findViewById(R.id.modifyDrawSpinner)

        resultsLayout.visibility = View.INVISIBLE

        setup()

    }

    /**
     * Setup process for Modify Draw Results Activity
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        setupSaveResultButton()
        drawSpinner()
    }

    /**
     * Setup for Save Result Button.
     *
     * @author Franklin Cardenas
     */
    private fun setupSaveResultButton() {
        saveResultsButton.setOnClickListener {
            if (selectedDraw != null) {
                try {
                    selectedDraw!!.result1 = resultInput1.text.toString()
                    selectedDraw!!.result2 = resultInput2.text.toString()
                    selectedDraw!!.result3 = resultInput3.text.toString()
                    drawReference.child(selectedDraw!!.id).setValue(selectedDraw)
                    alertUtil.simpleAlert(success, sucessfullSave + selectedDraw!!.name, this)
                } catch (exception: Exception) {
                    alertUtil.simpleAlert(error,errorMessage ,this)
                }
            }
        }
    }

    /**
     *  Fill spinner with draw name data and get value from spinner
     *
     * @author Josue Calderón Varela
     */
    private fun drawSpinner() {

        var drawOptions: ArrayList<String>

        drawReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                drawOptions = arrayListOf(draw)
                if (snapshot.exists()) {

                    for (d in snapshot.children) {
                        val draw = d.getValue(Draw::class.java)
                        if (draw != null) {
                            drawOptions.add(draw.name)
                            drawList.add(draw)
                        }
                    }

                    drawSpinner.adapter = ArrayAdapter<String>(
                        this@ModifyDrawResultsActivity,
                        android.R.layout.simple_list_item_1,
                        drawOptions
                    )

                }

                drawSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {

                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            //Get Draw name from selected item
                            drawSelectedValue = parent!!.getItemAtPosition(position).toString()

                            if (drawSelectedValue == draw) {
                                resultsLayout.visibility = View.INVISIBLE
                            } else {
                                resultsLayout.visibility = View.VISIBLE
                            }

                            loadResults()
                        }
                    }
            }

        })
    }

    /**
     * Load each result input with the selected Draw results.
     *
     * @author Franklin Cardenas
     */
    private fun loadResults() {
        for (draw in drawList) {
            if (draw.name == drawSelectedValue) {
                selectedDraw = draw
                resultInput1.setText(draw.result1)
                resultInput2.setText(draw.result2)
                resultInput3.setText(draw.result3)
                break
            }
        }
    }

}