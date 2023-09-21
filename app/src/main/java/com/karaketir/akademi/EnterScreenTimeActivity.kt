package com.karaketir.akademi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityEnterScreenTimeBinding
import java.util.Calendar

class EnterScreenTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterScreenTimeBinding
    private var studentID = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterScreenTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val tarihSecButton = binding.tarihSecButton
        val dutySaveButton = binding.dutySaveButton
        val screenTimeEditText = binding.screenTimeEditText
        var intYear = 2023
        var intMonth = 9
        var intDay = 21
        studentID = intent.getStringExtra("studentID").toString()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        tarihSecButton.setOnClickListener {


            val dpd = DatePickerDialog(this, { _, year2, monthOfYear, dayOfMonth ->
                tarihSecButton.text = ("Ekran Tarihi: $dayOfMonth/${monthOfYear + 1}/$year2")
                intYear = year2
                intMonth = monthOfYear + 1
                intDay = dayOfMonth
                c.set(year2, monthOfYear, dayOfMonth, 0, 0, 0)
            }, year, month, day)

            dpd.show()
        }


        dutySaveButton.setOnClickListener {

            if (screenTimeEditText.text.toString().isNotEmpty()) {
                screenTimeEditText.error = null

                val newID = "$intDay-$intMonth-$intYear"

                db.collection("School").document("763455").collection("Student").document(studentID)
                    .collection("ScreenTimes").document(newID).set(
                        hashMapOf(
                            "timestamp" to c, "time" to screenTimeEditText.text.toString().toInt()
                        )
                    ).addOnSuccessListener {
                        finish()
                    }


            } else {
                screenTimeEditText.error = "Bu Alan Boş Bırakılamaz"
            }


        }


    }
}