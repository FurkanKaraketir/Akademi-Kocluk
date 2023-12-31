package com.karaketir.akademi.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.StudentRowBinding
import com.karaketir.akademi.models.Student

open class AllStudentsRecyclerAdapter(private val studentList: ArrayList<Student>) :
    RecyclerView.Adapter<AllStudentsRecyclerAdapter.StudentHolder>() {
    private lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    val kurumKodu = 763455

    class StudentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StudentRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.student_row, parent, false)
        return StudentHolder(view)
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        with(holder) {
            if (studentList.isNotEmpty() && position >= 0 && position < studentList.size) {

                val myItem = studentList[position]

                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                binding.studentDeleteButton.visibility = View.GONE
                binding.studentAddButton.visibility = View.VISIBLE

                binding.studentNameTextView.text = myItem.studentName

                binding.studentAddButton.setOnClickListener {

                    val addStudent = AlertDialog.Builder(holder.itemView.context)
                    addStudent.setTitle("Öğrenci Ekle")
                    addStudent.setMessage("${myItem.studentName} Öğrencisini Koçluğunuza Eklemek İstediğinizden Emin misiniz?")
                    addStudent.setPositiveButton("EKLE") { _, _ ->
                        db.collection("School").document(kurumKodu.toString()).collection("Student")
                            .document(myItem.id).update("teacher", auth.uid.toString())
                        db.collection("User").document(myItem.id)
                            .update("teacher", auth.uid.toString())
                    }
                    addStudent.setNegativeButton("İPTAL") { _, _ ->

                    }
                    addStudent.show()


                }

                binding.studentGradeTextView.text = myItem.grade.toString()


            }

        }


    }

    override fun getItemCount(): Int {
        return studentList.size
    }
}