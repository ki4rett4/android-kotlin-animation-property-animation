/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    //for individual property animations, ObjectAnimator

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    private fun rotater() {
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        // The reason that the animation starts at -360 is that that allows the star to complete a full circle (360 degrees) and end at 0
        animator.duration = 1000
        //rotateButton is disabled as soon as the animation starts, and re-enabled when the animation ends.
        // This way, each animation is completely separate from any other rotation animation, avoiding the jank of restarting in the middle.
        animator.disableViewDuringAnimation(rotateButton)
        animator.start()
    }

    private fun translater() {
        // Repetition is a way of telling animations to do the same task again and again. You can specify how many times to repeat (or just tell it to run infinitely).
        // You can also specify the repetition behavior, either REVERSE (for reversing the direction every time it repeats)
        // or RESTART
        // (for animating from the original start value to the original end value, thus repeating in the same direction every time).
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        animator.disableViewDuringAnimation(translateButton) // same problem but if play button during animation then starts in the middle
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    private fun scaler() {
        //Two properties in parallel
        /**rotating around a single axis
         * (the "z" axis, which runs perpendicular to the screen) and translating along a single axis
         * (the "x" axis, which runs left to right on the screen).
         * **/
        //an object is scaled, it is usually scaled in x and y simultaneously
        //PropertyValuesHolder,
        // which is an object that holds information about both a property and the values that that property should animate between.

        //An ObjectAnimator can hold multiple PropertyValuesHolder objects, which will all animate together
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            star, scaleX, scaleY)
        //resetting object
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(scaleButton)
    }

    private fun fader() {
        // 0 trasparent, 1 opaque
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    private fun colorizer() {
        // change the color of the star field background from black to red (and back).
        //var animator = ObjectAnimator.ofInt(star.parent, "backgroundColor", Color.BLACK, Color.RED).start() //Problematic, flashing
        //Animating between two integer values does not necessarily yield the same result as animating between the colors that those two integers represent.
        val animator = ObjectAnimator.ofArgb(star.parent,
            "backgroundColor", Color.BLACK, Color.RED) //smoothly animates from black to red
        //propertyName system searches for setters and getters with that exact spelling using reflection.
        animator.duration = 500
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }

    private fun shower() {
        //animating multiple properties on multiple objects.
        /**
         * For this effect, a button click will result in the creation of a star with a random size, which will be added to the background container,
         * just out of view of the top of that container.
         * The star will proceed to fall down to the bottom of the screen, accelerating as it goes. As it falls, it will rotate.
         * **/

        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()

        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
        container.addView(newStar)

        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2

        /**
         * You could do a similar thing here,
         * except there will be different types of motion,
         * what is called "interpolation," on these two animations.
         * Specifically, the rotation will use a smooth linear motion
         * (moving at a constant rate over the entire rotation animation),
         * while the falling animation will use an accelerating motion
         * (simulating gravity pulling the star downward at a constantly faster rate).
         * **/

        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y,
            -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION,
            (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()
        //The AccelerateInterpolator "interpolator" that you are setting on the star causes a gentle acceleration motion.
        // For the motion, use a LinearInterpolator, so the rotation will proceed at a constant rate as the star falls

        /**
         * Now it is time to put these two animators together into a single AnimatorSet,
         * which is useful for this slightly more complex animation involving multiple ObjectAnimator
         * **/

        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()
        //add listener for removeView when animation is finishing
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()
    }

}
