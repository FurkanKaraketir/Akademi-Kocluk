package com.karaketir.akademi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.adapter.SubjectAdapter
import com.karaketir.akademi.databinding.ActivitySubjectCompBinding
import com.karaketir.akademi.models.Subject

class SubjectCompActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubjectCompBinding
    private lateinit var selectClassAndSubjectText: CardView
    private lateinit var calssPanel: CardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: SubjectAdapter
    private val subjectList = ArrayList<Subject>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val dersList = ArrayList<String>()
    private val benimKonularim = ArrayList<String>()
    private var studentID = ""
    private lateinit var progressBar: ProgressBar

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectCompBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val mathButton = binding.mathButton
        val fizik = binding.physics
        val turkce = binding.turkce
        val cografya = binding.cografya
        val biyoloji = binding.biyoloji
        val kimya = binding.kimya
        val tarih = binding.tarih
        val geometri = binding.geometri
        val felsefe = binding.felsefe
        val din = binding.din
        progressBar = binding.progressBar


        studentID = intent.getStringExtra("studentID").toString()
        recyclerView = binding.subjectRecycler


        recyclerView.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = SubjectAdapter(subjectList, studentID)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = recyclerViewAdapter

        selectClassAndSubjectText = binding.selectClassAndSubjectText
        calssPanel = binding.calssPanel


        mathButton.setOnClickListener { it ->

            //create instance of PopupMenu
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Matematik", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Matematik", "AYT")

                }
                false
            }
        }

        geometri.setOnClickListener {
            //create instance of PopupMenu
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Geometri", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Geometri", "AYT")

                }
                false
            }
        }

        felsefe.setOnClickListener {

            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Felsefe", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Fesefe", "AYT")

                }
                false
            }
        }

        din.setOnClickListener {

            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Din", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Din", "AYT")

                }
                false
            }
        }

        fizik.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Fizik", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Fizik", "AYT")

                }
                false
            }
        }
        turkce.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Türkçe-Edebiyat", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Türkçe-Edebiyat", "AYT")

                }
                false
            }
        }
        cografya.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Coğrafya", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Coğrafya", "AYT")

                }
                false
            }
        }
        biyoloji.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Biyoloji", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Biyoloji", "AYT")

                }
                false
            }
        }
        kimya.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Kimya", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Kimya", "AYT")

                }
                false
            }
        }
        tarih.setOnClickListener {
            val popup = PopupMenu(applicationContext, it)
            //inflate menu with layout mainmenu
            popup.inflate(R.menu.subject_context)
            popup.show()

            popup.setOnMenuItemClickListener { item ->
                no()

                if (item.itemId == R.id.TYT) {

                    show("Tarih", "TYT")


                }

                if (item.itemId == R.id.AYT) {
                    show("Tarih", "AYT")

                }
                false
            }
        }


    }

    private fun no() {
        selectClassAndSubjectText.visibility = View.GONE
        calssPanel.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun show(dersAdi: String, tur: String) {

        var progressStats: Float

        db.collection("Lessons").document(dersAdi).collection(tur)
            .addSnapshotListener { konuAdlari, _ ->
                if (konuAdlari != null) {
                    dersList.clear()
                    for (i in konuAdlari) {
                        val newName = i.get("konuAdi").toString()
                        dersList.add(newName)
                    }

                    db.collection("School").document("763455").collection("Student")
                        .document(studentID).collection("tamamlananKonular")
                        .addSnapshotListener { benimKonuAdlarim, _ ->
                            if (benimKonuAdlarim != null) {
                                benimKonularim.clear()
                                for (i in benimKonuAdlarim) {
                                    val newMyName = i.get("konuAdi").toString()
                                    benimKonularim.add(newMyName)
                                }
                                var myCount = 0
                                subjectList.clear()
                                for (a in dersList) {
                                    if (a in benimKonularim) {
                                        myCount += 1
                                        val newSubject = Subject(a, true)
                                        subjectList.add(newSubject)
                                    } else {
                                        val newSubject = Subject(a, false)
                                        subjectList.add(newSubject)
                                    }
                                }
                                subjectList.sortBy { subject ->
                                    subject.name
                                }

                                progressStats = ((myCount * 100) / (dersList.size)).toFloat()
                                binding.proggressStats.text = "Tamamlanma Oranı: %$progressStats"
                                progressBar.progress = progressStats.toInt()
                                recyclerViewAdapter.notifyDataSetChanged()


                            }
                        }

                }
            }
    }
}