package com.syntepro.sueldazo.ui.home.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.base.BaseFragment
import com.syntepro.sueldazo.ui.login.ConditionsActivity
import com.syntepro.sueldazo.ui.menu.EditProfileActivity2
import com.syntepro.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment() {

    override fun layoutId(): Int = R.layout.fragment_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showData()

        editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity2::class.java)
            intent.putExtra("provenance", 1)
            startActivity(intent)
        }

        terms.setOnClickListener {
            val intent = Intent(requireContext(), ConditionsActivity::class.java)
            startActivity(intent)
        }
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
            val fullName = "${it.names ?: ""} ${it.lastNames ?: ""}"
            name.text = fullName
            emailId.text = "${it.email ?: ""}"
        }
    }

}