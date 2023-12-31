package com.karaketir.akademi

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityAddProgramBinding

class AddProgramActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProgramBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var turler = arrayListOf("TYT", "AYT", "TYT ve AYT")
    private var gun = "Pazartesi"
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
    private var gunler =
        arrayListOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar")

    private var valuesID = arrayOf(
        "1", "2", "3", "4"
    )
    private val kurumKodu = 763455


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val studentID = intent.getStringExtra("studentID").toString()
        val gunSpinner = binding.gunSpinner

        val saveButton = binding.dutySaveButton

        val gunlerAdapter = ArrayAdapter(
            this@AddProgramActivity, R.layout.simple_spinner_dropdown_item, gunler
        )

        gunlerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        gunSpinner.adapter = gunlerAdapter
        gunSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                gun = gunler[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


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
                        this@AddProgramActivity, R.layout.simple_spinner_dropdown_item, dersAdlari
                    )
                    dersAdiAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    dersAdiSpinner.adapter = dersAdiAdapter
                    dersAdiSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?, p1: View?, position: Int, p3: Long
                            ) {
                                secilenDers = dersAdlari[position]

                                val dutyTurSpinner = binding.dutyTurSpinner

                                val dutyTurAdapter = ArrayAdapter(
                                    this@AddProgramActivity,
                                    R.layout.simple_spinner_dropdown_item,
                                    turler
                                )

                                dutyTurAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
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

        val sayiPicker = binding.numberPickerSayi
        sayiPicker.minValue = 0
        sayiPicker.maxValue = valuesID.size - 1
        sayiPicker.displayedValues = valuesID
        sayiPicker.wrapSelectorWheel = true
        sayiPicker.setOnValueChangedListener { _, _, newVal ->
            sayi = valuesID[newVal].toInt() - 1
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