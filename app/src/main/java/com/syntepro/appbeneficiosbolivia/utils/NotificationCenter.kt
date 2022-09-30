package com.syntepro.appbeneficiosbolivia.utils

import java.util.*

class NotificationCenter private constructor() {

    private val registeredObjects: HashMap<String, ArrayList<Runnable>> = HashMap()

    @Synchronized
    fun addFunctionForNotification(notificationName: String, r: Runnable) {
        var list = registeredObjects[notificationName]
        if (list == null) {
            list = ArrayList()
            registeredObjects[notificationName] = list
        }
        list.add(r)
    }

    @Synchronized
    fun verifyNotificationExists(notificationName: String) {
        val it: MutableIterator<Map.Entry<String, ArrayList<Runnable>>> = registeredObjects.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.key == notificationName) {
                it.remove()
            }
        }
    }

    @Synchronized
    fun removeFunctionForNotification(notificationName: String, r: Runnable) {
        val list = registeredObjects[notificationName]
        list?.remove(r)
    }

    @Synchronized
    fun postNotification(notificationName: String) {
        val list = registeredObjects[notificationName]
        if (list != null) {
            for (r in list) r.run()
        }
    }

    companion object {
        //static reference for singleton
        private var _instance: NotificationCenter? = null

        // Returning the reference
        @Synchronized
        fun defaultCenter(): NotificationCenter? {
            if (_instance == null) _instance = NotificationCenter()
            return _instance
        }
    }

}