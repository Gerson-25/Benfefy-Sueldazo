package com.syntepro.appbeneficiosbolivia.ui.menu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.StatisticsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.TransactionsActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showInformation
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showUserQR
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    private var readUser: Usuario? = null
    private var db: FirebaseFirestore? = null
    private var moneda: String? = null
    private var mensajePopUp: String? = null
    private var perfil: ImageView? = null
    private var txt_nombre: TextView? = null
    private var txt_correo: TextView? = null
    private var txt_ahorro: TextView? = null
    private var idUser: String? = null
    private var res = 0.0
    private lateinit var btn_infoProfile: View
    private lateinit var transactionsID: LinearLayout
    private lateinit var btnProfileContainer: LinearLayout
    private lateinit var qrID: LinearLayout
    private lateinit var estadisticasID: LinearLayout
    private lateinit var profileID: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // ROOM
        val roomDataBase = RoomDataBase.getRoomDatabase(this@ProfileActivity)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.perfil_menu)

        // Firebase
        db = FirebaseFirestore.getInstance()

        // Options Menu
        transactionsID = findViewById(R.id.transactionsID)
        qrID = findViewById(R.id.qrID)
        estadisticasID = findViewById(R.id.estadisticasID)
        profileID = findViewById(R.id.profileID)
        btn_infoProfile = findViewById(R.id.btn_infoProfile)
        btnProfileContainer = findViewById(R.id.btnProfileContainer)

        // Views
        perfil = findViewById(R.id.circleImageView)
        txt_nombre = findViewById(R.id.txt_nombre)
        txt_correo = findViewById(R.id.txt_correo)
        txt_ahorro = findViewById(R.id.txt_ahorro)

        // Country User
        val cu = roomDataBase.accessDao().country
        moneda = cu.moneda
        txt_pais.text = cu.pais
//        val countryUser = roomDataBase.accessDao().country
//        txt_pais.text = countryUser.pais

        btn_infoProfile.setOnClickListener { showAlertDialog(mensajePopUp) }
        fb_add.setOnClickListener { openGallery() }

        profileID.setOnClickListener {
            val intent = Intent(this, EditProfileActivity2::class.java)
            intent.putExtra("provenance", 1)
            startActivity(intent)
        }

        transactionsID.setOnClickListener {
            val intent = Intent(applicationContext, TransactionsActivity::class.java)
            startActivity(intent)
        }

        qrID.setOnClickListener {
            if (idUser != null) showUserQR(this)
        }

        estadisticasID.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        // Show Data
        readExchangeUser()
    }

    override fun onStart() {
        super.onStart()
        readCouponCustomObject()
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        try {
            mAnimationSet!!.cancel()
            mAnimationSet!!.end()
            mAnimationSet = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        readUser = null
        finish()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /**
     * Gerson Aquino 28JUN2021
     *
     * we have reduce the number of assignations in this method
     * to avoid any performance delay
     *
     */
    @SuppressLint("WrongConstant")
    private fun readCouponCustomObject() {
        txt_nombre!!.text = "${Constants.userProfile?.names} ${Constants.userProfile?.lastNames}"
        txt_correo!!.text = Constants.userProfile?.email
        Constants.userProfile?.photoUrl?.let {
            Picasso.get()
                .load(it)
                .error(R.drawable.notfound)
                .placeholder(R.drawable.cargando)
                .into(perfil)
        }

        val user = db!!.collection("Usuarios").document(Constants.userProfile?.idUserFirebase ?: "")

        user.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (Objects.requireNonNull(documentSnapshot).exists()) {
                    readUser = documentSnapshot.toObject(Usuario::class.java)
                    readUser?.let {
                        idUser = it.id
                        val total = 7
                        var counter = 0
                        val name = it.nombre + " " + it.apellido
                        val nom = it.nombre
                        if (nom == "") counter++
                        val depto = it.departamento
                        if (depto == "") counter++
                        val prov = it.provincia
                        if (prov == "") counter++
                        val civil = it.estadoCivil
                        if (civil == "") counter++
                        val birth = it.fechaNac
                        if (birth == "") counter++
                        val gender = it.genero
                        if (gender == "") counter++
                        val phone = it.numeroTel
                        if (phone == "") counter++
//                        if (counter == 0) btnProfileContainer.visibility = View.GONE else {
//                            val dif = total - counter.toDouble()
//                            val div = dif / total.toDouble()
//                            val accumulate = div * 100
//                            val formateador = DecimalFormat("###,###.00")
//                            setAlphaAnimation(btn_infoProfile)
//                            btnProfileContainer.visibility = View.VISIBLE
//                            mensajePopUp = getString(R.string.first_profile) + " " + formateador.format(accumulate) + getString(R.string.second_profile)
//                        }
                    }
                }
            }
        }
    }


    private fun readExchangeUser() {
        db!!.collection("Codigo")
                .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase)
                .whereEqualTo("estado", "1")
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.result!!.isEmpty) {
                        val a = "$moneda 00.00"
                        txt_ahorro!!.text = a
                    } else {
                        if (task.isSuccessful) {
                            val formateador = DecimalFormat("###,###.00")
                            for (document in task.result!!) {
                                if (document["precioReal"] != null && document["precioDescuento"] != null) {
                                    val a = Objects.requireNonNull(document["precioReal"]).toString().toDouble()
                                    val b = Objects.requireNonNull(document["precioDescuento"]).toString().toDouble()
                                    var acumulado = a - b
                                    acumulado = ((acumulado * 100).roundToInt() / 100f).toDouble()
                                    res += acumulado
                                }
                            }
                            val a = moneda + " " + formateador.format(res)
                            txt_ahorro!!.text = a
                        }
                    }
                }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data!!.data
            perfil!!.setImageURI(imageUri)
            cargarImagen()
        }
    }

    fun cargarImagen() {
        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "idUser.jpg"
        val userRef = storageRef.child("imagesProfile/${Constants.userProfile?.idUserFirebase ?: ""}.jpg")

        // Create a reference to 'images/idUser.jpg'
        //StorageReference userImagesRef = storageRef.child("imagesProfile/"+ Objects.requireNonNull(currentUser).getUid() +".jpg");

        // While the file names are the same, the references point to different files
        //userRef.getName().equals(userImagesRef.getName());    // true
        //userRef.getPath().equals(userImagesRef.getPath());    // false

        // Get the data from an ImageView as bytes
        perfil!!.isDrawingCacheEnabled = true
        perfil!!.buildDrawingCache()
        val bitmap = (perfil!!.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()
        Log.e("Tamaño de la imagen", data.size.toString() + "kb")
        val uploadTask = userRef.putBytes(data)
        uploadTask.addOnFailureListener { }.addOnSuccessListener { userRef.downloadUrl.addOnSuccessListener { uri: Uri -> userUpdate(uri.toString()) } }
    }

    private fun userUpdate(img: String?) {
        val ref = db!!.collection(Constants.USER_COLLECTION).document(Constants.userProfile?.idUserFirebase ?: "")
        ref.update("imagenPerfil", img)
    }

    private fun showAlertDialog(message: String?) {
        showInformation(this, getString(R.string.progreso_perfil), message)
    }

    companion object {
        private const val PICK_IMAGE = 100
        private var mAnimationSet: AnimatorSet? = AnimatorSet()
        private fun setAlphaAnimation(v: View?) {
            try {
                val fadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f)
                fadeOut.duration = 1000
                val fadeIn = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f)
                fadeIn.duration = 1000
                mAnimationSet!!.play(fadeIn).after(fadeOut)
                mAnimationSet!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        try {
                            mAnimationSet!!.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
                mAnimationSet!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}