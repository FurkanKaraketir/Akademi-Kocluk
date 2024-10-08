package com.karaketir.akademi.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karaketir.akademi.DenemeTeacherEditActivity
import com.karaketir.akademi.R
import com.karaketir.akademi.TestResultsShortActivity
import com.karaketir.akademi.databinding.DenemelerTeacherRowBinding
import com.karaketir.akademi.models.DenemeTeacher

class DenemelerTeacherRecyclerAdapter(
    private var denemeList: ArrayList<DenemeTeacher>, private var kurumKodu: Int
) : RecyclerView.Adapter<DenemelerTeacherRecyclerAdapter.DenemeHolder>() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    class DenemeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DenemelerTeacherRowBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DenemeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.denemeler_teacher_row, parent, false)
        return DenemeHolder(view)
    }

    override fun onBindViewHolder(holder: DenemeHolder, position: Int) {
        with(holder) {

            if (denemeList.isNotEmpty() && position >= 0 && position < denemeList.size) {

                val myItem = denemeList[position]

                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()


                binding.denemeAdiTeacherTextView.text = myItem.denemeAdi
                binding.deleteDenemeTeacherButton.setOnClickListener {


                    val deleteAlertDialog = AlertDialog.Builder(holder.itemView.context)
                    deleteAlertDialog.setTitle("Deneme Sil")
                    deleteAlertDialog.setMessage("Bu Denemeyi Silmek İstediğinize Emin misiniz?")
                    deleteAlertDialog.setPositiveButton("Sil") { _, _ ->

                        db.collection("School").document(kurumKodu.toString()).collection("Teacher")
                            .document(auth.uid.toString()).collection("Denemeler")
                            .document(myItem.denemeID).delete().addOnSuccessListener {
                                Toast.makeText(
                                    holder.itemView.context, "İşlem Başarılı!", Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    deleteAlertDialog.setNegativeButton("İptal") { _, _ ->

                    }
                    deleteAlertDialog.show()


                }
                binding.fullDenemeCard.setOnClickListener {
                    val intent =
                        Intent(holder.itemView.context, TestResultsShortActivity::class.java)
                    intent.putExtra("denemeAdi", myItem.denemeAdi)
                    intent.putExtra("kurumKodu", kurumKodu.toString())
                    holder.itemView.context.startActivity(intent)
                }

                binding.denemeEditButton.setOnClickListener {
                    val intent =
                        Intent(holder.itemView.context, DenemeTeacherEditActivity::class.java)
                    intent.putExtra("denemeID", myItem.denemeID)
                    intent.putExtra("kurumKodu", kurumKodu.toString())
                    intent.putExtra("denemeAdi", myItem.denemeAdi)
                    holder.itemView.context.startActivity(intent)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return denemeList.size
    }
}