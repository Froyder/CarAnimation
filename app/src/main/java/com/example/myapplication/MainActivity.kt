package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.math.cos
import kotlin.math.sin


class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var carIcon: ImageView
    private lateinit var refillButton: Button
    private lateinit var infoText: TextView
    private var toRightAnimation = false
    private var fuel = 5

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.transitions_container)
        carIcon = findViewById(R.id.car)
        refillButton = findViewById(R.id.refill_button)
        infoText = findViewById(R.id.info_field)

        carIcon.setOnClickListener { moveCarIconRandomly() }
        refillButton.setOnClickListener { refillTank() }
    }

    private fun refillTank() {
        fuel = 5
        refillButton.visibility = View.GONE
        infoText.text = this.getString(R.string.fuel_info, fuel.toString())
    }

    private fun moveCarIconRandomly() {
        if (fuel > 0) {
            fuel -= 1
            job?.cancel()
            job = coroutineScope.launch {
                calculateAndStartRandomRide()
                delay(500)
                calculateAndStartRandomRide()
                delay(500)
                calculateAndStartRandomRide()
                delay(500)
                moveCarIconToCorner()
            }
            infoText.append(this.getString(R.string.fuel_info, fuel.toString()))
        } else {
            infoText.append("\n ${this.getString(R.string.out_of_gas)}")
            refillButton.visibility = View.VISIBLE
        }
    }

    private fun calculateAndStartRandomRide() {
            val distance = 500
            val duration = 500

            val direction = Math.random() * 2 * Math.PI

            val translationX = (cos(direction) * distance).toInt()
            val translationY = (sin(direction) * distance).toInt()

            carIcon.animate().translationX(translationX.toFloat())
                .translationY(translationY.toFloat())
                .setDuration(duration.toLong()).start()
    }

    private fun moveCarIconToCorner() {
        val parentCenterX: Float = container.x + container.width / 2
        val parentCenterY: Float = container.y + container.height / 2
        toRightAnimation = !toRightAnimation
        if (toRightAnimation){
            carIcon.animate()
                .x(container.x)
                .y(container.y)
                .setDuration(1000)
                .withEndAction {
                    carIcon.x = container.x
                    carIcon.y = container.y
                }
        } else carIcon.animate()
            .translationX(parentCenterX - carIcon.width /2)
            .translationY(parentCenterY - carIcon.height /2)
            .setDuration(1000)
            .start()
    }

    override fun onDestroy() {
        job?.cancel()
        coroutineScope.cancel()
        super.onDestroy()
    }
}