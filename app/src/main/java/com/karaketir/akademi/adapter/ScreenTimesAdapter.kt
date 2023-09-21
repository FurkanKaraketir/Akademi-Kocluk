package com.karaketir.akademi.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.ScreenTimeRowBinding
import com.karaketir.akademi.models.ScreenTime

class ScreenTimesAdapter(private val timeList: ArrayList<ScreenTime>) :
    RecyclerView.Adapter<ScreenTimesAdapter.ViewHolder>() {


    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ScreenTimeRowBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.screen_time_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myItem = timeList[position]
        auth = Firebase.auth
        db = Firebase.firestore
        holder.binding.dateText.text = myItem.dateString
        holder.binding.timeText.text = myItem.time.toString() + "dk"

        holder.binding.myCard.setOnClickListener {
            val deleteDutyDialog = AlertDialog.Builder(holder.itemView.context)
            deleteDutyDialog.setTitle("Ekran Süresi Sil")
            deleteDutyDialog.setMessage("Ekran Süresini Silmek İstediğinizden Emin misiniz?")

            deleteDutyDialog.setPositiveButton("Sil") { _, _ ->


                db.collection("School").document("763455").collection("Student")
                    .document(myItem.studentID).collection("ScreenTimes")
                    .document(myItem.dateString).delete().addOnSuccessListener {
                        Toast.makeText(
                            holder.itemView.context, "İşlem Başarılı!", Toast.LENGTH_SHORT
                        ).show()
                    }


            }
            deleteDutyDialog.setNegativeButton("İptal") { _, _ ->

            }

            deleteDutyDialog.show()
        }
    }

}