package com.karaketir.akademi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.SubjectRowBinding
import com.karaketir.akademi.models.Subject

class SubjectAdapter(private val subjectList: ArrayList<Subject>, private val studentID: String) :
    RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SubjectRowBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.subject_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            val myItem = subjectList[position]
            db = FirebaseFirestore.getInstance()
            auth = FirebaseAuth.getInstance()

            var active = false

            db.collection("User").document(auth.uid.toString()).get().addOnSuccessListener {
                if (it.get("personType").toString() == "Teacher") {
                    active = true
                }
                println(active)

                binding.subjectName.text = myItem.name
                binding.subjectName.setOnClickListener {
                    if (active) {


                        if (myItem.stats) {
                            db.collection("School").document("763455").collection("Student")
                                .document(studentID).collection("tamamlananKonular")
                                .document(myItem.name).delete()
                        } else {
                            db.collection("School").document("763455").collection("Student")
                                .document(studentID).collection("tamamlananKonular")
                                .document(myItem.name).set(
                                    hashMapOf("konuAdi" to myItem.name)
                                )

                        }
                    }
                }


            }



            if (myItem.stats) {
                binding.subjectStats.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)

            } else {
                binding.subjectStats.setImageResource(R.drawable.ic_baseline_error_outline_24)

            }


        }
    }
}