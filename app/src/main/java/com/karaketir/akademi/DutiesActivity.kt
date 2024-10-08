package com.karaketir.akademi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.adapter.DutiesRecyclerAdapter
import com.karaketir.akademi.databinding.ActivityDutiesBinding
import com.karaketir.akademi.models.Duty

class DutiesActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    init {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var dutiesRecyclerAdapter: DutiesRecyclerAdapter
    private var gorevTurleri =
        arrayOf("Tamamlanmayan Görevler", "Tamamlanan Görevler", "Tüm Görevler")
    private var dutyList = ArrayList<Duty>()
    private lateinit var dutyAddButton: FloatingActionButton

    private lateinit var binding: ActivityDutiesBinding
    private var kurumKodu = 0
    private var studentID = ""
    private var personType = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDutiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore
        studentID = intent.getStringExtra("studentID").toString()

        kurumKodu = intent.getStringExtra("kurumKodu").toString().toInt()
        personType = intent.getStringExtra("personType").toString()

        val dutiesRecyclerView = binding.dutiesRecyclerView
        val gorevTuruSpinner = binding.gorevSpinner
        dutyAddButton = binding.addDutyButton
        val dutyAdapter = ArrayAdapter(
            this@DutiesActivity, android.R.layout.simple_spinner_item, gorevTurleri
        )
        dutyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gorevTuruSpinner.adapter = dutyAdapter
        gorevTuruSpinner.onItemSelectedListener = this

        if (personType == "Teacher") {
            dutyAddButton.visibility = View.VISIBLE
        } else {
            dutyAddButton.visibility = View.GONE
        }


        val layoutManager = GridLayoutManager(applicationContext, 2)

        dutiesRecyclerView.layoutManager = layoutManager

        dutiesRecyclerAdapter = DutiesRecyclerAdapter(dutyList, kurumKodu, personType = "fdf")

        dutiesRecyclerView.adapter = dutiesRecyclerAdapter


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {


        when (p2) {
            0 -> {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(studentID).collection("Duties").whereEqualTo("tamamlandi", false)
                    .orderBy("eklenmeTarihi", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            println(e.localizedMessage)
                        }
                        if (value != null) {
                            dutyList.clear()
                            for (document in value) {
                                val konuAdi = document.get("konuAdi").toString()
                                val tur = document.get("tür").toString()
                                val dersAdi = document.get("dersAdi").toString()
                                val toplamCalisma = document.get("toplamCalisma").toString()
                                val cozulenSoru = document.get("çözülenSoru").toString()
                                val bitisZamani = document.get("bitisZamani") as Timestamp
                                val dutyTamamlandi = document.get("tamamlandi") as Boolean

                                val currentDuty = Duty(
                                    konuAdi,
                                    toplamCalisma,
                                    studentID,
                                    dersAdi,
                                    tur,
                                    cozulenSoru,
                                    bitisZamani,
                                    document.id,
                                    dutyTamamlandi
                                )
                                dutyList.add(currentDuty)
                            }
                            dutiesRecyclerAdapter.notifyDataSetChanged()


                        }
                    }
            }

            1 -> {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(studentID).collection("Duties").whereEqualTo("tamamlandi", true)
                    .orderBy("eklenmeTarihi", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            println(e.localizedMessage)
                        }
                        if (value != null) {
                            dutyList.clear()
                            for (document in value) {
                                val konuAdi = document.get("konuAdi").toString()
                                val tur = document.get("tür").toString()
                                val dersAdi = document.get("dersAdi").toString()
                                val toplamCalisma = document.get("toplamCalisma").toString()
                                val cozulenSoru = document.get("çözülenSoru").toString()
                                val bitisZamani = document.get("bitisZamani") as Timestamp
                                val dutyTamamlandi = document.get("tamamlandi") as Boolean

                                val currentDuty = Duty(
                                    konuAdi,
                                    toplamCalisma,
                                    studentID,
                                    dersAdi,
                                    tur,
                                    cozulenSoru,
                                    bitisZamani,
                                    document.id,
                                    dutyTamamlandi
                                )
                                dutyList.add(currentDuty)
                            }
                            dutiesRecyclerAdapter.notifyDataSetChanged()


                        }
                    }
            }

            2 -> {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(studentID).collection("Duties")
                    .orderBy("eklenmeTarihi", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            println(e.localizedMessage)
                        }
                        if (value != null) {
                            dutyList.clear()
                            for (document in value) {
                                val konuAdi = document.get("konuAdi").toString()
                                val tur = document.get("tür").toString()
                                val dersAdi = document.get("dersAdi").toString()
                                val toplamCalisma = document.get("toplamCalisma").toString()
                                val cozulenSoru = document.get("çözülenSoru").toString()
                                val bitisZamani = document.get("bitisZamani") as Timestamp
                                val dutyTamamlandi = document.get("tamamlandi") as Boolean

                                val currentDuty = Duty(
                                    konuAdi,
                                    toplamCalisma,
                                    studentID,
                                    dersAdi,
                                    tur,
                                    cozulenSoru,
                                    bitisZamani,
                                    document.id,
                                    dutyTamamlandi
                                )
                                dutyList.add(currentDuty)
                            }
                            dutiesRecyclerAdapter.notifyDataSetChanged()


                        }
                    }
            }

        }


        dutyAddButton = binding.addDutyButton

        dutyAddButton.setOnClickListener {
            val sendIntent = Intent(this, EnterDutyActivity::class.java)
            sendIntent.putExtra("studentID", studentID)
            sendIntent.putExtra("kurumKodu", kurumKodu.toString())
            this.startActivity(sendIntent)
        }


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}