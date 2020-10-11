package com.mobile.lotterysmartapp.model

import android.view.ViewParent
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

/**
 * Class that represents Draw collection on the database.
 *
 * @author Franklin Cardenas
 */


data class Draw (var Id: String ="", val state: String="", val name: String="", val result1: String="", val result2: String="", val result3: String=""){

    /*var Id : String
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

    var result1 : String
        get() = this.result1
        set(result1) {
            this.result1 = result1
        }
    var result2 : String
        get() = this.result2
        set(result2) {
            this.result2 = result2
        }
    var result3 : String
        get() = this.result3
        set(result3) {
            this.result3 = result3
        }*/
    //( var getId:String, var getName:String, var getResult1: String,var getResult2: String,var getResult3: String, var getState: String)
   // constructor( id:String, name:String,result1: String,result2: String,result3: String,state: String)
}