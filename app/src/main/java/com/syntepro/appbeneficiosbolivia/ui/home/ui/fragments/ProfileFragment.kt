package com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.menu.EditProfileActivity2
import com.syntepro.appbeneficiosbolivia.ui.menu.ScannerActivity
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.StatisticsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.TransactionsActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment() {

    override fun layoutId(): Int = R.layout.fragment_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showData()

//        editProfile.setOnClickListener {
//            val intent = Intent(requireContext(), EditProfileActivity2::class.java)
//            intent.putExtra("provenance", 1)
//            startActivity(intent)
//        }
//
//        transactions.setOnClickListener {
//            val intent = Intent(requireContext(), TransactionsActivity::class.java)
//            startActivity(intent)
//        }
//
//        qrCode.setOnClickListener { Functions.showUserQR(requireContext()) }
//
//        stats.setOnClickListener {
//            val intent = Intent(requireContext(), StatisticsActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun showData() {
        Constants.userProfile?.let {
            val fullName = "${it.names} ${it.lastNames}"
            name.text = fullName
            emailId.text = "${it.email}"
        }
    }

}