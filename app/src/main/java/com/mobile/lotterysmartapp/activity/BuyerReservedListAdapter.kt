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

class BuyerReservedListAdapter(
    private val mCtx: Context,
    private val layoutId: Int,
    private val buyerList: MutableList<Inventory>,
    private val inventoryList: ArrayList<Inventory>
    ) : ArrayAdapter<Inventory>(mCtx, layoutId, buyerList) {


        private lateinit var inventoryReference: DatabaseReference
        private var state = ""
        val inventoryService = InventoryService()

        /**
         * Load data into a custom listView to be used in a main listView
         * Get data from firebase to use a user data and complete an specific textView into a custom
         * listView.
         *
         * @author Allan Diaz
         * @param position buyerList position
         * @param convertView view
         * @param parent ViewGroup
         * @return data view
         */
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            inventoryReference = FirebaseDatabase.getInstance().getReference("Inventory")


            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view: View = layoutInflater.inflate(layoutId, null)

            val number = view.findViewById<TextView>(R.id.numberId3)
            val series = view.findViewById<TextView>(R.id.serieId3)
            val date = view.findViewById<TextView>(R.id.dateId3)

            val button = view.findViewById<Button>(R.id.buyerButton)

            val buyer = buyerList[position]

           // buyer.state = state

            if(buyer.number!=null){
                number.text = buyer.number.toString()
            }

            series.text = buyer.series
            date.text = buyer.reserveDate

            button.setOnClickListener {

                val builder = AlertDialog.Builder(mCtx)

                builder.setTitle("Devolver número")
                builder.setMessage("Seleccione aceptar para confirmar la devolución.")

                builder.setPositiveButton("Aceptar") { _, _ ->

                  //  val  myDatabase = FirebaseDatabase.getInstance().getReference("Inventory")

                    //buyer.state = "DEL"

                  //  val inventory = Inventory(buyer.Id, buyer.drawName, buyer.userEmail, buyer.sellerEmail, buyer.state, buyer.number, buyer.series, buyer.availableFractions, buyer.fractions, buyer.reserveDate, buyer.searches)
                  //  myDatabase.child(buyer.Id).setValue(inventory)
                    inventoryService.returnReservedNumber(buyer,inventoryList)

                    Toast.makeText(mCtx,"Devuelto", Toast.LENGTH_SHORT).show()

                }

                builder.setNegativeButton("Cancelar") { _, _ ->

                    Toast.makeText(mCtx,"Cancelar", Toast.LENGTH_SHORT).show()

                }

                builder.show()

            }

            return view

        }

}