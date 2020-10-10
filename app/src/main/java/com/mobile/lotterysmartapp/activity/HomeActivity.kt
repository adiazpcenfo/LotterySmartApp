package com.mobile.lotterysmartapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.lotterysmartapp.R
import com.mobile.lotterysmartapp.model.Constants

/**
 * Class in charge of Home Activity.
 *
 * @author Franklin Cardenas
 */
class HomeActivity : AppCompatActivity() {

    /**
     * On Create method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))


        //Get user and provider from login or register
        val bundle = intent.extras
        val email = bundle?.getString(Constants.EMAIL)
        val provider = bundle?.getString(Constants.PROVIDER)

        //Setup process for Home Activity
        setup()

    }

    /**
     * Setup method for Home Activity.
     *
     * @author Franklin Cardenas
     */
    private fun setup() {
        this.title = "Inicio"
    }
}