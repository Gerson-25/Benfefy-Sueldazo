package com.syntepro.appbeneficiosbolivia.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.Comercio
import com.syntepro.appbeneficiosbolivia.entity.firebase.ComercioPickList
import com.syntepro.appbeneficiosbolivia.entity.firebase.SeguirComercio
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.TransactionsActivity
import com.syntepro.appbeneficiosbolivia.ui.wallet.WalletAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import syntepro.util.carousel.CarouselLayoutManager
import syntepro.util.carousel.CarouselZoomPostLayoutListener
import syntepro.util.carousel.CenterScrollListener
import syntepro.util.carousel.DefaultChildSelectionListener
import syntepro.util.picker.PickerDialog
import syntepro.util.picker.PickerEditText

class WalletFragment : Fragment() {

    private lateinit var mList: RecyclerView
    private lateinit var mStamps: RecyclerView
    private lateinit var mEmptytId: LinearLayout
    private lateinit var mCoupons: LinearLayout
    private lateinit var mInfo: LinearLayout
    private lateinit var mTransactions: LinearLayout
    private lateinit var mLoyalty: LinearLayout
    private lateinit var mFilter: ImageView
    private lateinit var mPicklist: PickerEditText<ComercioPickList>
    private lateinit var emptyCommerceLayout:LinearLayout
    private lateinit var walletEmptytId:LinearLayout
    private lateinit var textStamp:TextView
    private var mCurrentCommerce: SeguirComercio? = null
    private var adapter: WalletAdapter? = null

    // Inflate the layout for this fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_wallet, container, false)

        // Views
        mList = view.findViewById(R.id.listId)
        mStamps = view.findViewById(R.id.stampsId)
        mCoupons = view.findViewById(R.id.couponsId)
        mInfo = view.findViewById(R.id.infoId)
        mTransactions = view.findViewById(R.id.agencyId)
        mLoyalty = view.findViewById(R.id.loyaltyId)
        mPicklist = view.findViewById(R.id.picklistId)
        mFilter = view.findViewById(R.id.filterId)
        textStamp = view.findViewById(R.id.textStamp)

        // Empty layout
        mEmptytId = view.findViewById(R.id.emptyId)
        emptyCommerceLayout = view.findViewById(R.id.linearLayout)
        walletEmptytId = view.findViewById(R.id.walletEmptytId)

        // Manager
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mStamps.layoutManager = linearLayoutManager
        mStamps.itemAnimator = DefaultItemAnimator()

        mCoupons.setOnClickListener{ openCoupons() }
        mInfo.setOnClickListener { showInfo() }
        mLoyalty.setOnClickListener { showLoyaltyPlans() }
        mTransactions.setOnClickListener{ showTransactionsCommerce() }
        mFilter.setOnClickListener { mPicklist.callOnClick() }

        // Show Data
        getTrends()

        return view
    }

    override fun onDestroyView() {
        mList.adapter = null
        mStamps.adapter = null
        mCurrentCommerce = null
        super.onDestroyView()
    }

    private fun getTrends() {
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        layoutManager.setMaxVisibleItems(2)
        layoutManager.scrollToPosition(0)
        mList.layoutManager = layoutManager
        mList.setHasFixedSize(true)

        val userId = Constants.userProfile?.idUserFirebase

        val query = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(userId ?: "")
                .collection(Constants.MY_TRADES_COLLECTION)
                .whereEqualTo("pais", Functions.userCountry)
                .whereEqualTo("estatus", SeguirComercio.ESTATUS_ACTIVO)
                .whereEqualTo("permitePlanes", true)

        val queryValidation = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(userId ?: "")
                .collection(Constants.MY_TRADES_COLLECTION)
                .whereEqualTo("pais", Functions.userCountry)
                .whereEqualTo("estatus", SeguirComercio.ESTATUS_ACTIVO)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(Constants.LIST_PREFETCH_DISTANCE)
                .setPageSize(Constants.PAGE_SIZE)
                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT)
                .build()

        val options = FirestorePagingOptions.Builder<SeguirComercio>()
                .setLifecycleOwner(this)
                .setQuery(query, config, SeguirComercio::class.java)
                .build()

        // Validate Data
        queryValidation.get().addOnSuccessListener {
            val commerce = it.toObjects(SeguirComercio::class.java)
            if (commerce.isNotEmpty()) {
                commerce.forEach { commerceValidation ->
                    verifyCommerceData(commerceValidation)
                }
            }
        }

        // Trends List for picker list
        query.get().addOnSuccessListener {
            val commerce = it.toObjects(SeguirComercio::class.java)
            if (commerce.isNullOrEmpty()) {
                walletEmptytId.visibility = View.VISIBLE
                mCurrentCommerce = null
                textStamp.visibility = View.GONE
            } else {
                textStamp.visibility = View.VISIBLE
                walletEmptytId.visibility = View.GONE
                val list: MutableList<ComercioPickList> = mutableListOf()
                var i = 0
                commerce.forEach { pickerListCommerce ->
                    val lst = ComercioPickList()
                    lst.position = i++
                    lst.comercio = pickerListCommerce
                    list.add(lst)
                }
                try {
                    startPicklist(list)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }.addOnFailureListener { Functions.showError(context, it.message) }

        adapter = WalletAdapter(options, requireContext())
        mList.adapter = adapter
        mList.addOnScrollListener(CenterScrollListener())
        DefaultChildSelectionListener.initCenterItemListener(object : DefaultChildSelectionListener.OnCenterItemClickListener {
            override fun onCenterItemClicked(recyclerView: RecyclerView, carouselLayoutManager: CarouselLayoutManager, v: View) {
                //val position = recyclerView.getChildLayoutPosition(v)
                //val msg: String = java.lang.String.format(Locale.US, "Item %1\$d was clicked", position)
                //Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
        }, mList, layoutManager)

        layoutManager.addOnItemSelectionListener(object : CarouselLayoutManager.OnCenterItemSelectionListener {
            override fun onCenterItemChanged(adapterPosition: Int) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    val value = adapter!!.currentList!![adapterPosition]!!.toObject(SeguirComercio::class.java)
                }
            }

            override fun onCenterItemChangedStopped(adapterPosition: Int) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    val value = adapter!!.currentList!![adapterPosition]!!.toObject(SeguirComercio::class.java)
                    getStamps(value?.id!!)
                    mCurrentCommerce = value
                }
            }
        })
    }

    private fun verifyCommerceData(commerceUser: SeguirComercio) {
        FirebaseFirestore.getInstance().collection(Constants.TRADE_COLLECTION).document(commerceUser.id)
                .get().addOnSuccessListener {
                    val mCommerce = it.toObject(Comercio::class.java)
                    if (mCommerce != null) {
                        if (mCommerce.permitePlanes != commerceUser.permitePlanes || mCommerce.permiteMillas != commerceUser.permiteMillas || mCommerce.colorTarjeta != commerceUser.colorTarjeta) {
                            commerceUser.permitePlanes = mCommerce.permitePlanes
                            commerceUser.permiteMillas = mCommerce.permiteMillas
                            commerceUser.colorTarjeta = mCommerce.colorTarjeta
                            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(Functions.userUID)
                                    .collection(Constants.MY_TRADES_COLLECTION).document(commerceUser.id).set(commerceUser)
                                    .addOnFailureListener { Functions.showError(activity, it.message!!) }
                            adapter!!.currentList!!.dataSource.invalidate()
                            walletEmptytId.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { it.stackTrace }
    }

    private fun getStamps(comercio: String) {
        showEmptyLayout(false)
        val userId = Constants.userProfile?.idUserFirebase
        val db = FirebaseFirestore.getInstance()
        /*db.collection(Constants.USERS_COLLECTION).document(userId ?: "")
                .collection(Constants.MY_LOYALTY_PLAN_COLLECTION)
                .whereEqualTo("comercio", comercio)
                .whereEqualTo("categoria", PlanLealtad.PLAN_SELLOS)
                .whereEqualTo("activo", true).get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        val lyp = it.documents[0]
                        db.collection(Constants.TRADE_COLLECTION).document(comercio).collection(Constants.LOYALTY_PLAN_COLLECTION)
                                .document(lyp.get("plan").toString()).get().addOnSuccessListener { pln ->
                                    if (pln.exists()) {
                                        mStamps.visibility = View.VISIBLE
                                        textStamp.visibility = View.VISIBLE
                                        val id = lyp.id
                                        val query = db.collection(Constants.USERS_COLLECTION).document(userId ?: "")
                                                .collection(Constants.MY_LOYALTY_PLAN_COLLECTION).document(id)
                                                .collection(Constants.STAMP_CARDS_COLLECTION)
                                                .orderBy("inicio", Query.Direction.DESCENDING)

                                        val config = PagedList.Config.Builder()
                                                .setEnablePlaceholders(false)
                                                .setPrefetchDistance(Constants.LIST_PREFETCH_DISTANCE)
                                                .setPageSize(Constants.PAGE_SIZE)
                                                .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT)
                                                .build()

                                        val options = FirestorePagingOptions.Builder<TarjetaSellos>()
                                                .setLifecycleOwner(this)
                                                .setQuery(query, config, TarjetaSellos::class.java)
                                                .build()

                                        try {
                                            val plan = pln.toObject(PlanLealtad::class.java)
                                            val adapter = StampsAdapter(options, context!!, plan!!, id)
                                            mStamps.adapter = adapter
                                            adapter.startListening()
                                        } catch (e: Exception) { e.printStackTrace() }
                                    } else {
                                        mStamps.visibility = View.GONE
                                        textStamp.visibility = View.GONE
                                    }
                                }.addOnFailureListener { Functions.showError(context!!, it.message) }
                    } else {
                        mStamps.visibility = View.GONE
                        textStamp.visibility = View.GONE
                    }

                }.addOnFailureListener { Functions.showError(context!!, it.message) }
        */
    }

    private fun openCoupons() {
        if (mCurrentCommerce != null) {
            Constants.provenance = "6"
            Constants.map["commerceId"] = mCurrentCommerce?.id!!
            Constants.map["commerceName"] = mCurrentCommerce!!.nombre
            val intent = Intent(activity, CouponListActivity::class.java)
            intent.putExtra("commerceId", mCurrentCommerce?.id)
            intent.putExtra("commerceName", mCurrentCommerce!!.nombre)
            startActivity(intent)
        } else
            Functions.showWarning(requireActivity(), "No hay comercio seleccionado.")
    }

    private fun showInfo() {
        if (mCurrentCommerce != null){
            FirebaseFirestore.getInstance().collection(Constants.TRADE_COLLECTION).document(mCurrentCommerce?.id!!)
                    .get().addOnSuccessListener {
                        if(it.exists()) {
                            val mCommerce = it.toObject(Comercio::class.java)!!
                            getCategoryName(mCommerce.idRubro!!).addOnSuccessListener {
//                                val intent = Intent(requireContext(), CommerceInfoDialog::class.java)
//                                intent.putExtra("nombre", mCommerce.nombre)
//                                intent.putExtra("telefono", mCommerce.telefono)
//                                intent.putExtra("email", mCommerce.email)
//                                intent.putExtra("direccion", mCommerce.direccion)
//                                intent.putExtra("website", mCommerce.website)
//                                intent.putExtra("horarioServicioInicio", mCommerce.horarioServicioInicio)
//                                intent.putExtra("horarioServicioFin", mCommerce.horarioServicioFin)
//                                intent.putExtra("latitud", mCommerce.latitud)
//                                intent.putExtra("longitud", mCommerce.longitud)
//                                intent.putExtra("imagen", mCommerce.imagen)
//                                intent.putExtra("descripcion", mCommerce.descripcion)
//                                intent.putExtra("facebook", mCommerce.facebook)
//                                intent.putExtra("instagram", mCommerce.instagram)
//                                intent.putExtra("whatsapp", mCommerce.whatsapp)
//                                intent.putExtra("rubro", it)
//                                startActivity(intent)
                            }
                        }

                    }.addOnFailureListener { it.printStackTrace() }
        } else
            Functions.showWarning(requireActivity(), "No hay comercio seleccionado.")
    }

    private fun startPicklist(list: MutableList<ComercioPickList>) {
        mPicklist.with(list, resources.getString(R.string.commerce_list_title), searchBy = PickerDialog.SEARCH_BY_NAME, layoutType = PickerDialog.ONE_ROW_LAYOUT)

        mPicklist.setOnItemSelectedListener(object : PickerEditText.OnItemSelectedListener<ComercioPickList> {
            override fun onItemSelectedListener(item: ComercioPickList, position: Int) {
                mList.scrollToPosition(item.position)
                getStamps(item.comercio?.id!!)
                mCurrentCommerce = item.comercio
            }

            override fun onResetListener() {}
        })
    }

    private fun getCategoryName(rubro: String): Task<String> {
        val tcs = TaskCompletionSource<String>()
        FirebaseFirestore.getInstance().collection(Constants.TRADE_CATEGORY_COLLECTION).document(rubro)
                .get().addOnSuccessListener {
                    if(it.exists()) {
                        val nombre = it.get("nombre").toString()
                        tcs.setResult(nombre)
                    } else {
                        tcs.setResult(null)
                    }
                }.addOnFailureListener { tcs.setException(it) }
        return tcs.task
    }

    private fun showLoyaltyPlans() {
        if (mCurrentCommerce != null) {
//            val intent = Intent(requireContext(), LoyaltyPlansActivity::class.java)
//            intent.putExtra("commerceId", mCurrentCommerce?.id)
//            intent.putExtra("commerceImage", mCurrentCommerce?.logo)
//            startActivity(intent)
        } else
            Functions.showWarning(requireContext(), "No hay comercio seleccionado.")
    }

    private fun showTransactionsCommerce() {
        if (mCurrentCommerce != null) {
            val intent = Intent(requireContext(), TransactionsActivity::class.java)
            intent.putExtra("commerceID", mCurrentCommerce?.id)
            intent.putExtra("commerceName", mCurrentCommerce?.nombre)
            startActivity(intent)
        } else
            Functions.showWarning(requireContext(), "No hay comercio seleccionado.")
    }

    fun showEmptyLayout(show: Boolean) = if (show) mEmptytId.visibility = View.VISIBLE else mEmptytId.visibility = View.GONE
}