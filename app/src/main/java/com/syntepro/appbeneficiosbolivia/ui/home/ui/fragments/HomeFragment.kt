package com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.base.viewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.category.CategoryActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FavoriteData
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CategoryAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.GiftCardsAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.model.CategoryRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.ParameterResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.StatesResponse
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.ArticleAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.FeaturedGiftCardAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationCountResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCardRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.readUserInfo
import kotlinx.android.synthetic.main.coupon_empty_layout.*
import kotlinx.android.synthetic.main.discount_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    @Inject
    lateinit var featuredGiftCardAdapter: FeaturedGiftCardAdapter

    @Inject
    lateinit var giftCardsAdapter: GiftCardsAdapter

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var selectedShopItem: Int = 1
    private var articleSize: Int = 0
    private var giftCardSize: Int = 0

    override fun layoutId() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            observe(categories, ::handleCategories)
            observe(giftCards, ::handleGiftCards)
            observe(featuredGiftCards, ::handleFeaturedGiftCards)
            observe(items, ::handleArticles)
            observe(parameters, ::parametersResponse)
            observe(counter, ::handleCounter)
            observe(states, ::handleStates)
            failure(failure, ::handleFailure)
        }

        // Adapters
        categoryAdapter = CategoryAdapter(1) { item -> openCategory(item) }
        articleAdapter.parentFragment(this)
        featuredGiftCardAdapter.parentFragment(this)
        giftCardsAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readUserInfo(userImageId, welcomeId, total_notificationsId)
        initList()
        configureRecyclerview(1)
        getCategories(selectedShopItem)

        if (!Functions.isDarkTheme(requireActivity()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            shopType.setCardBackgroundColor(requireContext().getColor(R.color.gray_card_benefit))

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

        moreCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra("homeProvenance", true)
            intent.putExtra("selectionType", selectedShopItem)
            startActivity(intent)
        }

        showArticles.setOnClickListener {
            articleSelectedId.setImageDrawable(activity?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_article_selected) })
            giftCardSelectedId.setImageDrawable(activity?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_gift_card_deselected) })
            shopTextId.text = requireContext().getString(R.string.articles_store)
            listTextId.text = requireContext().getString(R.string.article_label)
            featuredGiftCardsView.visibility = View.GONE
            selectedShopItem = 1
            configureRecyclerview(1)
            getCategories(1)
            if (articleSize > 10) showMoreListId.visibility = View.VISIBLE else showMoreListId.visibility = View.GONE
            if (articleAdapter.collection.isEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
        }

        showGiftCards.setOnClickListener {
            articleSelectedId.setImageDrawable(activity?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_article_deselected) })
            giftCardSelectedId.setImageDrawable(activity?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_gift_card_selected) })
            shopTextId.text = requireContext().getString(R.string.gift_card_store)
            listTextId.text = requireContext().getString(R.string.gift_card_label)
            featuredGiftCardsView.visibility = View.VISIBLE
            selectedShopItem = 2
            configureRecyclerview(2)
            getCategories(2)
            if (giftCardSize > 10) showMoreListId.visibility = View.VISIBLE else showMoreListId.visibility = View.GONE
            if (giftCardsAdapter.collection.isEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
        }

        moreShopItems.setOnClickListener {
            val intent = Intent(requireContext(), ShopActivity::class.java)
            intent.putExtra("type", selectedShopItem)
            startActivity(intent)
        }

        showMoreList.setOnClickListener {
            val intent = Intent(requireContext(), ShopActivity::class.java)
            intent.putExtra("type", selectedShopItem)
            startActivity(intent)
        }
    }

    /**
     * App Functions
     */

    private fun initList() {
        // Category Recycler View
        categoriesListId.setHasFixedSize(true)
        categoriesListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriesListId.itemAnimator = DefaultItemAnimator()
        categoriesListId.adapter = categoryAdapter

        // Featured GiftCards Recycler View
        featuredGiftCardListId.setHasFixedSize(true)
        featuredGiftCardListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        featuredGiftCardListId.itemAnimator = DefaultItemAnimator()
        featuredGiftCardListId.adapter = featuredGiftCardAdapter
    }

    private fun configureRecyclerview(type: Int) {
        shopItemsListId.removeAllViewsInLayout()
        shopItemsListId.adapter = null
        if (type == 1) {
            shopItemsListId.setHasFixedSize(true)
            shopItemsListId.layoutManager = GridLayoutManager(requireContext(), 2)
            shopItemsListId.itemAnimator = DefaultItemAnimator()
            shopItemsListId.isLayoutFrozen = true
            shopItemsListId.adapter = articleAdapter
        } else {
            shopItemsListId.setHasFixedSize(true)
            shopItemsListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            shopItemsListId.itemAnimator = DefaultItemAnimator()
            shopItemsListId.isLayoutFrozen = true
            shopItemsListId.adapter = giftCardsAdapter
        }
    }

    private fun getCategories(type: Int) {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = type
        )
        homeViewModel.loadCategories(request)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleCategories(categories: BaseResponse<List<Category>>?) {
        categories?.data?.let {
            val all = with(Category()) {
                idCategory = null
                name = "Todas"
                description = "Todas las categorias."
                urlImage = "https://firebasestorage.googleapis.com/v0/b/beneficios-1b534.appspot.com/o/Extras%2Ffondo_tarjeta_lealtad.png?alt=media&token=fd4848ca-5939-4a3c-9bdc-d193cd593d56"
                this
            }
            val categoriesList = it.toMutableList()
            categoriesList.add(0, all)
            categoryAdapter.setListData(categoriesList)
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun handleGiftCards(response: BaseResponse<List<GiftCard>>?) {
        if (articleSize == 0 && response?.data!!.isNullOrEmpty() && HomeActivity.make_sort) showGiftCards.callOnClick() else if (articleSize > 0 && HomeActivity.make_sort) showArticles.callOnClick()
        giftCardsAdapter.collection = response?.data.orEmpty()
        featuredGiftCardAdapter.collection = response?.data.orEmpty()
        if (!response?.data.isNullOrEmpty()) emptyId.visibility = View.GONE else emptyId.visibility = View.VISIBLE
        if (articleSize > 0 && selectedShopItem == 1) emptyId.visibility = View.GONE
    }

    private fun handleFeaturedGiftCards(response: BaseResponse<List<GiftCard>>?) {
        giftCardSize = response?.data?.size ?: 0
        val homeGiftCardList = response?.data?.take(10)
        featuredGiftCardAdapter.collection = homeGiftCardList.orEmpty()
        if (!response?.data.isNullOrEmpty()) {
            if (response?.data?.size ?: 0 > 10) showMoreListId.visibility = View.VISIBLE else showMoreListId.visibility = View.GONE
        } else showMoreListId.visibility = View.GONE
    }

    /**
     * Gerson Aquino 28JUN2021
     *
     * getGiftCards() will be called after we receive the articles list
     *
     */

    private fun handleArticles(response: BaseResponse<List<ArticleResponse>>?) {
        articleSize = response?.data?.size ?: 0
        val homeItemList = response?.data?.take(10)
        articleAdapter.collection = homeItemList.orEmpty()
        if (!response?.data.isNullOrEmpty()) {
            emptyId.visibility = View.VISIBLE
            showMoreListId.visibility = View.GONE
        } else {
            emptyId.visibility = View.GONE
            if (response?.data?.size ?: 0 > 10) showMoreListId.visibility = View.VISIBLE else showMoreListId.visibility = View.GONE
        }
//        response?.data?.let {
//            Log.e("Articles Size", "Size: ${it.size}")
//            emptyId.visibility = View.GONE
//            val homeItemList = it.take(9)
//            val dataModel: MutableList<ArticleResponseDataModel> = mutableListOf()
//            homeItemList.forEachIndexed { i, a ->
//                if (i != 8) {
//                    val articleDataModel = ArticleResponseDataModel()
//                    articleDataModel.type = ArticleResponseDataModel.DATA
//                    articleDataModel.id = a.articleId ?: ""
//                    articleDataModel.article = a
//                    dataModel.add(articleDataModel)
//                } else {
//                    val articleDataModel = ArticleResponseDataModel()
//                    articleDataModel.type = ArticleResponseDataModel.FOOTER
//                    articleDataModel.id = "FooterId${i}"
//                    articleDataModel.article = ArticleResponse()
//                    dataModel.add(articleDataModel)
//                }
//            }
//            articleAdapter.collection = dataModel
//        } ?: run {
//            articleAdapter.collection = emptyList()
//            emptyId.visibility = View.VISIBLE
//        }
    }

    private fun parametersResponse(response: BaseResponse<ParameterResponse>?) {
        response?.data?.let {
            Constants.sudamericanaParameters = it.SUDAMERICANA_PARAMETROS
            Constants.transactionParameters = it.transactionsType
        }
    }

    private fun handleCounter(response: BaseResponse<NotificationCountResponse>?) {
        Constants.NOTIFICATION_COUNTER = response?.data?.count ?: 0
    }

    private fun handleStates(response: BaseResponse<List<StatesResponse>>?) {
        Constants.countryStates = response?.data?.toMutableList()
    }

    private fun openCategory(item: Any) {
        HomeActivity.make_sort = false
        val model = item as Category
        Constants.categoryFiltered = model.idCategory
        // getGiftCards()
    }

    fun openArticleDetail(articleId: String) {
        val intent = Intent(requireContext(), ShopDetailActivity::class.java)
        intent.putExtra("couponId", articleId)
        intent.putExtra("type", 1)
        startActivity(intent)
    }

    fun openGiftCardDetail(giftCardId: String) {
        val intent = Intent(requireContext(), ShopDetailActivity::class.java)
        intent.putExtra("couponId", giftCardId)
        intent.putExtra("type", 2)
        startActivity(intent)
    }

    fun favorite(item: Any, position: Int) {
        val model = item as ArticleResponse
        if (model.favorite)
            FavoriteData.removeFavorite(model.articleId) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = shopItemsListId.findViewHolderForAdapterPosition(position) as ArticleAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                } else Log.e("Favorite", message)
            }
        else
            FavoriteData.addFavorite(model.articleId) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = shopItemsListId.findViewHolderForAdapterPosition(position) as ArticleAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                } else Log.e("Favorite", message)
            }
    }
}
