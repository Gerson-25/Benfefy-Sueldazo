package com.appbenefy.sueldazo.ui.profile.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.profile.model.*
import com.appbenefy.sueldazo.ui.profile.ui.adapters.TransactionsAdapter
import com.appbenefy.sueldazo.ui.profile.viewModel.ProfileViewModel
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_transactions.*
import java.text.DateFormatSymbols
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

class TransactionsActivity : BaseActivity(), BottomNavigationDrawerFragment.BottomSheetListener {

    @Inject
    lateinit var transactionsAdapter: TransactionsAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private var transactionOriginalList: MutableList<SavingDetails> = mutableListOf()
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_transactions)

        profileViewModel = viewModel(viewModelFactory) {
            observe(userTransactions, ::handleTransactions)
            failure(failure, ::handleError)
        }

        filterId.setOnClickListener{
            openFilter()
        }

        swipeRefreshLayout.setDistanceToTriggerSync(300)
        swipeRefreshLayout.setOnRefreshListener { loadTransactions() }

        nestedScroll.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val nestedScrollView = checkNotNull(v) {
                return@setOnScrollChangeListener
            }
            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - nestedScrollView.measuredHeight)) && scrollY > oldScrollY) {
                    //get more items
                    page++
                    showProgress(true)
                    loadTransactions()
                }
            }
        }

        initList()
        loadTransactions()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun stopRefreshing() { swipeRefreshLayout.isRefreshing = false }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.addItemDecoration(
                DividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                )
        )
        listId.adapter = transactionsAdapter
        transactionsAdapter.setActivity(this)
    }

    private fun loadTransactions() {
        val request = TransactionRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = page,
                idUser = Constants.userProfile?.idUser ?: ""
        )
        profileViewModel.getUserTransactions(request)
    }

    private fun handleTransactions(response: BaseResponse<SavingDetailsResponse>?) {
        showProgress(false)
        response?.data?.let {
            if (page > 1) {
                val temp = transactionOriginalList.toList()
                val full = merge(temp, it.detalle)
                transactionOriginalList = full.toMutableList()
                transactionsAdapter.collection = it.detalle
            } else {
                if (it.detalle.isEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
                transactionOriginalList = it.detalle.toMutableList()
                transactionsAdapter.collection = it.detalle
            }
        } ?: run { showEmptyLayout(true) }
    }

    fun openFilter() {
        val intent = Intent(this, TransactionsFilterDialog::class.java)
        intent.putExtra("screen", 1)
        startActivityForResult(intent, 1)
    }

//    fun collapseMonth(month: Int, year: Int) {
//        try {
//            val temp = transactionOriginalList
//            temp.forEachIndexed { index, transactionResponse: SavingDetails ->
//                transactionResponse.takeIf { ot -> getMonth(ot.transactionDate) == month && getYear(ot.transactionDate) == year }?.let {
//                    transactionOriginalList[index] = it.copy(visible = !it.visible)
//                }
//            }
//            transactionsAdapter.collection = getTransactionModel(temp)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun openDetail(id: String?) {
        id?.let {
            callIntent<TransactionsInfoDialog> {
                this.putExtra("id", it)
            }
        }
    }

//    private fun getTransactionModel(it: List<SavingDetailsResponse>): MutableList<TransactionsByDateDataModel> {
//        val dataModel: MutableList<TransactionsByDateDataModel> = mutableListOf()
//        var lastMonthEvaluate = 0
//        for (t in it) {
//            val actualMonth = getMonth(t.transactionDate) + 1
//            if (lastMonthEvaluate != actualMonth) {
//                lastMonthEvaluate = actualMonth
//                val dateDataModel = TransactionsByDateDataModel()
//                dateDataModel.type = 1
//                dateDataModel.month = "${getMonthName(lastMonthEvaluate)} ${getYear(t.transactionDate)}"
//                dateDataModel.numberMonth = actualMonth - 1
//                dateDataModel.year = getYear(t.transactionDate)
//                dataModel.add(dateDataModel)
//                val transactionMonth = it.filter { trs -> getMonth(trs.transactionDate) == getMonth(t.transactionDate) }
//                for (tm in transactionMonth) {
//                    val transactionDataModel = TransactionsByDateDataModel()
//                    transactionDataModel.type = 2
//                    transactionDataModel.transaction = tm
//                    dataModel.add(transactionDataModel)
//                }
//            }
//        }
//        return dataModel
//    }

    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }

    private fun getYear(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    private fun getMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }

    private fun getMonthName(number: Int): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) Month.of(number).getDisplayName(TextStyle.FULL, Locale.US)
        else DateFormatSymbols().months[number].toString()
    }

    companion object {
        private const val EXCHANGE_COUPON = 1
        private const val MILES = 2
        private const val SEALS = 3
        private const val EXCHANGE_MILES = 4
        private const val EXCHANGE_LOYALTY = 5
        private const val LINK_LOYALTY_PLAN = 6
        private const val UNLINK_LOYALTY = 7
        private const val BLOCKING_LOYALTY = 8
        private const val GIFTED_ARTICLES = 9
        private const val MY_GIFT = 10
    }

    override fun onOptionsClick(id: Int) {
//        when(id) {
//            R.id.all_nav -> {
//                if (transactionOriginalList.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(transactionOriginalList)
//            }
//
//            R.id.exchange_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == EXCHANGE_COUPON }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.plusMiles_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == MILES }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.plusLoyalty_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == SEALS }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.miles_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == EXCHANGE_MILES }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.loyaltyCards_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == EXCHANGE_LOYALTY }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.addLoyalty_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == LINK_LOYALTY_PLAN }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.deleteLoyalty_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == UNLINK_LOYALTY }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.blockingLoyalty_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == BLOCKING_LOYALTY }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.giftedArticle_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == GIFTED_ARTICLES }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//
//            R.id.myArticleGift_nav -> {
//                val filter = transactionOriginalList.filter { o -> o.idTransactionType == MY_GIFT }
//                if (filter.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
//                transactionsAdapter.collection = getTransactionModel(filter)
//            }
//        }
    }

}
