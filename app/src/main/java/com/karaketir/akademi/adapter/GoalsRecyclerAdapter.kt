package com.karaketir.akademi.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.GoalGridRowBinding
import com.karaketir.akademi.models.Goal
import java.util.*
import kotlin.collections.ArrayList

class GoalsRecyclerAdapter(
    private val goalList: ArrayList<Goal>,
    private val kurumKodu: Int,
    private val personType: String
) : RecyclerView.Adapter<GoalsRecyclerAdapter.GoalHolder>() {


    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    class GoalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = GoalGridRowBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.goal_grid_row, parent, false)
        return GoalHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GoalHolder, position: Int) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        with(holder) {

            if (goalList.isNotEmpty() && position >= 0 && position < goalList.size) {

                val myItem = goalList[position]

                binding.goalDersAdi.text = myItem.dersAdi
                binding.hedefToplamCalisma.text =
                    "Hedef Toplam Çalışma: " + myItem.toplamCalisma.toString() + "dk"
                binding.hedefSoru.text = "Hedef Toplam Soru: " + myItem.cozulenSoru.toString()
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !

                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)

                cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                val bitisTarihi = cal.time


                cal.add(Calendar.DAY_OF_YEAR, -7)
                val baslangicTarihi = cal.time
                var toplamCalisma = 0
                var cozulenSoru = 0


                if (personType == "Student") {
                    binding.deleteGoalButton.visibility = View.GONE
                } else {
                    binding.deleteGoalButton.visibility = View.VISIBLE

                    binding.deleteGoalButton.setOnClickListener {

                        val deleteDutyDialog = AlertDialog.Builder(holder.itemView.context)
                        deleteDutyDialog.setTitle("Görev Sil")
                        deleteDutyDialog.setMessage("Bu Görevi Silmek İstediğinizden Emin misiniz?")

                        deleteDutyDialog.setPositiveButton("Sil") { _, _ ->
                            db.collection("School").document(kurumKodu.toString())
                                .collection("Student").document(myItem.studentOwnerID)
                                .collection("HaftalikHedefler").document(myItem.goalID).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "İşlem Başarılı!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                        deleteDutyDialog.setNegativeButton("İptal") { _, _ ->

                        }

                        deleteDutyDialog.show()

                    }
                }



                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(myItem.studentOwnerID).collection("Studies")
                    .whereEqualTo("dersAdi", myItem.dersAdi)
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, _ ->

                        if (value != null) {

                            for (document in value) {

                                toplamCalisma += document.get("toplamCalisma").toString().toInt()
                                cozulenSoru += document.get("çözülenSoru").toString().toInt()

                            }

                        }
                        println(toplamCalisma.toString())
                        binding.haftaToplamCalisma.text =
                            "Geçen Hafta Toplam Çalışma: $toplamCalisma" + "dk"
                        if (toplamCalisma < myItem.toplamCalisma) {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }

                        if (cozulenSoru < myItem.cozulenSoru) {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }
                        binding.haftaSoru.text = "Geçen Hafta Toplam Soru: $cozulenSoru"

                    }


            }

        }

    }

    override fun getItemCount(): Int {
        return goalList.size
    }
}