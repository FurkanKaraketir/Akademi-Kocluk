package com.karaketir.akademi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityRegisterBinding
import com.karaketir.akademi.services.openLink

class RegisterActivity : AppCompatActivity() {

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


    private lateinit var documentID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding
    private var nameAndSurname = ""
    private var grade = 0
    private var selection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore


        val registerLayout = binding.registerLayout
        val studentButton = binding.studentButton
        val teacherButton = binding.teacherButton
        val textInputClass = binding.TextInputClass
        val signUpButton = binding.signUpButton
        val gradeText = binding.classEditText
        val emailEditText = binding.emailRegisterEditText
        val passwordEditText = binding.passwordRegisterEditText
        val codeEditText = binding.passwordRegisterCode
        val nameAndSurnameEditText = binding.nameAndSurnameEditText
        val kullanici = binding.kullanimBtn
        val gizlilik = binding.gizBtn
        val cerez = binding.cerezBtn
        kullanici.setOnClickListener {
            openLink(
                "https://docs.google.com/document/d/1mzyFHaD6UUrB85BkXRO9r3fS8_jo_sUxoDPbn0Qi2kk/edit?usp=sharing",
                this
            )
        }
        gizlilik.setOnClickListener {
            openLink(
                "https://docs.google.com/document/d/1BRYlg76TfhzmxdokrkXg8k3wbua5xbgbSx-xSqokJMM/edit?usp=sharing",
                this
            )
        }
        cerez.setOnClickListener {
            openLink(
                "https://docs.google.com/document/d/1jFbZ8IW4AEb8ZMBy57YLQAJAwl5uQ34W4q_yMhqc8aU/edit?usp=sharing",
                this
            )
        }

        signUpButton.setOnClickListener {
            Toast.makeText(this, "Lütfen Bekleyiniz...", Toast.LENGTH_SHORT).show()
            if (emailEditText.text.toString().isNotEmpty()) {
                emailEditText.error = null

                if (passwordEditText.text.toString().isNotEmpty()) {

                    if (passwordEditText.text.toString().length >= 6) {
                        passwordEditText.error = null
                        if (gradeText.text.toString().isNotEmpty() || selection == 2) {
                            gradeText.error = null
                            if (nameAndSurnameEditText.text.toString().isNotEmpty()) {
                                nameAndSurnameEditText.error = null

                                if (codeEditText.text.toString().isNotEmpty()) {
                                    codeEditText.error = null

                                    if (codeEditText.text.toString() == "hxoabibk") {

                                        nameAndSurname = nameAndSurnameEditText.text.toString()

                                        grade = try {
                                            gradeText.text.toString().toInt()
                                        } catch (e: Exception) {
                                            0
                                        }

                                        signUp(
                                            emailEditText.text.toString(),
                                            passwordEditText.text.toString()
                                        )
                                    } else {
                                        codeEditText.error = "Hatalı Giriş Kodu"

                                    }


                                } else {
                                    codeEditText.error = "Bu Alan Boş Bırakılamaz"
                                }


                            } else {
                                nameAndSurnameEditText.error = "Bu Alan Boş Bırakılamaz"

                            }


                        } else {
                            gradeText.error = "Bu Alan Boş Bırakılamaz"
                        }
                    } else {
                        passwordEditText.error = "Şifre En Az 6 Karakter Uzunluğunda Olmalı"
                    }


                } else {
                    passwordEditText.error = "Bu Alan Boş Bırakılamaz"
                }
            } else {
                emailEditText.error = "Bu Alan Boş Bırakılamaz"
            }
        }




        studentButton.setOnClickListener {
            selection = 1
            registerLayout.visibility = View.VISIBLE
            textInputClass.visibility = View.VISIBLE
        }

        teacherButton.setOnClickListener {
            selection = 2
            registerLayout.visibility = View.VISIBLE
            textInputClass.visibility = View.GONE
        }


    }

    private fun signUp(email: String, password: String) {
        val kurumKodu = 763455
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                documentID = auth.uid!!


                when (selection) {
                    1 -> {
                        val user = hashMapOf(
                            "email" to email,
                            "grade" to grade,
                            "id" to documentID,
                            "nameAndSurname" to nameAndSurname,
                            "personType" to "Student",
                            "kurumKodu" to kurumKodu,
                            "teacher" to "",
                        )

                        db.collection("User").document(documentID).set(user).addOnSuccessListener {

                            db.collection("School").document(kurumKodu.toString())
                                .collection("Student").document(documentID).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Başarılı", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    this.startActivity(intent)
                                    finish()
                                }


                        }.addOnFailureListener { e ->
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    2 -> {
                        val user = hashMapOf(
                            "email" to email,
                            "id" to documentID,
                            "nameAndSurname" to nameAndSurname,
                            "personType" to "Teacher",
                            "kurumKodu" to kurumKodu,
                        )

                        db.collection("User").document(documentID).set(user).addOnSuccessListener {

                            db.collection("School").document(kurumKodu.toString())
                                .collection("Teacher").document(documentID).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Başarılı", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    this.startActivity(intent)
                                    finish()
                                }


                        }.addOnFailureListener { e ->
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    baseContext, "Kayıt Başarısız!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}