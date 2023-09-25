package com.karaketir.akademi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityEditProgramBinding

class EditProgramActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProgramBinding
    private var gun = "Pazartesi"
    private var count = "0"
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var turler = arrayListOf("TYT", "AYT", "TYT ve AYT")
    private var sayi = 0
    private var dersAdlari = ArrayList<String>()

    private var secilenDers = "Matematik"
    private var secilenTur = "TYT"
    private var dakika = 0
    private var soru = 0
    private var values = arrayOf(
        "0",
        "30",
        "40",
        "50",
        "60",
        "70",
        "80",
        "90",
        "100",
        "110",
        "120",
        "130",
        "140",
        "150",
        "160",
        "170",
        "180"
    )
    private val kurumKodu = 763455

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore
        val saveButton = binding.dutySaveButton
        val studentID = intent.getStringExtra("studentID").toString()

        val gunText = binding.dayText
        val contText = binding.dersCountText

        gun = intent.getStringExtra("gun").toString()
        count = intent.getStringExtra("count").toString()
        sayi = count.toInt()
        gunText.text = "Gün: $gun"
        contText.text = "Ders Konumu: " + (count.toInt() + 1).toString()

        setupNumberPickerForStringValues()

        db.collection("Lessons").orderBy("dersAdi", Query.Direction.ASCENDING)
            .addSnapshotListener { dersAdlariDocument, _ ->
                if (dersAdlariDocument != null) {
                    dersAdlari.clear()
                    for (dersAdi in dersAdlariDocument) {
                        dersAdlari.add(dersAdi.id)
                    }
                    val dersAdiSpinner = binding.dutyDersAdiSpinner
                    val dersAdiAdapter = ArrayAdapter(
                        this@EditProgramActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        dersAdlari
                    )
                    dersAdiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dersAdiSpinner.adapter = dersAdiAdapter
                    dersAdiSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?, p1: View?, position: Int, p3: Long
                            ) {
                                secilenDers = dersAdlari[position]

                                val dutyTurSpinner = binding.dutyTurSpinner

                                val dutyTurAdapter = ArrayAdapter(
                                    this@EditProgramActivity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    turler
                                )

                                dutyTurAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                dutyTurSpinner.adapter = dutyTurAdapter
                                dutyTurSpinner.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                                        ) {
                                            secilenTur = turler[p2]


                                        }

                                        override fun onNothingSelected(p0: AdapterView<*>?) {
                                        }

                                    }


                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                        }

                }
            }

        saveButton.setOnClickListener {

            val data = hashMapOf(
                "id" to (sayi).toString(),
                "dersGun" to gun,
                "dersAdi" to secilenDers,
                "dersTuru" to secilenTur,
                "dersSure" to dakika,
                "dersSoru" to soru
            )

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document(gun).collection("Dersler")
                .document((sayi).toString()).set(data).addOnSuccessListener {
                    Toast.makeText(this, "İşlem Başarılı", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }


    }


    private fun setupNumberPickerForStringValues() {
        val numberPicker = binding.numberPickerDk
        numberPicker.minValue = 0
        numberPicker.maxValue = values.size - 1
        numberPicker.displayedValues = values
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            dakika = values[newVal].toInt()
        }


        val soruPicker = binding.numberPickerSoru
        soruPicker.minValue = 0
        soruPicker.maxValue = values.size - 1
        soruPicker.displayedValues = values
        soruPicker.wrapSelectorWheel = true
        soruPicker.setOnValueChangedListener { _, _, newVal ->
            soru = values[newVal].toInt()
        }
    }

}