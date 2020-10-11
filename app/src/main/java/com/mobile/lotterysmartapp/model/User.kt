package com.mobile.lotterysmartapp.model

import android.text.Editable

/**
 * Class that represents User collection on the database.
 *
 * @author Franklin Cardenas
 */
class User {
    var email = ""
    var name = ""
    var middleName = ""
    var userType = ""
    var coordinatesX = ""
    var coordinatesY = ""

    constructor(
        email: String,
        name: String,
        middleName: String,
        userType: String,
        coordinatesX: String,
        coordinatesY: String
    ){
        this.email = email
        this.name = name
        this.middleName = middleName
        this.userType = userType
        this.coordinatesX = coordinatesX
        this.coordinatesY = coordinatesY
    }

}

enum class userType(val type:String){
    SELLER("Vendedor"),
    BUYER("Comprador")
}