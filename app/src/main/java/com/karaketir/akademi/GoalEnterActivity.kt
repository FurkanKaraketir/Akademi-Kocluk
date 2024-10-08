package com.karaketir.akademi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityGoalEnterBinding
import java.util.UUID

class GoalEnterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

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


    private lateinit var binding: ActivityGoalEnterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var dersler = ArrayList<String>()
    private var secilenDers = ""
    private var kurumKodu = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGoalEnterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore
        val toplamCalisma = binding.hedefToplamCalismaEditText
        val cozulenSoru = binding.hedefToplamSoruEditText
        val goalSave = binding.goalSave
        val studentID = intent.getStringExtra("studentID").toString()
        val dersAdiSpinner = binding.hedefDersSpinner
        kurumKodu = intent.getStringExtra("kurumKodu").toString().toInt()


        db.collection("Lessons").orderBy("dersAdi", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    for (document in value) {
                        dersler.add(document.get("dersAdi").toString())
                    }


                    val studentAdapter = ArrayAdapter(
                        this@GoalEnterActivity, android.R.layout.simple_spinner_item, dersler
                    )

                    studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dersAdiSpinner.adapter = studentAdapter
                    dersAdiSpinner.onItemSelectedListener = this


                }
            }


        val documentID = UUID.randomUUID()


        goalSave.setOnClickListener {
            if (toplamCalisma.text.toString().isNotEmpty()) {
                toplamCalisma.error = null

                if (cozulenSoru.text.toString().isNotEmpty()) {
                    cozulenSoru.error = null


                    val data = hashMapOf(
                        "dersAdi" to secilenDers,
                        "toplamCalisma" to toplamCalisma.text.toString().toInt(),
                        "çözülenSoru" to cozulenSoru.text.toString().toInt()
                    )

                    db.collection("School").document(kurumKodu.toString()).collection("Student")
                        .document(studentID).collection("HaftalikHedefler")
                        .whereEqualTo("dersAdi", secilenDers).addSnapshotListener { value, _ ->

                            if (value != null) {
                                if (!value.isEmpty) {
                                    for (document in value) {
                                        db.collection("School").document(kurumKodu.toString())
                                            .collection("Student").document(studentID)
                                            .collection("HaftalikHedefler").document(document.id)
                                            .set(data).addOnSuccessListener {
                                                finish()
                                            }
                                    }
                                } else {
                                    db.collection("School").document(kurumKodu.toString())
                                        .collection("Student").document(studentID)
                                        .collection("HaftalikHedefler")
                                        .document(documentID.toString()).set(data)
                                        .addOnSuccessListener {
                                            finish()
                                        }
                                }
                            } else {
                                db.collection("School").document(kurumKodu.toString())
                                    .collection("Student").document(studentID)
                                    .collection("HaftalikHedefler").document(documentID.toString())
                                    .set(data).addOnSuccessListener {
                                        finish()
                                    }
                            }

                        }


                } else {
                    cozulenSoru.error = "Bu Alan Boş Bırakılamaz"
                }

            } else {
                toplamCalisma.error = "Bu Alan Boş Bırakılamaz"
            }
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        secilenDers = dersler[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}