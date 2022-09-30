package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.entity.firebase.Comercio
import com.syntepro.appbeneficiosbolivia.entity.firebase.Rubro

object ParentDataFactory {
    const val MAX_TRENDS_PER_CATEGORY: Long = 3

    fun getCategories(rubroId: String?): Task<MutableList<ParentModel>> {
        val tcs = TaskCompletionSource<MutableList<ParentModel>>()
        val list = mutableListOf<ParentModel>()

        getQuery(rubroId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val tasks = arrayOfNulls<Task<ParentModel>>(it.result!!.size())
                var i = 0
                for (parent in it.result!!)
                    tasks[i++] = getCommerceList(parent.id, parent.toObject(Rubro::class.java))
                Tasks.whenAll(tasks.toMutableList()).addOnCompleteListener {
                    tasks.forEach { task ->
                        list.add(task?.result!!)
                    }
                    tcs.setResult(list)
                }
            } else {
                if (list.size > 0)
                    tcs.setResult(list)
                else
                    tcs.setResult(null)
            }
        }.addOnFailureListener { tcs.setException(it) }
        return tcs.task
    }

    private fun getQuery(rubroId: String?): Query {
        return if (rubroId.isNullOrEmpty()) {
            FirebaseFirestore.getInstance().collection(Constants.TRADE_CATEGORY_COLLECTION)
                    .whereEqualTo("isActivo", true)
                    .whereArrayContains("paises", Functions.userCountry)
                    .orderBy("orden", Query.Direction.ASCENDING)
        } else {
            FirebaseFirestore.getInstance().collection(Constants.TRADE_CATEGORY_COLLECTION)
                    .whereEqualTo("isActivo", true)
                    .whereArrayContains("paises", Functions.userCountry)
                    .whereEqualTo(FieldPath.documentId(), rubroId)
        }
    }

    private fun getCommerceList(rubroId: String, rubro: Rubro): Task<ParentModel> {
        val tcs = TaskCompletionSource<ParentModel>()
        val child = arrayListOf<Comercio>()
        FirebaseFirestore.getInstance().collection(Constants.TRADE_COLLECTION)
                .whereEqualTo("idRubro", rubroId)
                .whereEqualTo("pais", Functions.userCountry)
                .whereEqualTo("isActivo", true)
                .limit(MAX_TRENDS_PER_CATEGORY)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (parent in it.result!!) {
                            val grp = parent.toObject(Comercio::class.java)
                            child.add(grp)
                        }
                        val pm = ParentModel(
                                rubroId,
                                rubro.nombre!!,
                                rubro.imagen,
                                rubro.totalComercios?.first { r -> r.pais == Functions.userCountry }?.total
                                        ?: 0,
                                rubro.icono,
                                child
                        )
                        tcs.setResult(pm)
                    } else {
                        tcs.setResult(null)
                    }
                }.addOnFailureListener { tcs.setException(it) }
        return tcs.task
    }

}