package com.mobile.lotterysmartapp.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.*
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Inventory
import com.mobile.lotterysmartapp.model.User

/**
 * Class in charge of Seller List Adapter.
 *
 * @author Josue Calderon Varela
 */
class SellerListAdapter(
    private val mCtx: Context,
    private val layoutId: Int,
    private val sellerList: MutableList<Inventory>
) :
    ArrayAdapter<Inventory>(mCtx, layoutId, sellerList) {

    private lateinit var ref: DatabaseReference
    private lateinit var userList: MutableList<User>


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

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)
        val inventoryService = InventoryService()

        userList = mutableListOf()

        val name = view.findViewById<TextView>(R.id.nameTextView)
        val series = view.findViewById<TextView>(R.id.seriesTextView)
        val button = view.findViewById<Button>(R.id.buttonViewAddress)
        val buttonReserve = view.findViewById<Button>(R.id.buttonReserve)

        val seller = sellerList[position]

        val spinnerAvailable:Spinner = view.findViewById<Spinner>(R.id.spinnerAvailable)

        ref = FirebaseDatabase.getInstance().getReference("User")

        ref.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    userList.clear()

                    for (s in snapshot.children) {

                        val user = s.getValue(User::class.java)

                        if (user != null && seller.userEmail == user.email) {

                            userList.add(user)

                            name.text = user.name
                            series.text = seller.series


                            var availableOptions = ArrayList<Int>()
                            val iterator =(1..seller.availableFractions).iterator()

                            iterator.forEach {
                                availableOptions.add(it)
                            }


                            val adapter = this@SellerListAdapter?.let {
                                spinnerAvailable.adapter =ArrayAdapter<Int>(
                                    mCtx,
                                    android.R.layout.simple_spinner_item,
                                    availableOptions
                                )

                            }

                            //spinnerAvailable.adapter(ArrayAdapter<String>)

                            button.setOnClickListener {

                                val intent = Intent(mCtx, SellerAddressActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                intent.putExtra("coordinatesX", user.coordinatesX)
                                intent.putExtra("coordinatesY", user.coordinatesY)
                                intent.putExtra("name", user.name)

                                mCtx.startActivity(intent)

                            }

                            buttonReserve.setOnClickListener{


                               if(inventoryService.reserveNumber(seller,spinnerAvailable.selectedItem.toString().toInt())){

                                   val alertBuilder = AlertDialog.Builder(mCtx)
                                   alertBuilder.setTitle("Alert")
                                   alertBuilder.setMessage("Reserva realizada con exito")
                                   alertBuilder.setPositiveButton("Aceptar", null)
                                   val dialog: AlertDialog = alertBuilder.create()
                                   dialog.show()


                               }


                            }



                        }
                    }
                }
            }
        })

        return view

    }


}