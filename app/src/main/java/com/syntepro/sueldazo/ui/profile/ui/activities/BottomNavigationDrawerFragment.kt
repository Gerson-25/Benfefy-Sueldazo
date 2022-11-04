package com.syntepro.sueldazo.ui.profile.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.syntepro.sueldazo.R

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    private var mBottomSheetListener: BottomSheetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_nav_layout, container, false)

        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId) {
                R.id.all_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.all_nav)
                    dismiss()
                }

                R.id.exchange_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.exchange_nav)
                    dismiss()
                }

                R.id.plusMiles_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.plusMiles_nav)
                    dismiss()
                }

                R.id.plusLoyalty_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.plusLoyalty_nav)
                    dismiss()
                }

                R.id.miles_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.miles_nav)
                    dismiss()
                }

                R.id.loyaltyCards_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.loyaltyCards_nav)
                    dismiss()
                }

                R.id.addLoyalty_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.addLoyalty_nav)
                    dismiss()
                }

                R.id.deleteLoyalty_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.deleteLoyalty_nav)
                    dismiss()
                }

                R.id.blockingLoyalty_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.blockingLoyalty_nav)
                    dismiss()
                }
                R.id.giftedArticle_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.giftedArticle_nav)
                    dismiss()
                }
                R.id.myArticleGift_nav -> {
                    mBottomSheetListener!!.onOptionsClick(R.id.myArticleGift_nav)
                    dismiss()
                }
            }
            true
        }

        return view
    }

    interface BottomSheetListener { fun onOptionsClick(id: Int) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mBottomSheetListener = context as BottomSheetListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString())
        }
    }

}