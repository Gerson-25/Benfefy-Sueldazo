package com.syntepro.sueldazo.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.syntepro.sueldazo.entity.firebase.PlanLealtad
import com.syntepro.sueldazo.entity.firebase.TarjetaSellos
import java.text.SimpleDateFormat
import java.util.*

class UploadData {
    companion object {
        fun addLoyaltyPlans() {
            val commerce = "44D105C6-DFD6-476A-9400-070AF27AE609"
            val lp1 = with(PlanLealtad()) {
                categoria = PlanLealtad.PLAN_MILLAS
                nombre = "Plan Premium de Millas"
                descripcion = "Plan de acumulacion de 10 millas por cada dolar en compras"
                fechaCreacion = Date()
                terminos = "https://indoors-app.com"
                requiereCodigo = false
                activo = true
                sellos = 0
                this
            }
            val lp2 = with(PlanLealtad()) {
                categoria = PlanLealtad.PLAN_COBRANDING
                nombre = "Programa de Lealtad Co-Branding"
                descripcion = "Por cada compra de $100 recibe un cupon de descuento en comercios afiliados"
                fechaCreacion = Date()
                terminos = "https://indoors-app.com"
                requiereCodigo = true
                activo = true
                sellos = 0
                this
            }

            val lp3 = with(PlanLealtad()) {
                categoria = PlanLealtad.PLAN_AFILIACION
                nombre = "Programa Alumnos VIP"
                descripcion = "Todos los alumnos afiliados reciben cupones de descuento es comercios de la red"
                fechaCreacion = Date()
                terminos = "https://indoors-app.com"
                requiereCodigo = true
                activo = true
                sellos = 0
                this
            }

            val lp4 = with(PlanLealtad()) {
                categoria = PlanLealtad.PLAN_SELLOS
                nombre = "Programa de Sellos"
                descripcion = "Por cada compra de un café recibes un sello; al completar la tarjeta de 8 sellos obtienes un café gratis"
                fechaCreacion = Date()
                terminos = "https://indoors-app.com"
                requiereCodigo = false
                activo = true
                sellos = 8
                this
            }

            val db = FirebaseFirestore.getInstance().collection(Constants.TRADE_COLLECTION)
                    .document(commerce) .collection(Constants.LOYALTY_PLAN_COLLECTION)
            db.document().set(lp1).addOnSuccessListener { Log.i("Loyalty Plan", "Created!!") }
                    .addOnFailureListener { Log.e("Loyalty Plan Error", it.message!!) }
            db.add(lp2).addOnSuccessListener {Log.i("Loyalty Plan", "Created!!") }
                    .addOnFailureListener { Log.e("Loyalty Plan Error", it.message!!) }
            db.add(lp3).addOnSuccessListener { Log.i("Loyalty Plan", "Created!!") }
                    .addOnFailureListener { Log.e("Loyalty Plan Error", it.message!!) }
            db.add(lp4).addOnSuccessListener { Log.i("Loyalty Plan", "Created!!") }
                    .addOnFailureListener { Log.e("Loyalty Plan Error", it.message!!) }
        }
        @SuppressLint("SimpleDateFormat")
        fun createStampCards() {
            // Tarjeta completada
            val ts1 = with(TarjetaSellos()) {
                inicio  = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("20-11-2019 10:01:00")!!
                fin = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("31-13-2019 23:59:59")
                sellosRequeridos = 8
                sellosObtenidos = 8
                estatus = TarjetaSellos.STATUS_COMPLETED
                fechaCompletada = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("24-11-2019 20:19:37")
                fechaCanje = null
                fechaUltimoSello = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("24-11-2019 20:19:37")
                this
            }

            // Tarjeta Expirada
            val ts2 = with(TarjetaSellos()) {
                inicio  = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("15-05-2019 00:00:00")!!
                fin = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("30-06-2019 23:59:59")
                sellosRequeridos = 12
                sellosObtenidos = 5
                estatus = TarjetaSellos.STATUS_EXPIRED
                fechaCompletada = null
                fechaCanje = null
                fechaUltimoSello = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("18-06-2019 14:21:01")
                this
            }

            // Tarjeta Canjeada
            val ts3 = with(TarjetaSellos()) {
                inicio  = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("20-06-2019 08:32:40")!!
                fin = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("31-12-2019 23:59:59")
                sellosRequeridos = 6
                sellosObtenidos = 6
                estatus = TarjetaSellos.STATUS_REDEEMED
                fechaCompletada = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("17-11-2019 16:23:07")
                fechaCanje = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("17-11-2019 16:24:18")
                fechaUltimoSello = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("17-11-2019 16:23:07")
                this
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                    .document(userId!!) .collection(Constants.MY_LOYALTY_PLAN_COLLECTION)
                    .document("8S19sE57Ji3oNZpVVfAn").collection(Constants.STAMP_CARDS_COLLECTION)
            db.document().set(ts1).addOnSuccessListener { Log.i("Tarjeta de Sellos", "Created!!") }
            db.document().set(ts2).addOnSuccessListener { Log.i("Tarjeta de Sellos", "Created!!") }
            db.document().set(ts3).addOnSuccessListener { Log.i("Tarjeta de Sellos", "Created!!") }

        }
    }
}