package com.merckers.asesorcajero.core.firebase

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either

class SnapshotLiveData(private val query: Query) : LiveData<Either<Failure, QuerySnapshot>>(),
    EventListener<QuerySnapshot> {
    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        value = if (e != null) {
            Either.Left(Failure(e.message!!))
        } else {
            Either.Right(snapshots!!)
        }
    }

    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()

        registration?.also {
            it.remove()
            registration = null
        }
    }
}