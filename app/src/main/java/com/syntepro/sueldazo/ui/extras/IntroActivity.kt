package com.syntepro.sueldazo.ui.extras

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.entity.app.ScreenItem
import com.syntepro.sueldazo.ui.extras.adapter.IntroViewPagerAdapter
import com.syntepro.sueldazo.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroActivity : AppCompatActivity() {

    private val btnAnim: Animation by lazy { AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation) }
    private var position = 0

    private var isLoyalty = false
    private var idPlanType = 0
    private var idPlan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val extras = intent.extras
        if (extras != null) {
            isLoyalty = extras.getBoolean("loyaltyPush")
            idPlanType = extras.getInt("planType", 0)
            idPlan = extras.getString("idPlan", "")
        }

        // When this Activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            val mainActivity = Intent(applicationContext, HomeActivity::class.java)
            if (idPlanType != 0 && idPlan.isNotEmpty()) {
                mainActivity.putExtra("loyaltyPush", isLoyalty)
                mainActivity.putExtra("planType", idPlanType)
                mainActivity.putExtra("idPlan", idPlan)
            }
            startActivity(mainActivity)
            finish()
        }
        setContentView(R.layout.activity_intro)

        // Fill List Screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(ScreenItem(R.drawable.tutorial_01))
        mList.add(ScreenItem(R.drawable.tutorial_02))
        mList.add(ScreenItem(R.drawable.tutorial_03))
        mList.add(ScreenItem(R.drawable.tutorial_04))
        mList.add(ScreenItem(R.drawable.tutorial_05))

        // Setup Viewpager
        val introViewPagerAdapter = IntroViewPagerAdapter(mList)
        screen_viewpager.adapter = introViewPagerAdapter

        // Setup Tab Layout with Viewpager
        tab_indicator.setupWithViewPager(screen_viewpager)

        // Next Button Click Listener
        btn_next.setOnClickListener {
            position = screen_viewpager.currentItem
            if (position < mList.size) {
                position++
                screen_viewpager.currentItem = position
            }
            if (position == mList.size - 1) { //When we reach to the last screen
                // TODO : show the GETSTARTED Button and hide the indicator and the next button
                loadLastScreen()
            }
        }

        // Tab Layout add Change Listener
        tab_indicator.addOnTabSelectedListener(object : BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {  }
            override fun onTabReselected(tab: TabLayout.Tab?) {  }
        })

        // Get Started Button Click Listener
        btn_get_started.setOnClickListener {
            // Open Main Activity
            val mainActivity = Intent(applicationContext, HomeActivity::class.java)
            startActivity(mainActivity)
            // Also we need to save a boolean value to storage so next time when the user run the app
            // We could know that he is already checked the intro screen activity
            // I'm going to use shared preferences to that process
            savePrefsData()
            finish()
        }

        // Skip Button Click Listener
        tv_skip.setOnClickListener { screen_viewpager.currentItem = mList.size }
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    // Show the GET STARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        btn_next.visibility = View.INVISIBLE
        btn_get_started.visibility = View.VISIBLE
        tv_skip.visibility = View.INVISIBLE
        tab_indicator.visibility = View.INVISIBLE
        // TODO : ADD an animation the getStarted button
        // Setup animation
        btn_get_started.animation = btnAnim
    }
}