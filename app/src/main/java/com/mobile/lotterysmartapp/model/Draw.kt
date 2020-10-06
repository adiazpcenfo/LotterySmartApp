package com.mobile.lotterysmartapp.model

import java.util.*

/**
 * Class that represents Draw collection on the database.
 *
 * @author Franklin Cardenas
 */
class Draw {
    var Id : Long
        get() = this.Id
        set(value) {
            this.Id = value
        }
    var state : String
        get() = this.state
        set(state) {
            this.state = state
        }
    var name : String
        get() = this.name
        set(name) {
            this.name = name
        }
    var drawDate : Date
        get() = this.drawDate
        set(drawDate) {
            this.drawDate = drawDate
        }

    var result1 : Int
        get() = this.result1
        set(result1) {
            this.result1 = result1
        }
    var result2 : Int
        get() = this.result2
        set(result2) {
            this.result2 = result2
        }
    var result3 : Int
        get() = this.result3
        set(result3) {
            this.result3 = result3
        }
}