package com.mobile.lotterysmartapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobile.lotterysmartapp.model.User

class SellerListAdapter(val mCtx: Context, val layoutId: Int, val sellerList: List<User>) :
    ArrayAdapter<User>(mCtx, layoutId, sellerList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val name = view.findViewById<TextView>(R.id.nameTextView)
        val email = view.findViewById<TextView>(R.id.emailTextView)

        val seller = sellerList[position]

        name.text = seller.name
        email.text = seller.email


        return view
    }


}