package com.mobile.lotterysmartapp.model

/**
 * Class that represents User collection on the database.
 *
 * @author Franklin Cardenas
 */
class User {
    var email : String
        get() = this.email
        set(email) {
            this.email = email
        }
    var name : String
        get() = this.name
        set(name) {
            this.name = name
        }
    var middleName : String
        get() = this.middleName
        set(middleName) {
            this.middleName = middleName
        }
    var userType : String
        get() = this.userType
        set(userType) {
            this.userType = userType
        }
    var coordinatesX : String
        get() = this.coordinatesX
        set(coordinatesX) {
            this.coordinatesX = coordinatesX
        }
    var coordinatesY : String
        get() = this.coordinatesY
        set(coordinatesY) {
            this.coordinatesY = coordinatesY
        }
}