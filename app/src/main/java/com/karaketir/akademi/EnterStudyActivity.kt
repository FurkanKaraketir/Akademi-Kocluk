package com.karaketir.akademi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityEnterStudyBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class EnterStudyActivity : AppCompatActivity() {

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


    private lateinit var binding: ActivityEnterStudyBinding
    private lateinit var auth: FirebaseAuth
    private var konuAdlari = ArrayList<String>()
    private var konuDk = 0
    private var soruDk = 0
    private var soruSayi = 0

    private lateinit var db: FirebaseFirestore
    private var kurumKodu = 0
    private var secilenKonu = ""
    private var secilenDocumentID = ""

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore

        kurumKodu = intent.getStringExtra("kurumKodu").toString().toInt()

        val textInputCurrentMinutes = binding.TextInputCurrentMinutes
        val textInputCurrentTestsMinutes = binding.TextInputCurrentTestsMinutes
        val textInputCurrentTests = binding.TextInputCurrentTests

        val currentMinutesEditText = binding.currentMinutesEditText
        val currentTestsMinutesEditText = binding.currentTestsMinutesEditText
        val currentTestsEditText = binding.currentTestsEditText
        val tarihSecButton = binding.studyDateButton


        val spinner = binding.studySpinner
        val documentID = UUID.randomUUID().toString()
        val subjectType = intent.getStringExtra("studyType")
        val dersAdi = intent.getStringExtra("dersAdi")
        secilenKonu = ""

        val cal = Calendar.getInstance()
        val cal2 = Calendar.getInstance()


        lifecycleScope.launch {
            val currentTime = Calendar.getInstance()


            tarihSecButton.setOnClickListener {

                val year = cal2.get(Calendar.YEAR)
                val month = cal2.get(Calendar.MONTH)
                val day = cal2.get(Calendar.DAY_OF_MONTH)

                val dpd =
                    DatePickerDialog(this@EnterStudyActivity, { _, year2, monthOfYear, dayOfMonth ->
                        tarihSecButton.text =
                            ("Çalışma Tarihi: $dayOfMonth/${monthOfYear + 1}/$year2")
                        cal.set(year2, monthOfYear, dayOfMonth, 0, 0, 0)
                        cal2.set(year2, monthOfYear, dayOfMonth, 0, 0, 0)
                    }, year, month, day)

                dpd.show()
            }

            cal.time = currentTime.time

            cal2.time = currentTime.time

            tarihSecButton.text =
                ("Çalışma Tarihi: ${cal2[Calendar.DAY_OF_MONTH]}/${cal[Calendar.MONTH] + 1}/${cal2[Calendar.YEAR]}")


            textInputCurrentMinutes.hint = "Kaç Dakika Konu Çalıştın?"
            textInputCurrentMinutes.helperText = "Kaç Dakika Konu Çalıştın?"

            textInputCurrentTestsMinutes.hint = "Bu Konuda Kaç Dakika Test Çözdün?"
            textInputCurrentTestsMinutes.helperText = "Bu Konuda Kaç Dakika Test Çözdün?"

            textInputCurrentTests.hint = "Bu Konuda Kaç Tane Soru Çözdün?"
            textInputCurrentTests.helperText = "Bu Konuda Kaç Tane Sorusu Çözdün?"

            db.collection("Lessons").document(dersAdi.toString()).collection(subjectType.toString())
                .orderBy("konuAdi", Query.Direction.ASCENDING).addSnapshotListener { value, _ ->
                    if (value != null) {
                        konuAdlari.clear()
                        for (document in value) {
                            try {
                                val arrayType = document.get("arrayType") as ArrayList<String>
                                if ("konu" in arrayType) {
                                    val konuAdi = document.get("konuAdi").toString()
                                    konuAdlari.add(konuAdi)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@EnterStudyActivity, e.localizedMessage, Toast.LENGTH_SHORT
                                ).show()
                            }


                        }
                        val studyAdapter = ArrayAdapter(
                            this@EnterStudyActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            konuAdlari
                        )

                        studyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = studyAdapter
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?, p1: View?, position: Int, p3: Long
                                ) {
                                    secilenKonu = konuAdlari[position]


                                    db.collection("Lessons").document(dersAdi.toString())
                                        .collection(subjectType.toString())
                                        .whereEqualTo("konuAdi", secilenKonu)
                                        .addSnapshotListener { value1, _ ->
                                            if (value1 != null) {
                                                for (document in value1) {
                                                    secilenDocumentID =
                                                        document.get("id").toString()
                                                }
                                            }
                                        }

                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }

                            }


                    }
                }


            val subjectTypeTitle = binding.subjectTypeTitle
            val studySaveButton = binding.studySaveButton





            subjectTypeTitle.text = "Tür: $subjectType"

            studySaveButton.setOnClickListener {
                studySaveButton.isClickable = false
                var stopper = false
                var stopper2 = false

                cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !

                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)


                if (currentMinutesEditText.text.toString().isNotEmpty()) {
                    currentMinutesEditText.error = null

                    konuDk = currentMinutesEditText.text.toString().toInt()

                    if (currentTestsMinutesEditText.text.toString().isNotEmpty()) {
                        currentTestsMinutesEditText.error = null
                        soruDk = currentTestsMinutesEditText.text.toString().toInt()

                        if (currentTestsEditText.text.toString().isNotEmpty()) {
                            currentTestsEditText.error = null

                            soruSayi = currentTestsEditText.text.toString().toInt()


                            val study = hashMapOf(
                                "id" to documentID,
                                "timestamp" to cal2.time,
                                "konuAnlatımı" to konuDk,
                                "konuTestiDK" to soruDk,
                                "dersAdi" to dersAdi,
                                "tür" to subjectType,
                                "konuAdi" to secilenKonu,
                                "toplamCalisma" to konuDk + soruDk,
                                "çözülenSoru" to soruSayi
                            )

                            val baslangicTarihi = cal.time


                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            val bitisTarihi = cal.time
                            Toast.makeText(
                                this@EnterStudyActivity, "Lütfen Bekleyiniz...", Toast.LENGTH_SHORT
                            ).show()

                            db.collection("School").document(kurumKodu.toString())
                                .collection("Student").document(auth.uid.toString())
                                .collection("Studies").whereEqualTo("dersAdi", dersAdi)
                                .whereEqualTo("tür", subjectType)
                                .whereEqualTo("konuAdi", secilenKonu)
                                .whereGreaterThan("timestamp", baslangicTarihi)
                                .whereLessThan("timestamp", bitisTarihi)
                                .addSnapshotListener { value, error ->
                                    if (error != null) {
                                        println(error.localizedMessage)
                                    }
                                    if (!stopper) {
                                        if (value != null) {
                                            if (!value.isEmpty) {
                                                for (document in value) {
                                                    val studyUpdate = hashMapOf(
                                                        "id" to document.id,
                                                        "timestamp" to cal2.time,
                                                        "konuAnlatımı" to konuDk + document.get(
                                                            "konuAnlatımı"
                                                        ).toString().toInt(),
                                                        "konuTestiDK" to soruDk + document.get("konuTestiDK")
                                                            .toString().toInt(),
                                                        "tür" to subjectType,
                                                        "dersAdi" to dersAdi,
                                                        "konuAdi" to secilenKonu,
                                                        "toplamCalisma" to konuDk + soruDk + document.get(
                                                            "konuAnlatımı"
                                                        ).toString()
                                                            .toInt() + document.get("konuTestiDK")
                                                            .toString().toInt(),
                                                        "çözülenSoru" to soruSayi + document.get(
                                                            "çözülenSoru"
                                                        ).toString().toInt()
                                                    )
                                                    stopper = true

                                                    db.collection("School")
                                                        .document(kurumKodu.toString())
                                                        .collection("Student")
                                                        .document(auth.uid.toString())
                                                        .collection("Studies").document(document.id)
                                                        .update(studyUpdate as Map<String, Any>)
                                                        .addOnSuccessListener {

                                                            if (!stopper2) {
                                                                db.collection("School")
                                                                    .document(kurumKodu.toString())
                                                                    .collection("Student")
                                                                    .document(auth.uid.toString())
                                                                    .collection("Duties")
                                                                    .whereGreaterThan(
                                                                        "bitisZamani", cal2.time
                                                                    ).whereEqualTo(
                                                                        "dersAdi", dersAdi
                                                                    ).whereEqualTo(
                                                                        "tür", subjectType
                                                                    ).whereEqualTo(
                                                                        "konuAdi", secilenKonu
                                                                    )
                                                                    .addSnapshotListener { value5, e5 ->


                                                                        if (!stopper2) {
                                                                            if (e5 != null) println(
                                                                                e5.localizedMessage
                                                                            )

                                                                            if (value5 != null) {
                                                                                for (document5 in value5) {


                                                                                    val gorevUpdate =
                                                                                        hashMapOf(
                                                                                            "toplamCalisma" to document5.get(
                                                                                                "toplamCalisma"
                                                                                            )
                                                                                                .toString()
                                                                                                .toInt() - (konuDk + soruDk),
                                                                                            "çözülenSoru" to document5.get(
                                                                                                "çözülenSoru"
                                                                                            )
                                                                                                .toString()
                                                                                                .toInt() - soruSayi
                                                                                        )

                                                                                    if (!stopper2) {
                                                                                        stopper2 =
                                                                                            true

                                                                                        db.collection(
                                                                                            "School"
                                                                                        ).document(
                                                                                            kurumKodu.toString()
                                                                                        )
                                                                                            .collection(
                                                                                                "Student"
                                                                                            )
                                                                                            .document(
                                                                                                auth.uid.toString()
                                                                                            )
                                                                                            .collection(
                                                                                                "Duties"
                                                                                            )
                                                                                            .document(
                                                                                                document5.id
                                                                                            )
                                                                                            .update(
                                                                                                gorevUpdate as Map<String, Any>
                                                                                            )
                                                                                            .addOnSuccessListener {

                                                                                                if (document5.get(
                                                                                                        "toplamCalisma"
                                                                                                    )
                                                                                                        .toString()
                                                                                                        .toInt() - (konuDk + soruDk) <= 0 && document5.get(
                                                                                                        "çözülenSoru"
                                                                                                    )
                                                                                                        .toString()
                                                                                                        .toInt() - soruSayi <= 0
                                                                                                ) {
                                                                                                    db.collection(
                                                                                                        "School"
                                                                                                    )
                                                                                                        .document(
                                                                                                            kurumKodu.toString()
                                                                                                        )
                                                                                                        .collection(
                                                                                                            "Student"
                                                                                                        )
                                                                                                        .document(
                                                                                                            auth.uid.toString()
                                                                                                        )
                                                                                                        .collection(
                                                                                                            "Duties"
                                                                                                        )
                                                                                                        .document(
                                                                                                            document5.id
                                                                                                        )
                                                                                                        .update(
                                                                                                            "tamamlandi",
                                                                                                            true
                                                                                                        )
                                                                                                        .addOnSuccessListener {
                                                                                                            stopper2 =
                                                                                                                true

                                                                                                            Toast.makeText(
                                                                                                                this@EnterStudyActivity,
                                                                                                                "İşlem Başarılı!",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            )
                                                                                                                .show()
                                                                                                            finish()
                                                                                                        }
                                                                                                } else {
                                                                                                    stopper2 =
                                                                                                        true
                                                                                                    Toast.makeText(
                                                                                                        this@EnterStudyActivity,
                                                                                                        "İşlem Başarılı!",
                                                                                                        Toast.LENGTH_SHORT
                                                                                                    )
                                                                                                        .show()
                                                                                                    finish()
                                                                                                }

                                                                                            }

                                                                                    }
                                                                                }
                                                                            }
                                                                        }


                                                                    }
                                                            }
                                                            Toast.makeText(
                                                                this@EnterStudyActivity,
                                                                "İşlem Başarılı!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            finish()


                                                        }


                                                }
                                            } else {
                                                stopper = true
                                                db.collection("School")
                                                    .document(kurumKodu.toString())
                                                    .collection("Student")
                                                    .document(auth.uid.toString())
                                                    .collection("Studies").document(documentID)
                                                    .set(study).addOnSuccessListener {

                                                        if (!stopper2) {
                                                            db.collection("School")
                                                                .document(kurumKodu.toString())
                                                                .collection("Student")
                                                                .document(auth.uid.toString())
                                                                .collection("Duties")
                                                                .whereGreaterThan(
                                                                    "bitisZamani", cal2.time
                                                                ).whereEqualTo(
                                                                    "dersAdi", dersAdi
                                                                ).whereEqualTo(
                                                                    "tür", subjectType
                                                                ).whereEqualTo(
                                                                    "konuAdi", secilenKonu
                                                                )
                                                                .addSnapshotListener { value5, e5 ->


                                                                    if (!stopper2) {
                                                                        if (e5 != null) println(
                                                                            e5.localizedMessage
                                                                        )

                                                                        if (value5 != null) {
                                                                            for (document5 in value5) {


                                                                                val gorevUpdate =
                                                                                    hashMapOf(
                                                                                        "toplamCalisma" to document5.get(
                                                                                            "toplamCalisma"
                                                                                        ).toString()
                                                                                            .toInt() - (konuDk + soruDk),
                                                                                        "çözülenSoru" to document5.get(
                                                                                            "çözülenSoru"
                                                                                        ).toString()
                                                                                            .toInt() - soruSayi
                                                                                    )

                                                                                if (!stopper2) {
                                                                                    stopper2 = true

                                                                                    db.collection(
                                                                                        "School"
                                                                                    ).document(
                                                                                        kurumKodu.toString()
                                                                                    ).collection(
                                                                                        "Student"
                                                                                    ).document(
                                                                                        auth.uid.toString()
                                                                                    ).collection(
                                                                                        "Duties"
                                                                                    ).document(
                                                                                        document5.id
                                                                                    ).update(
                                                                                        gorevUpdate as Map<String, Any>
                                                                                    )
                                                                                        .addOnSuccessListener {

                                                                                            if (document5.get(
                                                                                                    "toplamCalisma"
                                                                                                )
                                                                                                    .toString()
                                                                                                    .toInt() - (konuDk + soruDk) <= 0 && document5.get(
                                                                                                    "çözülenSoru"
                                                                                                )
                                                                                                    .toString()
                                                                                                    .toInt() - soruSayi <= 0
                                                                                            ) {
                                                                                                db.collection(
                                                                                                    "School"
                                                                                                )
                                                                                                    .document(
                                                                                                        kurumKodu.toString()
                                                                                                    )
                                                                                                    .collection(
                                                                                                        "Student"
                                                                                                    )
                                                                                                    .document(
                                                                                                        auth.uid.toString()
                                                                                                    )
                                                                                                    .collection(
                                                                                                        "Duties"
                                                                                                    )
                                                                                                    .document(
                                                                                                        document5.id
                                                                                                    )
                                                                                                    .update(
                                                                                                        "tamamlandi",
                                                                                                        true
                                                                                                    )
                                                                                                    .addOnSuccessListener {
                                                                                                        stopper2 =
                                                                                                            true

                                                                                                        Toast.makeText(
                                                                                                            this@EnterStudyActivity,
                                                                                                            "İşlem Başarılı!",
                                                                                                            Toast.LENGTH_SHORT
                                                                                                        )
                                                                                                            .show()
                                                                                                        finish()
                                                                                                    }
                                                                                            } else {
                                                                                                stopper2 =
                                                                                                    true
                                                                                                Toast.makeText(
                                                                                                    this@EnterStudyActivity,
                                                                                                    "İşlem Başarılı!",
                                                                                                    Toast.LENGTH_SHORT
                                                                                                )
                                                                                                    .show()
                                                                                                finish()
                                                                                            }

                                                                                        }

                                                                                }
                                                                            }
                                                                        }
                                                                    }


                                                                }
                                                        }

                                                        Toast.makeText(
                                                            this@EnterStudyActivity,
                                                            "İşlem Başarılı!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        finish()


                                                    }


                                                    .addOnFailureListener {
                                                        println(it.localizedMessage)
                                                    }


                                            }


                                        } else {

                                            db.collection("School").document(kurumKodu.toString())
                                                .collection("Student").document(auth.uid.toString())
                                                .collection("Studies").document(documentID)
                                                .set(study).addOnSuccessListener {

                                                    db.collection("School")
                                                        .document(kurumKodu.toString())
                                                        .collection("Student")
                                                        .document(auth.uid.toString())
                                                        .collection("Duties").whereGreaterThan(
                                                            "bitisZamani", cal2.time
                                                        ).whereEqualTo("dersAdi", dersAdi)
                                                        .whereEqualTo("tür", subjectType)
                                                        .whereEqualTo(
                                                            "konuAdi", secilenKonu
                                                        ).addSnapshotListener { value5, e5 ->
                                                            if (e5 != null) println(e5.localizedMessage)

                                                            if (!stopper2) {
                                                                if (value5 != null) {
                                                                    for (document5 in value5) {

                                                                        stopper2 = true
                                                                        val gorevUpdate = hashMapOf(
                                                                            "toplamCalisma" to document5.get(
                                                                                "toplamCalisma"
                                                                            ).toString()
                                                                                .toInt() - (konuDk + soruDk),
                                                                            "çözülenSoru" to document5.get(
                                                                                "çözülenSoru"
                                                                            ).toString()
                                                                                .toInt() - soruSayi
                                                                        )
                                                                        db.collection("School")
                                                                            .document(
                                                                                kurumKodu.toString()
                                                                            ).collection("Student")
                                                                            .document(auth.uid.toString())
                                                                            .collection("Duties")
                                                                            .document(
                                                                                document5.id
                                                                            ).update(
                                                                                gorevUpdate as Map<String, Any>
                                                                            ).addOnSuccessListener {
                                                                                if (document5.get(
                                                                                        "toplamCalisma"
                                                                                    ).toString()
                                                                                        .toInt() - (konuDk + soruDk) <= 0 && document5.get(
                                                                                        "çözülenSoru"
                                                                                    ).toString()
                                                                                        .toInt() - soruSayi <= 0
                                                                                ) {
                                                                                    db.collection(
                                                                                        "School"
                                                                                    ).document(
                                                                                        kurumKodu.toString()
                                                                                    ).collection(
                                                                                        "Student"
                                                                                    ).document(
                                                                                        auth.uid.toString()
                                                                                    ).collection(
                                                                                        "Duties"
                                                                                    ).document(
                                                                                        document5.id
                                                                                    ).update(
                                                                                        "tamamlandi",
                                                                                        true
                                                                                    )
                                                                                        .addOnSuccessListener {
                                                                                            Toast.makeText(
                                                                                                this@EnterStudyActivity,
                                                                                                "İşlem Başarılı!",
                                                                                                Toast.LENGTH_SHORT
                                                                                            ).show()
                                                                                            finish()
                                                                                        }
                                                                                } else {

                                                                                    finish()
                                                                                }
                                                                            }


                                                                    }
                                                                }

                                                            }


                                                        }


                                                }.addOnFailureListener {
                                                    println(it.localizedMessage)
                                                }
                                        }

                                    }
                                    Toast.makeText(
                                        this@EnterStudyActivity,
                                        "İşlem Başarılı",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val cal3 = Calendar.getInstance()

                                    cal3.time = currentTime.time

                                    db.collection("School").document(kurumKodu.toString())
                                        .collection("LastReports").document(auth.uid.toString())
                                        .set(
                                            hashMapOf(
                                                "id" to auth.uid.toString(),
                                                "timestamp" to cal3.time
                                            )
                                        )


                                    finish()


                                }


                        } else {
                            currentTestsEditText.error = "Bu Alanı Boş Bırakamazsın!"
                            studySaveButton.isClickable = true

                        }


                    } else {
                        currentTestsMinutesEditText.error = "Bu Alanı Boş Bırakamazsın!"
                        studySaveButton.isClickable = true

                    }


                } else {
                    currentMinutesEditText.error = "Bu Alanı Boş Bırakamazsın!"
                    studySaveButton.isClickable = true

                }
            }

        }

    }


}
