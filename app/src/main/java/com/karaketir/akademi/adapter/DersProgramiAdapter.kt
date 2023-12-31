package com.karaketir.akademi.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karaketir.akademi.EditProgramActivity
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.ProgramRowBinding
import com.karaketir.akademi.models.Ders
import java.util.Calendar

class DersProgramiAdapter(
    private val dersList: ArrayList<Ders>,
    private var secilenZaman: Int,
    private val personType: String
) : RecyclerView.Adapter<DersProgramiAdapter.DersHolder>() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val kurumKodu = 763455

    class DersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ProgramRowBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DersHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.program_row, parent, false)
        return DersHolder(view)
    }

    override fun getItemCount(): Int {
        return dersList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DersHolder, position: Int) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        with(holder) {

            val myItem = dersList[position]
            val dersAdiText = binding.dersAdiProgram
            val dersTuruText = binding.dersTuruProgram
            val dersSureText = binding.derSureProgram
            val dersNumaraText = binding.dersNumaraProgram
            val dersSoruText = binding.derSoruProgram

            val dersNumber = (myItem.dersNumara.toInt() + 1).toString()

            dersAdiText.text = myItem.dersAdi
            dersTuruText.text = myItem.dersTuru
            dersSureText.text = myItem.dersSure.toString() + "dk"
            dersSoruText.text = myItem.dersSoru.toString() + " Soru"
            dersNumaraText.text = "$dersNumber. Ders"


            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0

            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)

            var baslangicTarihi = cal.time
            var bitisTarihi = cal.time

            when (secilenZaman) {
                0 -> {
                    when (myItem.gun) {
                        "Pazartesi" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time

                        }

                        "Salı" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Çarşamba" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 2)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Perşembe" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 3)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Cuma" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 4)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Cumartesi" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 5)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Pazar" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, 6)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }
                    }

                }

                1 -> {
                    when (myItem.gun) {
                        "Pazartesi" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time

                        }

                        "Salı" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Çarşamba" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 2)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Perşembe" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 3)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Cuma" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 4)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Cumartesi" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 5)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }

                        "Pazar" -> {
                            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                            cal.add(Calendar.DAY_OF_YEAR, -7)
                            cal.add(Calendar.DAY_OF_YEAR, 6)
                            baslangicTarihi = cal.time

                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            bitisTarihi = cal.time
                        }
                    }

                }
            }


            var toplamCalisma = 0

            if (myItem.dersTuru != "TYT ve AYT") {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(myItem.studentOwnerID).collection("Studies")
                    .whereEqualTo("dersAdi", myItem.dersAdi).whereEqualTo("tür", myItem.dersTuru)
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, _ ->

                        if (value != null) {

                            for (document in value) {

                                toplamCalisma += document.get("toplamCalisma").toString().toInt()

                            }

                        }

                        if (toplamCalisma < myItem.dersSure) {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }

                    }

                var toplamSoru = 0

                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(myItem.studentOwnerID).collection("Studies")
                    .whereEqualTo("dersAdi", myItem.dersAdi).whereEqualTo("tür", myItem.dersTuru)
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, _ ->

                        if (value != null) {

                            for (document in value) {

                                toplamSoru += document.get("çözülenSoru").toString().toInt()

                            }

                        }

                        if (toplamSoru < myItem.dersSoru) {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }

                    }
            } else {
                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(myItem.studentOwnerID).collection("Studies")
                    .whereEqualTo("dersAdi", myItem.dersAdi)
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, _ ->

                        if (value != null) {

                            for (document in value) {

                                toplamCalisma += document.get("toplamCalisma").toString().toInt()

                            }

                        }

                        if (toplamCalisma < myItem.dersSure) {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSureImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }

                    }

                var toplamSoru = 0

                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(myItem.studentOwnerID).collection("Studies")
                    .whereEqualTo("dersAdi", myItem.dersAdi)
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, _ ->

                        if (value != null) {

                            for (document in value) {

                                toplamSoru += document.get("çözülenSoru").toString().toInt()

                            }

                        }

                        if (toplamSoru < myItem.dersSoru) {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_error_outline_24)
                        } else {
                            binding.haftaSoruImage.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        }

                    }
            }


            if (personType == "Teacher") {

                binding.myCard.setOnClickListener {

                    val newIntent = Intent(holder.itemView.context, EditProgramActivity::class.java)
                    newIntent.putExtra("studentID", myItem.studentOwnerID)
                    newIntent.putExtra("gun", myItem.gun)
                    newIntent.putExtra("count", myItem.dersNumara)
                    holder.itemView.context.startActivity(newIntent)

                }

            }


        }
    }
}