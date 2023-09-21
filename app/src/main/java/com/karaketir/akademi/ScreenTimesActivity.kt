package com.karaketir.akademi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.adapter.ScreenTimesAdapter
import com.karaketir.akademi.databinding.ActivityScreenTimesBinding
import com.karaketir.akademi.models.ScreenTime

class ScreenTimesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenTimesBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerViewScreenTimesAdapter: ScreenTimesAdapter
    private var timeList = ArrayList<ScreenTime>()
    private var studentID = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenTimesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)

        val recyclerView = binding.screenTimeRecycler
        recyclerViewScreenTimesAdapter = ScreenTimesAdapter(timeList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewScreenTimesAdapter

        studentID = intent.getStringExtra("studentID").toString()

        auth = Firebase.auth
        db = Firebase.firestore

        if (auth.uid.toString() == studentID) {
            binding.addScreenTimeButton.visibility = View.GONE
        } else {
            binding.addScreenTimeButton.visibility = View.VISIBLE
        }

        db.collection("School").document("763455").collection("Student").document(studentID)
            .collection("ScreenTimes").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                timeList.clear()

                if (value != null) {
                    for (i in value) {

                        val newTimeStamp = i.id
                        val newTime = i.get("time").toString().toInt()

                        val newScreenTime = ScreenTime(newTimeStamp, newTime, studentID)
                        timeList.add(newScreenTime)

                    }
                    recyclerViewScreenTimesAdapter.notifyDataSetChanged()
                }
            }


        binding.addScreenTimeButton.setOnClickListener {

            val newIntent = Intent(this, EnterScreenTimeActivity::class.java)
            newIntent.putExtra("studentID", studentID)
            this.startActivity(newIntent)

        }
        binding.graphScreenTimeButton.setOnClickListener {

            val newIntent = Intent(this, ScreenTimesGraphActivity::class.java)
            newIntent.putExtra("studentID", studentID)
            this.startActivity(newIntent)

        }


    }


}