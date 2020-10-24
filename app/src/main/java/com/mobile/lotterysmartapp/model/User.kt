package com.mobile.lotterysmartapp.model

import android.text.Editable

/**
 * Class that represents User collection on the database.
 *
 * @author Franklin Cardenas
 */
class User {
    var id = ""
    var email = ""
    var name = ""
    var middleName = ""
    var userType = ""
    var coordinatesX = 0.0
    var coordinatesY = 0.0

    constructor()

    constructor(
        id: String,
        email: String,
        name: String,
        middleName: String,
        userType: String,
        coordinatesX: Double,
        coordinatesY: Double
    ){
        this.id = id
        this.email = email
        this.name = name
        this.middleName = middleName
        this.userType = userType
        this.coordinatesX = coordinatesX
        this.coordinatesY = coordinatesY
    }

}

enum class UserType(val type:String){
    SELLER("Vendedor"),
    BUYER("Comprador")
}