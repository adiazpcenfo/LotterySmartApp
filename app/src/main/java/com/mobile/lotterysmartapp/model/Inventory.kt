package com.mobile.lotterysmartapp.model

/**
 * Class that represents Inventory collection on the database.
 *
 * @author Franklin Cardenas
 */
class Inventory {

    constructor(
        Id: String,
        drawName: String,
        userEmail: String,
        state: String,
        number: Int,
        series: String,
        fractions: Int,
        availableFractions: Int,
        searches: Int
    ) {
        this.Id = Id
        this.drawName = drawName
        this.userEmail = userEmail
        this.state = state
        this.number = number
        this.series = series
        this.fractions = fractions
        this.availableFractions = availableFractions
        this.searches = searches
    }

    constructor()

    var Id: String = ""
        get() = field        // getter
        set(value) {         // setter
            field = value
        }

    var drawName: String = ""
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

    var series: String = ""
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

    var searches: Int = 0
        get() = field        // getter
        set(value) {         // setter
            field = value
        }
}