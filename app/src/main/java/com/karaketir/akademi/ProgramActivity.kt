package com.karaketir.akademi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.adapter.DersProgramiAdapter
import com.karaketir.akademi.databinding.ActivityProgramBinding
import com.karaketir.akademi.models.Ders

class ProgramActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProgramBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var pazartesiList = ArrayList<Ders>()
    private var saliList = ArrayList<Ders>()
    private var carsambaList = ArrayList<Ders>()
    private var persembeList = ArrayList<Ders>()
    private var cumaList = ArrayList<Ders>()
    private var cumartesiList = ArrayList<Ders>()
    private var pazarList = ArrayList<Ders>()
    private var secilenZaman = 0
    private val kurumKodu = 763455
    private var personType = "Student"
    private lateinit var recyclerPazartesiAdapter: DersProgramiAdapter
    private lateinit var recyclerPazarAdapter: DersProgramiAdapter
    private lateinit var recyclerSaliAdapter: DersProgramiAdapter
    private lateinit var recyclerCarsambaAdapter: DersProgramiAdapter
    private lateinit var recyclerPersembeAdapter: DersProgramiAdapter
    private lateinit var recyclerCumaAdapter: DersProgramiAdapter
    private lateinit var recyclerCumartesiAdapter: DersProgramiAdapter
    private lateinit var studentID: String
    private lateinit var addLessonButton: FloatingActionButton
    private val zamanAraliklari = arrayOf("Bu Hafta", "Geçen Hafta")

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        studentID = intent.getStringExtra("studentID").toString()

        addLessonButton = binding.addLessonButton

        addLessonButton.setOnClickListener {
            val newIntent = Intent(this, AddProgramActivity::class.java)
            newIntent.putExtra("studentID", studentID)
            newIntent.putExtra("kurumKodu", kurumKodu.toString())
            this.startActivity(newIntent)
        }
        db.collection("User").document(auth.uid.toString()).get().addOnSuccessListener {
            personType = it.get("personType").toString()



            if (personType == "Student") {
                addLessonButton.visibility = View.GONE
                binding.timeSpinnerLayout.visibility = View.GONE

                studentID = auth.uid.toString()

            } else {
                addLessonButton.visibility = View.VISIBLE
                binding.timeSpinnerLayout.visibility = View.VISIBLE


                binding.paz.setOnClickListener {
                    deleteProgramDay("Pazartesi")
                }
                binding.sal.setOnClickListener {
                    deleteProgramDay("Salı")
                }
                binding.car.setOnClickListener {
                    deleteProgramDay("Çarşamba")
                }
                binding.per.setOnClickListener {
                    deleteProgramDay("Perşembe")
                }
                binding.cum.setOnClickListener {
                    deleteProgramDay("Cuma")
                }
                binding.cmt.setOnClickListener {
                    deleteProgramDay("Cumartesi")
                }
                binding.pzr.setOnClickListener {
                    deleteProgramDay("Pazar")
                }
            }
            hello()
        }


        val timeAdapter = ArrayAdapter(
            this@ProgramActivity, android.R.layout.simple_spinner_dropdown_item, zamanAraliklari
        )

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeSpinner.adapter = timeAdapter
        binding.timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                secilenZaman = p2
                hello()


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteProgramDay(day: String) {


        val signOutAlertDialog = AlertDialog.Builder(this)
        signOutAlertDialog.setTitle("$day Sil")
        signOutAlertDialog.setMessage("Silmek İstediğinize Emin misiniz?")
        signOutAlertDialog.setPositiveButton("Sil") { _, _ ->

            db.collection("School").document("763455").collection("Student").document(studentID)
                .collection("DersProgrami").document(day).collection("Dersler").document("0")
                .delete()
            db.collection("School").document("763455").collection("Student").document(studentID)
                .collection("DersProgrami").document(day).collection("Dersler").document("1")
                .delete()
            db.collection("School").document("763455").collection("Student").document(studentID)
                .collection("DersProgrami").document(day).collection("Dersler").document("2")
                .delete()
            db.collection("School").document("763455").collection("Student").document(studentID)
                .collection("DersProgrami").document(day).collection("Dersler").document("3")
                .delete()

            Toast.makeText(this, "İşlem Başarılı", Toast.LENGTH_SHORT).show()

        }
        signOutAlertDialog.setNegativeButton("İptal") { _, _ ->

        }
        signOutAlertDialog.show()


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun hello() {


        val layoutManager0 = LinearLayoutManager(this)
        val layoutManager1 = LinearLayoutManager(this)
        val layoutManager2 = LinearLayoutManager(this)
        val layoutManager3 = LinearLayoutManager(this)
        val layoutManager4 = LinearLayoutManager(this)
        val layoutManager5 = LinearLayoutManager(this)
        val layoutManager6 = LinearLayoutManager(this)

        val recyclerPazartesi = binding.recyclerPazartesi
        recyclerPazartesiAdapter = DersProgramiAdapter(pazartesiList, secilenZaman, personType)
        recyclerPazartesi.layoutManager = layoutManager0
        recyclerPazartesi.adapter = recyclerPazartesiAdapter


        val recyclerSali = binding.recyclerSali
        recyclerSaliAdapter = DersProgramiAdapter(saliList, secilenZaman, personType)
        recyclerSali.layoutManager = layoutManager1
        recyclerSali.adapter = recyclerSaliAdapter


        val recyclerCarsamba = binding.recyclerCarsamba
        recyclerCarsambaAdapter = DersProgramiAdapter(carsambaList, secilenZaman, personType)
        recyclerCarsamba.layoutManager = layoutManager2
        recyclerCarsamba.adapter = recyclerCarsambaAdapter


        val recyclerPersembe = binding.recyclerPersembe
        recyclerPersembeAdapter = DersProgramiAdapter(persembeList, secilenZaman, personType)
        recyclerPersembe.layoutManager = layoutManager3
        recyclerPersembe.adapter = recyclerPersembeAdapter


        val recyclerCuma = binding.recyclerCuma
        recyclerCumaAdapter = DersProgramiAdapter(cumaList, secilenZaman, personType)
        recyclerCuma.layoutManager = layoutManager4
        recyclerCuma.adapter = recyclerCumaAdapter


        val recyclerCumartesi = binding.recyclerCumartesi
        recyclerCumartesiAdapter = DersProgramiAdapter(cumartesiList, secilenZaman, personType)
        recyclerCumartesi.layoutManager = layoutManager5
        recyclerCumartesi.adapter = recyclerCumartesiAdapter


        val registerPazar = binding.recyclerPazar
        recyclerPazarAdapter = DersProgramiAdapter(pazarList, secilenZaman, personType)
        registerPazar.layoutManager = layoutManager6
        registerPazar.adapter = recyclerPazarAdapter


        db.collection("User").document(auth.uid.toString()).get().addOnSuccessListener {
            personType = it.get("personType").toString()

            if (personType == "Student") {
                addLessonButton.visibility = View.GONE
                binding.timeSpinnerLayout.visibility = View.GONE

                studentID = auth.uid.toString()
            } else {
                addLessonButton.visibility = View.VISIBLE
                binding.timeSpinnerLayout.visibility = View.VISIBLE
            }

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Pazartesi")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        pazartesiList.clear()
                        for (i in value) {

                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            pazartesiList.add(newDers)
                            recyclerPazartesiAdapter.notifyDataSetChanged()
                        }
                    }


                }
            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Salı")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        saliList.clear()
                        for (i in value) {


                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            saliList.add(newDers)
                            recyclerSaliAdapter.notifyDataSetChanged()


                        }
                    }


                }

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Çarşamba")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        carsambaList.clear()
                        for (i in value) {


                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            carsambaList.add(newDers)
                            recyclerCarsambaAdapter.notifyDataSetChanged()

                        }
                    }


                }

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Perşembe")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        persembeList.clear()
                        for (i in value) {

                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )
                            persembeList.add(newDers)
                            recyclerPersembeAdapter.notifyDataSetChanged()

                        }
                    }


                }


            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Cuma")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        cumaList.clear()
                        for (i in value) {


                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            cumaList.add(newDers)
                            recyclerCumaAdapter.notifyDataSetChanged()

                        }
                    }


                }

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Cumartesi")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        cumartesiList.clear()
                        for (i in value) {


                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            cumartesiList.add(newDers)
                            recyclerCumartesiAdapter.notifyDataSetChanged()

                        }
                    }


                }

            db.collection("School").document(kurumKodu.toString()).collection("Student")
                .document(studentID).collection("DersProgrami").document("Pazar")
                .collection("Dersler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ ->

                    if (value != null) {
                        pazarList.clear()
                        for (i in value) {


                            val newDersAdi = i.get("dersAdi").toString()
                            val newDersTuru = i.get("dersTuru").toString()
                            val newDersSure = i.get("dersSure").toString().toInt()
                            val newDersSoru = i.get("dersSoru").toString().toInt()
                            val newDersNumara = i.get("id").toString()
                            val newGun = i.get("dersGun").toString()
                            val newDers = Ders(
                                newDersAdi,
                                newDersTuru,
                                newDersSure,
                                newDersSoru,
                                newDersNumara,
                                studentID,
                                newGun
                            )

                            pazarList.add(newDers)
                            recyclerPazarAdapter.notifyDataSetChanged()

                        }
                    }


                }


        }

    }

}