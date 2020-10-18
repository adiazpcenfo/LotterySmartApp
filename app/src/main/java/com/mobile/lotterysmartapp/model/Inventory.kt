package com.mobile.lotterysmartapp.model

/**
 * Class that represents Inventory collection on the database.
 *
 * @author Franklin Cardenas
 */
class Inventory {

    constructor(
        Id: Long,
        drawId: String,
        userEmail: String,
        state: String,
        number: Int,
        series: Int,
        fractions: Int,
        availableFractions: Int
    ) {
        this.Id = Id
        this.drawId = drawId
        this.userEmail = userEmail
        this.state = state
        this.number = number
        this.series = series
        this.fractions = fractions
        this.availableFractions = availableFractions
    }

    constructor()

    var Id: Long = 0L
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var drawId: String = ""
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var userEmail: String = ""
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var state: String = ""
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var number: Int = 0
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var series: Int = 0
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var fractions: Int = 0
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var availableFractions: Int = 0
        get() = field        // getter
        set(value) {         // setter
            field = value
        }
}