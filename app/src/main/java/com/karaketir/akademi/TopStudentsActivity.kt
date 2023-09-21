package com.karaketir.akademi

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.adapter.StudentPlacementAdapter
import com.karaketir.akademi.databinding.ActivityTopStudentsBinding
import com.karaketir.akademi.models.StudentPlacment
import java.util.Calendar

class TopStudentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopStudentsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val mainHash = hashMapOf<String, Int>()
    private val idList = ArrayList<String>()
    private val studentList = ArrayList<StudentPlacment>()
    private val kurumKodu = 763455
    private val zamanAraliklari =
        arrayOf("Tüm Zamanlar", "Bugün", "Dün", "Bu Hafta", "Geçen Hafta", "Bu Ay", "Geçen Ay")
    private var secilenZaman = "Tüm Zamanlar"
    private var criteriaSelection = "Soru Sayısı"
    private val criteria = arrayOf("Soru Sayısı", "Çalışma Süresi")
    private var arrayCount = 0
    private var check = false
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var studentsRecyclerAdapter: StudentPlacementAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore


        val layoutManager = LinearLayoutManager(applicationContext)

        binding.studentPlacementRecyclerView.layoutManager = layoutManager

        studentsRecyclerAdapter = StudentPlacementAdapter(studentList)

        binding.studentPlacementRecyclerView.adapter = studentsRecyclerAdapter




        binding.getResults.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.transitionsContainer)
            binding.progressCircular.visibility = View.VISIBLE
            siralamaHazirla()


        }


        val studyAdapter = ArrayAdapter(
            this@TopStudentsActivity, android.R.layout.simple_spinner_dropdown_item, criteria
        )

        studyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.studySpinner.adapter = studyAdapter
        binding.studySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                criteriaSelection = criteria[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val timeAdapter = ArrayAdapter(
            this@TopStudentsActivity, android.R.layout.simple_spinner_dropdown_item, zamanAraliklari
        )

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeSpinner.adapter = timeAdapter
        binding.timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secilenZaman = zamanAraliklari[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        handler.post(object : Runnable {
            override fun run() {
                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                handler.postDelayed(this, 1000)
                updateTime()
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTime() {

        if (idList.isNotEmpty()) {
            if (idList.size >= arrayCount + 1) {
                valueAdding(arrayCount)
                arrayCount += 1
            } else {

                if (!check) {
                    Toast.makeText(this, "İşlem Başarılı", Toast.LENGTH_LONG).show()
                    studentsRecyclerAdapter.notifyDataSetChanged()
                    TransitionManager.beginDelayedTransition(binding.transitionsContainer)
                    binding.progressCircular.visibility = View.GONE


                    check = true
                }
            }
        }


    }

    private fun siralamaHazirla() {

        db.collection("School").document(kurumKodu.toString()).collection("Student").get()
            .addOnSuccessListener {


                for (i in it.documents) {

                    if (i.get("teacher").toString() != "" && i.get("grade").toString() == "12") {
                        idList.add(i.id)

                    }


                }
                println(idList.size)
            }


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun valueAdding(position: Int) {
        var toplamCalisma = 0


        var cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0

        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)

        var baslangicTarihi = cal.time
        var bitisTarihi = cal.time


        when (secilenZaman) {

            "Bugün" -> {
                baslangicTarihi = cal.time


                cal.add(Calendar.DAY_OF_YEAR, 1)
                bitisTarihi = cal.time
            }

            "Dün" -> {
                bitisTarihi = cal.time

                cal.add(Calendar.DAY_OF_YEAR, -1)
                baslangicTarihi = cal.time

            }

            "Bu Hafta" -> {
                cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                baslangicTarihi = cal.time


                cal.add(Calendar.WEEK_OF_YEAR, 1)
                bitisTarihi = cal.time

            }

            "Geçen Hafta" -> {
                cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                bitisTarihi = cal.time


                cal.add(Calendar.DAY_OF_YEAR, -7)
                baslangicTarihi = cal.time


            }

            "Bu Ay" -> {

                cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0

                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)

                cal.set(Calendar.DAY_OF_MONTH, 1)
                baslangicTarihi = cal.time


                cal.add(Calendar.MONTH, 1)
                bitisTarihi = cal.time


            }

            "Geçen Ay" -> {
                cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0

                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)

                cal.set(Calendar.DAY_OF_MONTH, 1)
                bitisTarihi = cal.time


                cal.add(Calendar.MONTH, -1)
                baslangicTarihi = cal.time

            }

            "Tüm Zamanlar" -> {
                cal.set(1970, Calendar.JANUARY, Calendar.DAY_OF_WEEK)
                baslangicTarihi = cal.time


                cal.set(2077, Calendar.JANUARY, Calendar.DAY_OF_WEEK)
                bitisTarihi = cal.time

            }
        }


        if (criteriaSelection != "Soru Sayısı") {

            if (secilenZaman != "Tüm Zamanlar") {

                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(idList[position]).collection("Studies")
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).get().addOnSuccessListener { my ->

                        for (i in my.documents) {
                            toplamCalisma += i.get("toplamCalisma").toString().toInt()
                        }
                        mainHash[idList[position]] = toplamCalisma

                        val sortedMap = mainHash.toList().sortedBy { (_, value) -> value }.toMap()

                        studentList.clear()
                        for (a in sortedMap.keys) {

                            val newStudent = StudentPlacment(a, sortedMap[a]!!)
                            studentList.add(newStudent)

                        }
                        studentList.reverse()
                        studentsRecyclerAdapter.notifyDataSetChanged()

                    }

            } else {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(idList[position]).collection("Studies").get()
                    .addOnSuccessListener { my ->

                        for (i in my.documents) {
                            toplamCalisma += i.get("toplamCalisma").toString().toInt()
                        }
                        mainHash[idList[position]] = toplamCalisma

                        val sortedMap = mainHash.toList().sortedBy { (_, value) -> value }.toMap()

                        studentList.clear()
                        for (a in sortedMap.keys) {

                            val newStudent = StudentPlacment(a, sortedMap[a]!!)
                            studentList.add(newStudent)

                        }
                        studentList.reverse()
                        studentsRecyclerAdapter.notifyDataSetChanged()

                    }
            }

        } else {

            if (secilenZaman != "Tüm Zamanlar") {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(idList[position]).collection("Studies").get()
                    .addOnSuccessListener { my ->

                        for (i in my.documents) {
                            toplamCalisma += i.get("çözülenSoru").toString().toInt()
                        }
                        mainHash[idList[position]] = toplamCalisma

                        val sortedMap = mainHash.toList().sortedBy { (_, value) -> value }.toMap()

                        studentList.clear()
                        for (a in sortedMap.keys) {

                            val newStudent = StudentPlacment(a, sortedMap[a]!!)
                            studentList.add(newStudent)

                        }
                        studentList.reverse()
                        studentsRecyclerAdapter.notifyDataSetChanged()

                    }
            } else {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(idList[position]).collection("Studies")
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).get().addOnSuccessListener { my ->

                        for (i in my.documents) {
                            toplamCalisma += i.get("çözülenSoru").toString().toInt()
                        }
                        mainHash[idList[position]] = toplamCalisma

                        val sortedMap = mainHash.toList().sortedBy { (_, value) -> value }.toMap()

                        studentList.clear()
                        for (a in sortedMap.keys) {

                            val newStudent = StudentPlacment(a, sortedMap[a]!!)
                            studentList.add(newStudent)

                        }
                        studentList.reverse()
                        studentsRecyclerAdapter.notifyDataSetChanged()

                    }
            }

        }


    }


}