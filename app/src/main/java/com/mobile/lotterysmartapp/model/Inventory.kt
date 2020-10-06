package com.mobile.lotterysmartapp.model

/**
 * Class that represents Inventory collection on the database.
 *
 * @author Franklin Cardenas
 */
class Inventory {
    var Id : Long
        get() = this.Id
        set(value) {
            this.Id = value
        }
    var drawId : String
        get() = this.drawId
        set(drawId) {
            this.drawId = drawId
        }
    var userEmail : String
        get() = this.userEmail
        set(userEmail) {
            this.userEmail = userEmail
        }
    var state : String
        get() = this.state
        set(state) {
            this.state = state
        }
    var number : Int
        get() = this.number
        set(number) {
            this.number = number
        }
    var series : Int
        get() = this.series
        set(series) {
            this.series = series
        }
    var fractions : Int
        get() = this.fractions
        set(fractions) {
            this.fractions = fractions
        }
    var availableFractions : Int
        get() = this.availableFractions
        set(availableFractions) {
            this.availableFractions = availableFractions
        }
}