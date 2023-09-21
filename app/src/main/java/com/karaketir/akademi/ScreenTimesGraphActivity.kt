package com.karaketir.akademi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karaketir.akademi.databinding.ActivityScreenTimesGraphBinding
import com.karaketir.akademi.models.ScreenTime

class ScreenTimesGraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenTimesGraphBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var timesList = ArrayList<ScreenTime>()
    private var studentID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenTimesGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentID = intent.getStringExtra("studentID").toString()

        auth = Firebase.auth
        db = Firebase.firestore

        val netHash = HashMap<String, Int>()

        db.collection("School").document("763455").collection("Student").document(studentID)
            .collection("ScreenTimes").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                timesList.clear()

                if (value != null) {
                    for (i in value) {

                        val newTimeStamp = i.id
                        val newTime = i.get("time").toString().toInt()

                        val newScreenTime = ScreenTime(newTimeStamp, newTime, studentID)
                        timesList.add(newScreenTime)

                    }
                    for (a in timesList) {
                        netHash[a.dateString] = a.time
                    }
                    val data: MutableList<DataEntry> = ArrayList()
                    val cartesian: Cartesian = AnyChart.column()
                    val anyChartView = binding.anyChartScreenTimes
                    val sortedDateMap = timesList.toList().sortedBy { it.dateString }

                    for (j in sortedDateMap) {
                        data.add(ValueDataEntry(j.dateString, netHash[j.dateString]))
                    }

                    val column: Column = cartesian.column(data)

                    column.tooltip().titleFormat("{%X}").position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM).offsetX(0.0).offsetY(5.0)
                        .format("{%Value}{groupsSeparator:.}dk")

                    cartesian.animation(true)
                    val title = "Ekran Süreleri"
                    cartesian.title(title)

                    cartesian.yScale().minimum(0.0)

                    cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator:.}dk")

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
                    cartesian.interactivity().hoverMode(HoverMode.BY_X)

                    cartesian.xAxis(0).title("Tarihler")
                    cartesian.yAxis(0).title("Ekran Süresi (dk)")

                    anyChartView.setChart(cartesian)


                }
            }


    }
}