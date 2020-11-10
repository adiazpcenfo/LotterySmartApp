package com.mobile.lotterysmartapp.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Draw
import com.mobile.lotterysmartapp.util.AlertUtil
import kotlinx.android.synthetic.main.activity_delete_draw.*
import kotlinx.android.synthetic.main.activity_modify_draw_results.*
import java.lang.Exception
import java.util.ArrayList

/**
 * Class in charge of Delete Draw Results Activity.
 *
 * @author Franklin Cardenas
 */
class DeleteDrawActivity : AppCompatActivity() {

    private val drawReference = FirebaseDatabase.getInstance().getReference("Draw")
    private var drawSelectedValue: String = ""
    private var selectedDraw: Draw? = null
    private var drawList: ArrayList<Draw> = arrayListOf()
    private val draw = "Sorteo"
    private val alertUtil = AlertUtil()
    private val success = "Éxito"
    private val error = "Error"
    private val errorMessage = "Sucedió un error al Eliminar. Por favor inténtelo de nuevo."
    private val sucessfullDelete = "Se ha eliminado el sorteo con éxito. "
    private lateinit var drawSpinner: Spinner
    val inventoryService = InventoryService()

    /**
     * On Create method for Modify Draw Results Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_draw)
        drawSpinner = findViewById(R.id.deleteDrawSpinner)

        deleteDrawLayout.visibility = View.INVISIBLE

        setup()

    }

    /**
     * Setup process for Modify Draw Results Activity
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        setupDeleteButton()
        drawSpinner()
    }

    /**
     * Setup for Delete Button.
     *
     * @author Franklin Cardenas
     */
    private fun setupDeleteButton() {
        deleteDrawButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Eliminar Sorteo")
            builder.setMessage("Seleccione aceptar para confirmar que desea eliminar el sorteo.")

            builder.setPositiveButton("Aceptar") { _, _ ->

                var drawName:String = drawSpinner.selectedItem.toString()
                inventoryService.deleteDrawInventory(drawName)

                Toast.makeText(this,"Devuelto", Toast.LENGTH_SHORT).show()

            }

            builder.setNegativeButton("Cancelar") { _, _ ->

                Toast.makeText(this,"Cancelar", Toast.LENGTH_SHORT).show()

            }

            builder.show()


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
                        this@DeleteDrawActivity,
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
                                deleteDrawLayout.visibility = View.INVISIBLE
                            } else {
                                deleteDrawLayout.visibility = View.VISIBLE
                            }

                        }
                    }
            }

        })
    }

}