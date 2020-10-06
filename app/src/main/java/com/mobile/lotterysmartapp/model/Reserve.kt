package com.mobile.lotterysmartapp.model

import java.util.*


/**
 * Class that represents Reserve collection on the database.
 *
 * @author Franklin Cardenas
 */
class Reserve {

    var userEmail : String
        get() = this.userEmail
        set(userEmail) {
            this.userEmail = userEmail
        }
    var inventoryId : String
        get() = this.inventoryId
        set(inventoryId) {
            this.inventoryId = inventoryId
        }
    var fractions : Int
        get() = this.fractions
        set(fractions) {
            this.fractions = fractions
        }
    var expireDate : Date
        get() = this.expireDate
        set(expireDate) {
            this.expireDate = expireDate
        }
    var state : String
        get() = this.state
        set(state) {
            this.state = state
        }
}