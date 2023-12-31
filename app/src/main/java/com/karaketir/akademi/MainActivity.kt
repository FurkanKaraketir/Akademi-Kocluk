package com.karaketir.akademi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.karaketir.akademi.adapter.StudentsRecyclerAdapter
import com.karaketir.akademi.adapter.StudiesRecyclerAdapter
import com.karaketir.akademi.databinding.ActivityMainBinding
import com.karaketir.akademi.models.Student
import com.karaketir.akademi.models.Study
import com.karaketir.akademi.models.UserViewModel
import com.karaketir.akademi.services.*
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.*


class MainActivity : AppCompatActivity() {

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

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerViewPreviousStudies: RecyclerView
    private lateinit var recyclerViewMyStudents: RecyclerView
    private lateinit var db: FirebaseFirestore
    private var raporGondermeyenList = ArrayList<Student>()
    private lateinit var recyclerViewPreviousStudiesAdapter: StudiesRecyclerAdapter
    private lateinit var recyclerViewMyStudentsRecyclerAdapter: StudentsRecyclerAdapter
    private var kurumKodu = 763455
    private val handler = Handler(Looper.getMainLooper())
    private val workbook = XSSFWorkbook()
    private var studyList = ArrayList<Study>()
    private var secilenGrade = "Bütün Sınıflar"
    private var secilenZaman = "Bugün"
    private var gradeList = arrayOf("Bütün Sınıflar", "12", "11", "10", "9", "0")
    private val zamanAraliklari =
        arrayOf("Bugün", "Dün", "Bu Hafta", "Geçen Hafta", "Bu Ay", "Geçen Ay", "Tüm Zamanlar")
    private lateinit var filteredList: ArrayList<Student>
    private lateinit var filteredStudyList: ArrayList<Study>
    private var studentList = ArrayList<Student>()


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(auth.uid.toString())
        }
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val developerButton = binding.developerButtonMain

        Purchases.logLevel = LogLevel.DEBUG


        db.collection("AdminData").document("subscriptionKey").get()
            .addOnSuccessListener { document ->

                Purchases.configure(

                    PurchasesConfiguration.Builder(this, document.get("key").toString())
                        .appUserID(auth.uid.toString()).build()

                )
                Purchases.sharedInstance.updatedCustomerInfoListener =
                    UpdatedCustomerInfoListener { customer ->

                        UserViewModel.shared.customerInfo.value = customer

                    }
                UserViewModel.shared.customerInfo.value?.activeSubscriptions?.let { values ->

                    db.collection("User").document(auth.uid.toString()).get()
                        .addOnSuccessListener { userInfo ->

                            if (values.isEmpty() && userInfo.get("personType")
                                    .toString() == "Student"
                            ) {
                                val newIntent = Intent(this, PaywallActivity::class.java)
                                this.startActivity(newIntent)
                                finish()
                            }
                        }


                }

            }


        val nameAndSurnameTextView = binding.nameAndSurnameTextView
        val transitionsContainer = binding.transitionsContainer
        val sayacContainer = binding.sayacContainer

        var sayacVisible = false

        val contentTextView = binding.contentTextView
        val topStudentsButton = binding.topStudentsButton
        val addStudyButton = binding.addStudyButton
        val signOutButton = binding.signOutButton
        val gorevButton = binding.gorevButton
        val allStudentsBtn = binding.allStudentsBtn
        val searchEditText = binding.searchStudentMainActivityEditText
        val studySearchEditText = binding.searchStudyEditText
        val studentDenemeButton = binding.studentDenemeButton
        val teacherDenemeButton = binding.teacherDenemeButton
        val istatistikButton = binding.koclukStatsBtn
        val hedeflerStudentButton = binding.hedefStudentButton
        val gradeSpinner = binding.gradeSpinner
        val gradeSpinnerLayout = binding.gradeSpinnerLayout
        val kocOgretmenTextView = binding.kocOgretmenTextView
        val teacherSpinnerLayout = binding.teacherSpinnerLayout
        val teacherSpinner = binding.studyZamanAraligiSpinner
        val searchBarTeacher = binding.searchBarTeacher
        val updateLayout = binding.updateLayout
        val updateButton = binding.updateButton
        val imageHalit = binding.imageHalit
        val excelButton = binding.excelButton
        val dersProgramiButton = binding.dersProgramiButton
        val noReportButton = binding.noReportButton
        val subjectsButton = binding.konuButton
        val screenTimeButton = binding.screenTimeButton

        screenTimeButton.setOnClickListener {
            val newIntent = Intent(this, ScreenTimesActivity::class.java)
            newIntent.putExtra("studentID", auth.uid.toString())
            this.startActivity(newIntent)
        }

        subjectsButton.setOnClickListener {
            val newIntent = Intent(this, SubjectCompActivity::class.java)
            newIntent.putExtra("studentID", auth.uid.toString())
            this.startActivity(newIntent)
        }

        developerButton.setOnClickListener {
            openLink(
                "https://www.linkedin.com/in/furkankaraketir/", this
            )
        }

        db.collection("UserPhotos").document(auth.uid.toString()).get().addOnSuccessListener {
            imageHalit.glide(it.get("photoURL").toString(), placeHolderYap(applicationContext))

        }

        updateButton.setOnClickListener {
            openLink(
                "https://play.google.com/store/apps/details?id=com.karaketir.akademi", this
            )
        }


        db.collection("AdminData").document("versionCode").get().addOnSuccessListener {
            val myVersion = BuildConfig.VERSION_CODE
            val latestVersion = it.get("key").toString().toInt()
            if (myVersion < latestVersion) {
                addStudyButton.visibility = View.GONE
                signOutButton.visibility = View.GONE
                updateLayout.visibility = View.VISIBLE

            } else {
                addStudyButton.visibility = View.VISIBLE
                signOutButton.visibility = View.VISIBLE
                updateLayout.visibility = View.GONE

            }
        }

        var grade = 0

        nameAndSurnameTextView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        recyclerViewPreviousStudies = binding.previousStudies
        recyclerViewMyStudents = binding.myStudents

        hedeflerStudentButton.setOnClickListener {
            val intent2 = Intent(this, GoalsActivity::class.java)
            intent2.putExtra("studentID", auth.currentUser?.uid.toString())
            this.startActivity(intent2)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredList = ArrayList()
                if (p0.toString() != "") {
                    for (item in studentList) {
                        if (item.studentName.lowercase(Locale.getDefault())
                                .contains(p0.toString().lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(item)
                        }
                    }
                    setupStudentRecyclerView(filteredList)
                } else {
                    setupStudentRecyclerView(studentList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        studySearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredStudyList = ArrayList()
                if (p0.toString() != "") {
                    for (item in studyList) {
                        if (item.studyName.lowercase(Locale.getDefault())
                                .contains(p0.toString().lowercase(Locale.getDefault()))
                        ) {
                            filteredStudyList.add(item)
                        }
                    }
                    setupStudyRecyclerView(filteredStudyList)
                } else {
                    setupStudyRecyclerView(studyList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        dersProgramiButton.setOnClickListener {
            val newIntent = Intent(this, ProgramActivity::class.java)
            this.startActivity(newIntent)
        }


        var visible = false
        try {

            db.collection("User").document(auth.uid.toString()).get().addOnSuccessListener {
                nameAndSurnameTextView.text = "Merhaba: " + it.get("nameAndSurname").toString()


                TransitionManager.beginDelayedTransition(transitionsContainer)
                visible = !visible
                nameAndSurnameTextView.visibility = if (visible) View.VISIBLE else View.GONE


                if (it.get("personType").toString() == "Student") {

                    var cal = Calendar.getInstance()
                    grade = it.get("grade").toString().toInt()
                    addStudyButton.visibility = View.VISIBLE
                    kocOgretmenTextView.visibility = View.VISIBLE
                    excelButton.visibility = View.GONE
                    binding.developerButtonMain.visibility = View.VISIBLE

                    db.collection("School").document(kurumKodu.toString()).collection("Student")
                        .document(auth.uid.toString()).get().addOnSuccessListener { student ->
                            val kocID = student.get("teacher").toString()
                            if (kocID.isEmpty()) {
                                kocOgretmenTextView.text = "Koç Öğretmenin Bulunmuyor"
                            } else {
                                db.collection("School").document(kurumKodu.toString())
                                    .collection("Teacher").document(kocID).get()
                                    .addOnSuccessListener { teacher ->
                                        kocOgretmenTextView.text =
                                            "Koç Öğretmenin: " + teacher.get("nameAndSurname")
                                                .toString()
                                    }

                            }
                        }
                    dersProgramiButton.visibility = View.VISIBLE
                    searchBarTeacher.visibility = View.GONE
                    gradeSpinnerLayout.visibility = View.GONE
                    topStudentsButton.visibility = View.GONE
                    teacherSpinnerLayout.visibility = View.GONE
                    subjectsButton.visibility = View.VISIBLE
                    studySearchEditText.visibility = View.VISIBLE
                    searchEditText.visibility = View.GONE
                    screenTimeButton.visibility = View.VISIBLE
                    studentDenemeButton.visibility = View.VISIBLE
                    teacherDenemeButton.visibility = View.GONE
                    hedeflerStudentButton.visibility = View.VISIBLE
                    recyclerViewPreviousStudies.visibility = View.VISIBLE
                    istatistikButton.visibility = View.GONE
                    allStudentsBtn.visibility = View.GONE
                    noReportButton.visibility = View.GONE
                    gorevButton.visibility = View.VISIBLE

                    contentTextView.text = "Son 7 Gün İçindeki\nÇalışmalarım"

                    cal.add(Calendar.DAY_OF_YEAR, 2)
                    val bitisTarihi = cal.time

                    cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -7)
                    val baslangicTarihi = cal.time



                    db.collection("School").document(kurumKodu.toString()).collection("Student")
                        .document(auth.uid.toString()).collection("Studies")
                        .whereGreaterThan("timestamp", baslangicTarihi)
                        .whereLessThan("timestamp", bitisTarihi)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { it1, _ ->
                            studyList.clear()
                            val documents = it1!!.documents
                            for (document in documents) {
                                val subjectTheme = document.get("konuAdi").toString()
                                val subjectCount = document.get("toplamCalisma").toString()
                                val studyDersAdi = document.get("dersAdi").toString()
                                val studyTur = document.get("tür").toString()
                                val soruSayisi = document.get("çözülenSoru").toString()
                                val timestamp = document.get("timestamp") as Timestamp
                                val currentStudy = Study(
                                    subjectTheme,
                                    subjectCount,
                                    auth.uid.toString(),
                                    studyDersAdi,
                                    studyTur,
                                    soruSayisi,
                                    timestamp,
                                    document.id
                                )

                                studyList.add(currentStudy)

                            }

                            setupStudyRecyclerView(studyList)

                            recyclerViewPreviousStudiesAdapter.notifyDataSetChanged()

                        }


                } else if (it.get("personType").toString() == "Teacher") {
                    screenTimeButton.visibility = View.GONE
                    studySearchEditText.visibility = View.GONE
                    dersProgramiButton.visibility = View.GONE
                    hedeflerStudentButton.visibility = View.GONE
                    excelButton.visibility = View.VISIBLE
                    searchEditText.visibility = View.VISIBLE
                    topStudentsButton.visibility = View.VISIBLE
                    teacherSpinnerLayout.visibility = View.VISIBLE
                    kocOgretmenTextView.visibility = View.GONE
                    recyclerViewMyStudents.visibility = View.VISIBLE
                    searchBarTeacher.visibility = View.VISIBLE
                    studentDenemeButton.visibility = View.GONE
                    teacherDenemeButton.visibility = View.VISIBLE
                    allStudentsBtn.visibility = View.VISIBLE
                    addStudyButton.visibility = View.GONE
                    noReportButton.visibility = View.VISIBLE
                    istatistikButton.visibility = View.VISIBLE
                    subjectsButton.visibility = View.GONE
                    gorevButton.visibility = View.GONE
                    gradeSpinnerLayout.visibility = View.VISIBLE
                    contentTextView.text = "Öğrencilerim"
                    binding.developerButtonMain.visibility = View.VISIBLE

                    val sheet: Sheet = workbook.createSheet("Sayfa 1")

                    val cellStyle = getHeaderStyle(workbook)

                    createSheetHeader(cellStyle, sheet)

                    excelButton.setOnClickListener {
                        addData(
                            sheet, secilenZaman, secilenGrade, auth, db, this, workbook
                        )

                        askForPermissions()
                    }


                    val gradeAdapter = ArrayAdapter(
                        this@MainActivity, android.R.layout.simple_spinner_item, gradeList
                    )
                    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    gradeSpinner.adapter = gradeAdapter
                    gradeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            secilenGrade = gradeList[p2]
                            if (secilenGrade == "Bütün Sınıflar") {
                                db.collection("School").document(kurumKodu.toString())
                                    .collection("Student")
                                    .whereEqualTo("teacher", auth.uid.toString())
                                    .addSnapshotListener { documents, _ ->

                                        studentList.clear()
                                        if (documents != null) {
                                            for (document in documents) {
                                                val studentGrade =
                                                    document.get("grade").toString().toInt()
                                                val studentName =
                                                    document.get("nameAndSurname").toString()
                                                val teacher = document.get("teacher").toString()
                                                val id = document.get("id").toString()
                                                val currentStudent =
                                                    Student(studentName, teacher, id, studentGrade)
                                                studentList.add(currentStudent)

                                            }
                                            binding.studentCountTextView.text =
                                                "Öğrenci Sayısı: " + studentList.size
                                        }
                                        studentList.sortBy { a ->
                                            a.studentName
                                        }
                                        setupStudentRecyclerView(studentList)

                                        recyclerViewMyStudentsRecyclerAdapter.notifyDataSetChanged()

                                    }
                            } else {
                                db.collection("School").document(kurumKodu.toString())
                                    .collection("Student")
                                    .whereEqualTo("teacher", auth.uid.toString())
                                    .whereEqualTo("grade", secilenGrade.toInt())
                                    .addSnapshotListener { documents, _ ->

                                        studentList.clear()
                                        if (documents != null) {
                                            for (document in documents) {
                                                val studentGrade =
                                                    document.get("grade").toString().toInt()
                                                val studentName =
                                                    document.get("nameAndSurname").toString()
                                                val teacher = document.get("teacher").toString()
                                                val id = document.get("id").toString()
                                                val currentStudent =
                                                    Student(studentName, teacher, id, studentGrade)
                                                studentList.add(currentStudent)

                                            }
                                            binding.studentCountTextView.text =
                                                "Öğrenci Sayısı: " + studentList.size
                                        }
                                        studentList.sortBy { a ->
                                            a.studentName
                                        }
                                        setupStudentRecyclerView(studentList)

                                        recyclerViewMyStudentsRecyclerAdapter.notifyDataSetChanged()

                                    }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }


                    val tarihAdapter = ArrayAdapter(
                        this@MainActivity, android.R.layout.simple_spinner_item, zamanAraliklari
                    )
                    tarihAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    teacherSpinner.adapter = tarihAdapter
                    teacherSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            secilenZaman = zamanAraliklari[p2]
                            setupStudentRecyclerView(studentList)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }

                    }


                }



                TransitionManager.beginDelayedTransition(sayacContainer)
                sayacVisible = !sayacVisible
                contentTextView.visibility = if (sayacVisible) View.VISIBLE else View.GONE

            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }

        studentDenemeButton.setOnClickListener {
            val intent = Intent(this, DenemelerActivity::class.java)
            intent.putExtra("studentID", auth.uid.toString())
            this.startActivity(intent)
        }
        teacherDenemeButton.setOnClickListener {
            val intent = Intent(this, DenemelerTeacherActivity::class.java)
            this.startActivity(intent)
        }


        istatistikButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            this.startActivity(intent)
        }

        allStudentsBtn.setOnClickListener {
            val intent = Intent(this, AllStudentsActivity::class.java)
            this.startActivity(intent)
        }

        addStudyButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            this.startActivity(intent)
        }

        topStudentsButton.setOnClickListener {
            val intent = Intent(this, TopStudentsActivity::class.java)
            this.startActivity(intent)
        }

        signOutButton.setOnClickListener {

            val signOutAlertDialog = AlertDialog.Builder(this)
            signOutAlertDialog.setTitle("Çıkış Yap")
            signOutAlertDialog.setMessage("Hesabınızdan Çıkış Yapmak İstediğinize Emin misiniz?")
            signOutAlertDialog.setPositiveButton("Çıkış") { _, _ ->
                signOut()
                finish()
            }
            signOutAlertDialog.setNegativeButton("İptal") { _, _ ->

            }
            signOutAlertDialog.show()

        }

        gorevButton.setOnClickListener {
            val intent = Intent(this, DutiesActivity::class.java)
            intent.putExtra("studentID", auth.uid)
            this.startActivity(intent)
        }

        noReportButton.setOnClickListener {
            val myIntent = Intent(this, NoReportActivity::class.java)

            var cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !

            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)

            var baslangicTarihi = cal.time
            var bitisTarihi = cal.time


            when (secilenZaman) {

                "Bugün" -> {
                    baslangicTarihi = cal.time


                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    bitisTarihi = cal.time
                }

                "Dün" -> {
                    bitisTarihi = cal.time

                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    baslangicTarihi = cal.time

                }

                "Bu Hafta" -> {
                    cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                    baslangicTarihi = cal.time


                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                    bitisTarihi = cal.time

                }

                "Geçen Hafta" -> {
                    cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                    bitisTarihi = cal.time


                    cal.add(Calendar.DAY_OF_YEAR, -7)
                    baslangicTarihi = cal.time


                }

                "Bu Ay" -> {

                    cal = Calendar.getInstance()
                    cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !

                    cal.clear(Calendar.MINUTE)
                    cal.clear(Calendar.SECOND)
                    cal.clear(Calendar.MILLISECOND)

                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    baslangicTarihi = cal.time


                    cal.add(Calendar.MONTH, 1)
                    bitisTarihi = cal.time


                }

                "Geçen Ay" -> {
                    cal = Calendar.getInstance()
                    cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !

                    cal.clear(Calendar.MINUTE)
                    cal.clear(Calendar.SECOND)
                    cal.clear(Calendar.MILLISECOND)

                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    bitisTarihi = cal.time


                    cal.add(Calendar.MONTH, -1)
                    baslangicTarihi = cal.time

                }

                "Tüm Zamanlar" -> {
                    cal.set(1970, Calendar.JANUARY, Calendar.DAY_OF_WEEK)
                    baslangicTarihi = cal.time


                    cal.set(2077, Calendar.JANUARY, Calendar.DAY_OF_WEEK)
                    bitisTarihi = cal.time

                }
            }
            raporGondermeyenList.clear()

            var my = 0
            for (i in studentList) {

                db.collection("School").document(kurumKodu.toString()).collection("Student")
                    .document(i.id).collection("Studies")
                    .whereGreaterThan("timestamp", baslangicTarihi)
                    .whereLessThan("timestamp", bitisTarihi).addSnapshotListener { value, error ->
                        if (error != null) {
                            println(error.localizedMessage)
                        }

                        if (value != null) {

                            if (value.isEmpty) {
                                raporGondermeyenList.add(i)
                            }

                        } else {
                            raporGondermeyenList.add(i)
                        }

                        my += 1
                        if (my == studentList.size) {
                            println(raporGondermeyenList.size)
                            for (a in raporGondermeyenList) {
                                println(a.studentName)
                            }
                            myIntent.putExtra("list", raporGondermeyenList)
                            myIntent.putExtra("secilenZaman", secilenZaman)
                            this@MainActivity.startActivity(myIntent)
                        }

                    }
            }


        }

        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                updateTime(grade)
            }
        })


    }

    private fun setupStudentRecyclerView(list: ArrayList<Student>) {
        val layoutManager = LinearLayoutManager(applicationContext)

        recyclerViewMyStudents.layoutManager = layoutManager

        recyclerViewMyStudentsRecyclerAdapter = StudentsRecyclerAdapter(list, secilenZaman)

        recyclerViewMyStudents.adapter = recyclerViewMyStudentsRecyclerAdapter

    }

    private fun setupStudyRecyclerView(list: ArrayList<Study>) {
        val layoutManager = GridLayoutManager(applicationContext, 2)

        recyclerViewPreviousStudies.layoutManager = layoutManager
        recyclerViewPreviousStudiesAdapter = StudiesRecyclerAdapter(list, "Bu Hafta")
        recyclerViewPreviousStudies.adapter = recyclerViewPreviousStudiesAdapter

    }

    private fun signOut() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(auth.uid.toString())
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
        finish()
    }


    @SuppressLint("SetTextI18n")
    fun updateTime(grade: Int) {

        val currentDate = Calendar.getInstance()

        val eventDate = Calendar.getInstance()
        when (grade) {
            12, 0 -> {

                db.collection("AdminData").document("date12").get().addOnSuccessListener {
                    eventDate[Calendar.YEAR] = it.get("year").toString().toInt()
                    eventDate[Calendar.MONTH] = it.get("month").toString().toInt() - 1
                    eventDate[Calendar.DAY_OF_MONTH] = it.get("day").toString().toInt()

                    eventDate[Calendar.HOUR_OF_DAY] = 10
                    eventDate[Calendar.MINUTE] = 15
                    eventDate[Calendar.SECOND] = 0

                    val diff = eventDate.timeInMillis - currentDate.timeInMillis

                    val days = diff / (24 * 60 * 60 * 1000)
                    val hours = diff / (1000 * 60 * 60) % 24
                    val minutes = diff / (1000 * 60) % 60
                    val seconds = (diff / 1000) % 60

                    binding.YKSsayac.text =
                        "YKS'ye Son:\n $days Gün $hours Saat $minutes Dk $seconds Sn"

                }

            }

            11 -> {
                db.collection("AdminData").document("date11").get().addOnSuccessListener {

                    eventDate[Calendar.YEAR] = it.get("year").toString().toInt()
                    eventDate[Calendar.MONTH] = it.get("month").toString().toInt() - 1
                    eventDate[Calendar.DAY_OF_MONTH] = it.get("day").toString().toInt()

                    eventDate[Calendar.HOUR_OF_DAY] = 10
                    eventDate[Calendar.MINUTE] = 15
                    eventDate[Calendar.SECOND] = 0

                    val diff = eventDate.timeInMillis - currentDate.timeInMillis

                    val days = diff / (24 * 60 * 60 * 1000)
                    val hours = diff / (1000 * 60 * 60) % 24
                    val minutes = diff / (1000 * 60) % 60
                    val seconds = (diff / 1000) % 60

                    binding.YKSsayac.text =
                        "YKS'ye Son:\n $days Gün $hours Saat $minutes Dk $seconds Sn"


                }
            }

            10 -> {
                db.collection("AdminData").document("date10").get().addOnSuccessListener {

                    eventDate[Calendar.YEAR] = it.get("year").toString().toInt()
                    eventDate[Calendar.MONTH] = it.get("month").toString().toInt() - 1
                    eventDate[Calendar.DAY_OF_MONTH] = it.get("day").toString().toInt()

                    eventDate[Calendar.HOUR_OF_DAY] = 10
                    eventDate[Calendar.MINUTE] = 15
                    eventDate[Calendar.SECOND] = 0

                    val diff = eventDate.timeInMillis - currentDate.timeInMillis

                    val days = diff / (24 * 60 * 60 * 1000)
                    val hours = diff / (1000 * 60 * 60) % 24
                    val minutes = diff / (1000 * 60) % 60
                    val seconds = (diff / 1000) % 60

                    binding.YKSsayac.text =
                        "YKS'ye Son:\n $days Gün $hours Saat $minutes Dk $seconds Sn"


                }
            }

            9 -> {
                db.collection("AdminData").document("date9").get().addOnSuccessListener {

                    eventDate[Calendar.YEAR] = it.get("year").toString().toInt()
                    eventDate[Calendar.MONTH] = it.get("month").toString().toInt() - 1
                    eventDate[Calendar.DAY_OF_MONTH] = it.get("day").toString().toInt()

                    eventDate[Calendar.HOUR_OF_DAY] = 10
                    eventDate[Calendar.MINUTE] = 15
                    eventDate[Calendar.SECOND] = 0

                    val diff = eventDate.timeInMillis - currentDate.timeInMillis

                    val days = diff / (24 * 60 * 60 * 1000)
                    val hours = diff / (1000 * 60 * 60) % 24
                    val minutes = diff / (1000 * 60) % 60
                    val seconds = (diff / 1000) % 60

                    binding.YKSsayac.text =
                        "YKS'ye Son:\n $days Gün $hours Saat $minutes Dk $seconds Sn"


                }
            }
        }


    }

    override fun onResume() {

        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        super.onResume()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                createExcel(this, secilenGrade, secilenZaman, workbook)
            }
        }

    private fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            createExcel(this, secilenGrade, secilenZaman, workbook)
        }
    }
}