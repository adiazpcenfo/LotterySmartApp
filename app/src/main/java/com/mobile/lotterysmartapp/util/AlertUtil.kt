package com.mobile.lotterysmartapp.util

import android.app.AlertDialog
import android.content.Context

/**
 * Utilitarian class for alert creation
 *
 *@author Franklin Cardenas
 * */
class AlertUtil {
    private val accept = "Aceptar"

    /**
     * Create an alert with title and message.
     * Just gives the accept button.
     *
     * @param title String
     * @param message String
     * @author Franklin Cardenas
     */
    fun simpleAlert(title : String?, message : String?, context : Context) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton(accept, null)
        val dialog : AlertDialog = alertBuilder.create()
        dialog.show()
    }
}