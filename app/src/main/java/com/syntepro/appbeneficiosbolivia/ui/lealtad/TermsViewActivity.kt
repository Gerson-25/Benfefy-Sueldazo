package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.PlanLealtad

class TermsViewActivity : AppCompatActivity() {

    private lateinit var mTerms: TextView
    private var mCommerceId: String? = null
    private var mPlanId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_view)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            mCommerceId = extras.getString("commerceId")
            mPlanId = extras.getString("planId")
            val planName = extras.getString("planName")
            supportActionBar!!.title = planName
        }

        // Views
        mTerms = findViewById(R.id.termId)

        val accept = findViewById<Button>(R.id.next_id)
        accept.setOnClickListener { finish() }

        // Show Data
        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    private fun getData() {
        FirebaseFirestore.getInstance().collection(Constants.TRADE_COLLECTION).document(mCommerceId!!)
                .collection(Constants.LOYALTY_PLAN_COLLECTION).document(mPlanId!!)
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        val pln = it.toObject(PlanLealtad::class.java)!!
                        mTerms.text = pln.terminos
                    }
                }.addOnFailureListener { Functions.showError(this, it.message) }
    }

}