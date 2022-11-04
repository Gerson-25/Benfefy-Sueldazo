package com.syntepro.sueldazo.ui.coupon.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.entity.firebase.Cupon
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.rv_card_items.view.*
import java.text.DecimalFormat
import java.util.*

class PagingCardAdapter (val options: FirestorePagingOptions<Cupon>, val ctx: Context, val adapterOnClick : (String) -> Unit)
    : FirestorePagingAdapter<Cupon, PagingCardAdapter.CardViewHolder>(options) {
    private var lastIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.rv_card_items, parent, false)
        return CardViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int, model: Cupon) {
        holder.bind(model, currentList!![position]!!.id, position)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> {
            }
            LoadingState.LOADING_MORE -> {
            }
            LoadingState.LOADED -> {
                (ctx as BaseActivity).stopRefreshing()
                val total = itemCount
                for (i in lastIndex until total) {
                    val model = getItem(i)!!.toObject(Cupon::class.java)
                    if (!model?.imagenCupon.isNullOrEmpty())
                        Picasso.get().load(model?.imagenCupon).fetch()
                }
            }
            LoadingState.FINISHED -> {
                (ctx as BaseActivity).stopRefreshing()
                (ctx).showEmptyLayout(itemCount == 0)
            }
            LoadingState.ERROR -> { }
        }
    }

    inner class CardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val mFav = view.fav

        fun bind(model: Cupon, id: String, position: Int) {
            val format = DecimalFormat("###,###.00")
            val m: String

            if (Constants.userCountryProfile != null) {
                m = Constants.userCountryProfile!!.moneda
            } else {
                m = Functions.userSession.moneda
                RoomDataBase.destroyInstance()
            }

            view.titleId.text = model.titulo
            view.txt_subtitulo.text = model.subtitulo

            // Validation Coupon Code
            if(model.fbCodeType.isNullOrEmpty() || model.fbCodeType == "1") {
                val real: Double = model.precioReal.toDouble()
                view.txt_precioReal.text = Functions.fromHtml("<strike>" + m + " " + format.format(real) + "</strike>")
                val descuento: Double = model.precioDesc.toDouble()
                val desc: String = m + " " + format.format(descuento)
                view.txt_precioDesc.text = desc
            } else if (model.fbCodeType == "2") {
                view.txt_precioReal.text = ""
                val descuento: Double = model.precioDesc.toDouble()
                val desc: String = format.format(descuento) + " %"
                view.txt_precioDesc.text = desc
            } else if (model.fbCodeType == "3") {
                view.txt_precioReal.text = ""
                val descuento: Double = model.precioDesc.toDouble()
                val desc: String = m + " " + format.format(descuento)
                view.txt_precioDesc.text = desc
            }

            Picasso.get()
                    .load(model.imagenCupon)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.notfound)
                    .into(view.couponImageView)

            Picasso.get()
                    .load(model.imagenComercio)
                    .fit()
                    .centerInside()
                    .error(R.drawable.notfound)
                    .into(view.commerceImage)

            favorite(id)

            mFav.setOnClickListener {
                val animScale = AnimationUtils.loadAnimation(ctx, R.anim.scale_fav)
                it.startAnimation(animScale)
                addFav(id)
            }

            view.card_view.setOnClickListener {
                Log.e("Click", "Ok")
                adapterOnClick(id)
            }
            view.data.setOnClickListener {
                Log.e("Click", "Ok Perform Data")
                view.card_view.performClick()
            }
            view.image.setOnClickListener {
                Log.e("Click", "Ok Perform Image")
                view.card_view.performClick()
            }

            view.actionId.setOnClickListener {
                Log.e("Click", "Ok Perform Button")
                view.card_view.performClick()
            }
        }

        private fun favorite(id: String) {
            val favorito = "0"
            FirebaseFirestore.getInstance().collection("Favorito")
                    .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase ?: "")
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                        if (task.isSuccessful) {
                            if (task.result!!.isEmpty) {
                                mFav.setBackgroundResource(R.drawable.ic_favorito)
                            } else {
                                val animScale = AnimationUtils.loadAnimation(ctx, R.anim.scale_fav)
                                mFav.startAnimation(animScale)
                                mFav.setBackgroundResource(R.drawable.ic_corazon)
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.exception)
                    }
        }

        private fun addFav(id: String) {
            val favorito = "0"
            FirebaseFirestore.getInstance().collection("Favorito")
                    .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase ?: "")
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener { task: Task<QuerySnapshot> ->
                        if (task.isSuccessful) {
                            if (task.result!!.isEmpty) {
                                mFav.setBackgroundResource(R.drawable.ic_corazon)

                                val pais: String

                                if (Constants.userCountryProfile != null) {
                                    pais = Constants.userCountryProfile!!.abr
                                } else {
                                    pais = Functions.userCountry
                                    RoomDataBase.destroyInstance()
                                }

                                val newFav: MutableMap<String, Any> = HashMap()
                                newFav["idUsuario"] = Constants.userProfile?.idUserFirebase ?: ""
                                newFav["idCupon"] = id
                                newFav["fecha"] = Date(System.currentTimeMillis())
                                newFav["estado"] = "0"
                                newFav["tipo"] = "c"
                                newFav["pais"] = pais
                                FirebaseFirestore.getInstance().collection("Favorito")
                                        .add(newFav)
                                        .addOnFailureListener{ mFav.setBackgroundResource(R.drawable.ic_favorito) }
                            } else {
                                mFav.setBackgroundResource(R.drawable.ic_favorito)
                                for (document in task.result!!) {
                                    val ref: DocumentReference = FirebaseFirestore.getInstance().collection("Favorito").document(document.id)
                                    ref.update("estado", "1")
                                            .addOnFailureListener { mFav.setBackgroundResource(R.drawable.ic_corazon) }
                                }
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.exception)
                    }
        }
    }

}