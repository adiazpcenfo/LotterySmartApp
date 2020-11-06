package com.mobile.lotterysmartapp.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Inventory

class ReservedListAdapter(
    private val mCtx: Context,
    private val layoutId: Int,
    private val sellerList: MutableList<Inventory>
) :
    ArrayAdapter<Inventory>(mCtx, layoutId, sellerList) {

    private lateinit var inventoryReference: DatabaseReference
    private var state = ""

    /**
     * Load data into a custom listView to be used in a main listView
     * Get data from firebase to use a user data and complete an specific textView into a custom
     * listView.
     *
     * @author Josue Calderon Varela
     * @param position sellerList position
     * @param convertView view
     * @param parent ViewGroup
     * @return data view
     */
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")


        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val number = view.findViewById<TextView>(R.id.numberId)
        val series = view.findViewById<TextView>(R.id.serieId)
        val date = view.findViewById<TextView>(R.id.dateId)
        val buyer = view.findViewById<TextView>(R.id.buyerId)

        val button = view.findViewById<Button>(R.id.sellerButton)

        val seller = sellerList[position]

        seller.state = state

        number.text = seller.number.toString()
        series.text = seller.series
        date.text = seller.reserveDate
        buyer.text = seller.userEmail

        button.setOnClickListener {

            val builder = AlertDialog.Builder(mCtx)

            builder.setTitle("Vender número")
            builder.setMessage("Seleccione aceptar para confirmar la venta.")

            builder.setPositiveButton("Aceptar") { _, _ ->

                val  myDatabase = FirebaseDatabase.getInstance().getReference("Inventory")

                seller.state = "SOLD"

                val inventory = Inventory(seller.Id, seller.drawName, seller.userEmail, seller.sellerEmail, seller.state, seller.number, seller.series, seller.availableFractions, seller.fractions, seller.reserveDate, seller.searches)
                myDatabase.child(seller.Id).setValue(inventory)

                Toast.makeText(mCtx,"Vendido", Toast.LENGTH_SHORT).show()

            }

            builder.setNegativeButton("Cancelar") { _, _ ->

                Toast.makeText(mCtx,"Cancelar", Toast.LENGTH_SHORT).show()

            }

            builder.show()

        }

        return view

    }
}