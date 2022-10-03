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

        scanId.setOnClickListener {
            val integrator = IntentIntegrator(requireActivity())
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scanner")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }

        notificationsId.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity2::class.java)
            intent.putExtra("provenance", 1)
            startActivity(intent)
        }

        transactions.setOnClickListener {
            val intent = Intent(requireContext(), TransactionsActivity::class.java)
            startActivity(intent)
        }

        qrCode.setOnClickListener { Functions.showUserQR(requireContext()) }

        stats.setOnClickListener {
            val intent = Intent(requireContext(), StatisticsActivity::class.java)
            startActivity(intent)
        }

        // Show Data
        Functions.readUserInfo(userImageId, welcomeId, total_notificationsId)
        showData()
    }

    private fun showData() {
        Constants.userProfile?.let {
            Functions.showImage(it.photoUrl, circleImageView)
            val fullName = "${it.names} ${it.lastNames}"
            nameId.text = fullName
            emailId.text = "${it.email}"
        }
    }

}