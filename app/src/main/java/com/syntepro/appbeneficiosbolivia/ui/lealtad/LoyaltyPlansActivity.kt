package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.firebase.MisPlanesLealtad
import com.syntepro.appbeneficiosbolivia.entity.firebase.PlanLealtad
import com.syntepro.appbeneficiosbolivia.utils.Constants

class LoyaltyPlansActivity: BaseActivity() {

    private lateinit var mAddPlan: FloatingActionButton
    private lateinit var mPlanList: RecyclerView
    private lateinit var mSealList: RecyclerView
    private lateinit var mParenScroll: NestedScrollView
    private lateinit var mRefresh: SwipeRefreshLayout
    private lateinit var mEmptytId: LinearLayout
    private lateinit var mEmptytIdSeals: LinearLayout
    private var mCommerceId: String? = null
    private var mCommerceImage: String? = null
    private var mRubroName: String? = null
    private var mCommerceName: String? = null
    private var mCommerceBanner: String? = null
    private var adapter: LoyaltyPlanAdapter? = null
    private var adapterSeals: LoyaltyPlanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_plans)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.my_loyalty_plans)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            mCommerceId = extras.getString("commerceId")
            mCommerceImage = extras.getString("commerceImage")
            mRubroName = extras.getString("rubroName" )
            mCommerceName = extras.getString("commerceName")
            mCommerceBanner = extras.getString("commerceBanner")
        }

        // Views
        mAddPlan = findViewById(R.id.newId)
        mPlanList = findViewById(R.id.planListId)
        mSealList = findViewById(R.id.sealListId)
        mParenScroll = findViewById(R.id.parentScrollId)
        mRefresh = findViewById(R.id.swipeRefreshLayout)
        mEmptytId = findViewById(R.id.emptyId1)
        mEmptytIdSeals = findViewById(R.id.emptyId2)

        mPlanList.isNestedScrollingEnabled = false
        mSealList.isNestedScrollingEnabled = false

        // Manager
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mPlanList.layoutManager = linearLayoutManager
        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mSealList.layoutManager = lm

        // Listeners
        mAddPlan.setOnClickListener { addPlan() }
        mRefresh.setOnRefreshListener { mRefresh.isRefreshing = false }
        mParenScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ _, _, scrollY, _, _ -> mRefresh.isEnabled = scrollY <= 0 })

        // Show Data
        getData()
    }

    override fun onDestroy() {
        mPlanList.adapter = null
        mSealList.adapter = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()

        val plans = db.collection(Constants.USERS_COLLECTION).document(Constants.userProfile?.idUserFirebase ?: "").collection(Constants.MY_LOYALTY_PLAN_COLLECTION)
                .whereEqualTo("comercio", mCommerceId!!)
                .whereLessThanOrEqualTo("categoria", PlanLealtad.PLAN_COBRANDING)
                .whereEqualTo("activo", true)
                .orderBy("categoria", Query.Direction.ASCENDING)
                .orderBy("fechaAfiliacion", Query.Direction.DESCENDING)

        val seals = db.collection(Constants.USERS_COLLECTION).document(Constants.userProfile?.idUserFirebase ?: "").collection(Constants.MY_LOYALTY_PLAN_COLLECTION)
                .whereEqualTo("comercio", mCommerceId!!)
                .whereEqualTo("categoria", PlanLealtad.PLAN_SELLOS)
                .whereEqualTo("activo", true)
                .orderBy("fechaAfiliacion", Query.Direction.DESCENDING)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(Constants.LIST_PREFETCH_DISTANCE)
                .setPageSize(Constants.PAGE_SIZE)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT)
                .build()

        val options = FirestorePagingOptions.Builder<MisPlanesLealtad>()
                .setLifecycleOwner(this)
                .setQuery(plans, config, MisPlanesLealtad::class.java)
                .build()

        val optionsSeals = FirestorePagingOptions.Builder<MisPlanesLealtad>()
                .setLifecycleOwner(this)
                .setQuery(seals, config, MisPlanesLealtad::class.java)
                .build()

        adapter = LoyaltyPlanAdapter(options, this, SOURCE_PLANS)
        mPlanList.adapter = adapter

        adapterSeals = LoyaltyPlanAdapter(optionsSeals, this, SOURCE_SEALS)
        mSealList.adapter = adapterSeals
    }

    fun showEmptyLayoutSeals(show: Boolean)  = if(show) mEmptytIdSeals.visibility = View.VISIBLE else mEmptytIdSeals.visibility = View.GONE

    private fun addPlan() {
        val intent = Intent(this, AddLoyaltyPlanDialog::class.java)
        intent.putExtra("commerceId", mCommerceId)
        intent.putExtra("commerceImage", mCommerceImage)
        intent.putExtra("rubroName", mRubroName)
        intent.putExtra("commerceName", mCommerceName)
        intent.putExtra("commerceBanner", mCommerceBanner)
        startActivityForResult(intent, AddLoyaltyPlanDialog.ADD_LOYALTY_PLAN_DIALOG_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AddLoyaltyPlanDialog.ADD_LOYALTY_PLAN_DIALOG_ID && resultCode == Activity.RESULT_OK) {
            adapter?.currentList!!.dataSource.invalidate()
            adapterSeals?.currentList!!.dataSource.invalidate()
        }
    }

    override fun showEmptyLayout(show: Boolean)  = if(show) mEmptytId.visibility = View.VISIBLE else mEmptytId.visibility = View.GONE
    override fun stopRefreshing() { mRefresh.isRefreshing = false}

    companion object {
        const val SOURCE_PLANS = 1
        const val SOURCE_SEALS = 2
    }

}
