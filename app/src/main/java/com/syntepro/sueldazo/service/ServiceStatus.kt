package com.syntepro.sueldazo.service

class ServiceStatus {
    var type: Int =
        ERROR_TYPE_ANY
    var status: Int =
        STATUS_OK
    var message: String? = null
    var state: State =
        State.DONE

    companion object {
        const val ERROR_TYPE_NETWORK = 1
        const val ERROR_TYPE_SERVICE = 2
        const val ERROR_TYPE_ANY = 3

        const val STATUS_OK = 1
        const val STATUS_ERROR = 2
    }
}