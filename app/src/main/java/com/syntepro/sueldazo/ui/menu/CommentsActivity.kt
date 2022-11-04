package com.syntepro.sueldazo.ui.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.entity.firebase.Comentario
import com.syntepro.sueldazo.ui.menu.adapter.CommentsAdapter
import com.syntepro.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.activity_comments.*
import java.util.*

class CommentsActivity : AppCompatActivity() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private var cardList: ArrayList<Comentario>? = null
    private var commentsAdapter: CommentsAdapter? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.comentarios_menu)

        // Adapter
        cardList = ArrayList()
        commentsAdapter = CommentsAdapter(cardList!!)

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_id)
        recyclerView.setHasFixedSize(true)
        val rvLiLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = rvLiLayoutManager
        recyclerView.adapter = commentsAdapter
        btn_enviar.setOnClickListener{
            if (et_comentario.text.isNullOrEmpty() || et_comentario.text!!.length < 8) {
                Toast.makeText(this@CommentsActivity, getString(R.string.c_required), Toast.LENGTH_SHORT).show()
            } else {
                pb.visibility = View.VISIBLE
                btn_enviar.visibility = View.GONE
                sendComment()
            }
        }

        // Show Data
        readUserComments()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun readUserComments() {
        cardList!!.clear()
        db.collection("Comentarios")
                .whereEqualTo("idUsuario", Constants.userProfile?.idUserFirebase)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            if (document.exists()) {
                                val comment = document.toObject(Comentario::class.java)
                                cardList!!.add(comment)
                            }
                        }
                        commentsAdapter!!.notifyDataSetChanged()
                    }
                }
    }

    private fun sendComment() {
        val ids = TimeZone.getAvailableIDs()
        val tz = TimeZone.getTimeZone(ids[144])
        val c = Calendar.getInstance(tz)
        val newComment: MutableMap<String, Any> = HashMap()
        newComment["idUsuario"] = Constants.userProfile?.idUserFirebase ?: ""
        newComment["comentario"] = et_comentario.text.toString()
        newComment["fechaComentario"] = c.time
        db.collection("Comentarios")
                .add(newComment)
                .addOnSuccessListener {
                    pb!!.visibility = View.GONE
                    btn_enviar!!.visibility = View.VISIBLE
                    et_comentario.setText("")
                    Toast.makeText(this@CommentsActivity, getString(R.string.send_comment), Toast.LENGTH_SHORT).show()
                    readUserComments()
                }
    }
}