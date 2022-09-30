package com.syntepro.appbeneficiosbolivia.ui.coupon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.service.VipCoupon
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.rv_card_items.view.*
import java.text.DecimalFormat
import java.util.*

class VipCouponAdapter(private var itemList: ArrayList<VipCoupon>,
                       val adapterOnClick: (String) -> Unit)
    : RecyclerView.Adapter<VipCouponAdapter.MyViewHolder>() {

    val format = DecimalFormat("###,###.00")
    val m = Constants.userCountryProfile?.moneda ?: run {
        Functions.userSession.moneda
        RoomDataBase.destroyInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_card_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int { return itemList.size }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (!itemList[position].urlImagenCampana.isNullOrEmpty()) Picasso.get().load(itemList[position].urlImagenCampana).fetch()
        if (!itemList[position].urlImagenComercio.isNullOrEmpty()) Picasso.get().load(itemList[position].urlImagenComercio).fetch()
        holder.bind(itemList[position], itemList[position].idCampana!!, position)
    }

    inner class MyViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val titulo = view.titleId
        private val subtitulo = view.txt_subtitulo
        private val precioReal = view.txt_precioReal
        private val precioDesc = view.txt_precioDesc
        private val imagen = view.couponImageView
        private val imgComercio = view.commerceImage
        private val mFav = view.fav
        private val rowId = view.card_view
        private val viewGlass = view.viewGlass
        private val data = view.data
        private val image = view.image
        private val action = view.actionId

        fun bind(model: VipCoupon, id: String, position: Int) {
            if (!model.isUsuarioActivo) viewGlass.visibility = View.VISIBLE

            titulo.text = model.titulo
            subtitulo.text = model.subtitulo

            // Validation Coupon Code
            if(model.fbCodeType.isNullOrEmpty() || model.fbCodeType == "1") {
                val real: Double = model.precioReal
                precioReal.text = Functions.fromHtml("<strike>" + m + " " + format.format(real) + "</strike>")
                val descuento: Double = model.precioDescuento
                val desc: String = "$m ${format.format(descuento)}"
                precioDesc.text = desc
            } else if (model.fbCodeType == "2") {
                precioReal.text = ""
                val descuento: Double = model.precioDescuento
                val desc: String = format.format(descuento) + " %"
                precioDesc.text = desc
            } else if (model.fbCodeType == "3") {
                precioReal.text = ""
                val descuento: Double = model.precioDescuento
                val desc: String = "$m ${format.format(descuento)}"
                precioDesc.text = desc
            }

            if (imagen != null) {
                Picasso.get()
                        .load(model.urlImagenCampana)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.notfound)
                        .into(imagen)
            }

            if (imgComercio != null) {
                Picasso.get()
                        .load(model.urlImagenComercio)
                        .fit()
                        .centerInside()
                        .error(R.drawable.notfound)
                        .into(imgComercio)
            }

            favorite(id, itemView.context)

            mFav.setOnClickListener {
                if (model.isUsuarioActivo) {
                    val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                    it.startAnimation(animScale)
                    addFav(id)
                } else Functions.showWarning(itemView.context, "Has sido bloqueado temporalmente del plan: ${model.nombrePlan}")
            }

            rowId.setOnClickListener {
                if (model.isUsuarioActivo) adapterOnClick(id.toUpperCase())
                else Functions.showWarning(itemView.context, "Has sido bloqueado temporalmente del plan: ${model.nombrePlan}")
            }

            data.setOnClickListener { rowId.performClick() }
            image.setOnClickListener { rowId.performClick() }
            action.setOnClickListener { rowId.performClick() }
        }

        @SuppressLint("DefaultLocale")
        private fun favorite(id: String, ctx: Context) {
            val favorite = 0
            FirebaseFirestore.getInstance().collection("FavoritoVIP")
                    .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase ?: "")
                    .whereEqualTo("idCupon", id.toUpperCase())
                    .whereEqualTo("estado", favorite)
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

        @SuppressLint("DefaultLocale")
        private fun addFav(id: String) {
            val favorite = 0
            FirebaseFirestore.getInstance().collection("FavoritoVIP")
                    .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase ?: "")
                    .whereEqualTo("idCupon", id.toUpperCase())
                    .whereEqualTo("estado", favorite)
                    .get()
                    .addOnCompleteListener { task: Task<QuerySnapshot> ->
                        if (task.isSuccessful) {
                            if (task.result!!.isEmpty) {
                                mFav.setBackgroundResource(R.drawable.ic_corazon)
                                val newFav: MutableMap<String, Any> = HashMap()
                                newFav["idUsuario"] = Constants.userProfile?.idUserFirebase ?: ""
                                newFav["idCupon"] = id.toUpperCase()
                                newFav["fecha"] = Date(System.currentTimeMillis())
                                newFav["estado"] = 0
                                newFav["tipo"] = "c"
                                newFav["pais"] = Functions.userCountry
                                FirebaseFirestore.getInstance().collection("FavoritoVIP")
                                        .add(newFav)
                                        .addOnFailureListener{ mFav.setBackgroundResource(R.drawable.ic_favorito) }
                            } else {
                                mFav.setBackgroundResource(R.drawable.ic_favorito)
                                for (document in task.result!!) {
                                    val ref: DocumentReference = FirebaseFirestore.getInstance().collection("FavoritoVIP").document(document.id)
                                    ref.update("estado", 1)
                                            .addOnFailureListener { mFav.setBackgroundResource(R.drawable.ic_corazon) }
                                }
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.exception)
                    }
        }

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        RoomDataBase.destroyInstance()
        super.onDetachedFromRecyclerView(recyclerView)
    }

}