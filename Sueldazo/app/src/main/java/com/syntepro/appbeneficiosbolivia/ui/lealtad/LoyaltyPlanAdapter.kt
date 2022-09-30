package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.gson.JsonParser
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.MisPlanesLealtad
import com.syntepro.appbeneficiosbolivia.entity.service.UnlinkLoyalty
import com.syntepro.appbeneficiosbolivia.service.NetworkService
import kotlinx.android.synthetic.main.loyalty_plan_row.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class LoyaltyPlanAdapter(options: FirestorePagingOptions<MisPlanesLealtad>, val ctx: Context, private val source: Int)
    : FirestorePagingAdapter<MisPlanesLealtad, LoyaltyPlanAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.loyalty_plan_row, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: MisPlanesLealtad) {
        holder.bind(model)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADED-> {
                (ctx as BaseActivity).stopRefreshing()
            }
            LoadingState.FINISHED -> {
                (ctx as BaseActivity).stopRefreshing()
                if(source == LoyaltyPlansActivity.SOURCE_PLANS)
                    (ctx).showEmptyLayout(itemCount == 0)
                else
                    (ctx as LoyaltyPlansActivity).showEmptyLayoutSeals(itemCount == 0)
            }
            LoadingState.ERROR -> {
            }
        }
    }

    inner class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val mPlan = view.planId
        private val mDate = view.dateId
        private val mTerms = view.conditionsId
        private val mUnfollowId = view.unfollowId

        fun bind(model: MisPlanesLealtad) {
            mPlan.text = model.nombrePlan
            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.US)
            mDate.text = Functions.fromHtml(String.format(ctx.getString(R.string.following_from_label), formatter.format(model.fechaAfiliacion)))
            mUnfollowId.setOnClickListener { unFollow(model) }
            mTerms.setOnClickListener { viewTerms(model) }
        }

        private fun unFollow(plan: MisPlanesLealtad) {
            val builder = AlertDialog.Builder(ctx)
            with(builder) {
                setTitle(ctx.getString(R.string.unlink_trade_label))
                setMessage(ctx.getString(R.string.are_you_sure_label))
                setPositiveButton(ctx.getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                    val unlinkLoyalty = UnlinkLoyalty()
                    unlinkLoyalty.pais = Functions.userCountry
                    unlinkLoyalty.idComercio = plan.comercio
                    unlinkLoyalty.idPlan = plan.plan
                    unlinkLoyalty.idPlanUsuario = plan.documentId
                    unlinkLoyalty.idUsuario = Functions.userUID

                    val call: Call<ResponseBody> = getRetrofit().create(NetworkService::class.java).unlinkLoyaltyPlan(unlinkLoyalty)

                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Functions.showError(ctx, ctx.getString(R.string.error_connection))
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.code() == 200) {
                                val stringResponse = response.body()?.string()

                                val element = JsonParser().parse(stringResponse)
                                val objectJSON = element.asJsonObject

                                val error = objectJSON.get("isError").asBoolean
                                val result = objectJSON.get("isOk").asBoolean
                                if (error || !result)
                                    Functions.showWarning(ctx, objectJSON.get("errorMessage").asString)
                                else {
                                    currentList!!.dataSource.invalidate()
                                    dialog.dismiss()
                                }
                            } else
                                Functions.showError(ctx, response.message())
                        }
                    })
//                    val db = FirebaseFirestore.getInstance()
//                    val user = FirebaseAuth.getInstance().currentUser!!.uid
//                    db.collection(Global.USERS_COLLECTION).document(user).collection(Global.MY_LOYALTY_PLAN_COLLECTION)
//                            .document(plan.id).update("activo", false, "fechaFinAfiliacion", Date())
//                            .addOnSuccessListener {
//                                // Substract 1 to Members
//                                db.collection(Global.TRADE_COLLECTION).document(plan.comercio)
//                                        .collection(Global.LOYALTY_PLAN_COLLECTION).document(plan.plan)
//                                        .update("miembros", FieldValue.increment(-1))
//                                        .addOnSuccessListener {
//                                            currentList!!.dataSource.invalidate()
//                                            dialog.dismiss()
//                                        }
//                                        .addOnFailureListener {  Fn.showError(ctx, it.message!!)  }
//
//                            }
//                            .addOnFailureListener { Fn.showError(ctx, it.message) }
                }
                setNegativeButton(android.R.string.no, negativeButtonClick)
                show()
            }
        }

        private val negativeButtonClick = { dialog: DialogInterface, _: Int -> dialog.dismiss() }

        private fun viewTerms(plan: MisPlanesLealtad) {
            val intent = Intent(ctx, TermsViewActivity::class.java)
            intent.putExtra("commerceId", plan.comercio)
            intent.putExtra("planId", plan.plan)
            intent.putExtra("planName", plan.nombrePlan)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            (ctx as BaseActivity).startActivity(intent)
        }

        private fun getRetrofit(): Retrofit {
            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }
            val client : OkHttpClient = OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
            }.build()

            return Retrofit.Builder()
                    .baseUrl(NetworkService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
    }
}
