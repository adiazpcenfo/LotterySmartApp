package com.mobile.lotterysmartapp.model

import android.view.ViewParent
import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDate
import java.util.*

/**
 * Class that represents Draw collection on the database.
 *
 * @author Franklin Cardenas
 */

class Draw() {
    var id: String = ""
    var state: String = ""
    var name: String = ""
    var result1: String = ""
    var result2: String = ""
    var result3: String = ""
    var drawDate: String = ""
}