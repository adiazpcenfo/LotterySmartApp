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


//class Draw (var Id: String ="", val state: String="", val name: String="", val result1: String="", val result2: String="", val result3: String="",val drawDate: String=""){
class Draw (){

    var id: String=""
        get() = field
        set(value){
            field=value
        }

    var state: String=""
        get() = field
        set(value){
            field=value
        }

    var name: String=""
        get() = field
        set(value){
            field=value
        }

    var result1: String=""
        get() = field
        set(value){
            field=value
        }
    var result2: String=""
        get() = field
        set(value){
            field=value
        }
    var result3: String=""
        get() = field
        set(value){
            field=value
        }
    var drawDate: String=""
        get() = field
        set(value){
            field=value
        }


}