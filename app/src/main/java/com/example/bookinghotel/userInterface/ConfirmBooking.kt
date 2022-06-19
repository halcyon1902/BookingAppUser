package com.example.bookinghotel.userInterface

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookinghotel.R
import com.example.bookinghotel.mainscreen.MainScreenUser
import com.example.bookinghotel.model.Booking
import com.example.bookinghotel.model.Hotel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ConfirmBooking : AppCompatActivity() {
    private lateinit var calendarCome: Calendar
    private lateinit var tvDateCome: TextView
    private lateinit var tvDateLeave: TextView
    private lateinit var tvTypeRoom: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvThongbao: TextView
    private lateinit var tvStayDate: TextView
    private lateinit var edtName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAmout: EditText
    private lateinit var booking: Booking
    private lateinit var btnSubmit: Button
    var totalRoomBooking: Int = 0
    private val itemList = ArrayList<String>()
    private var itemRef: DatabaseReference? = null
    private var bookingRef: DatabaseReference? = null
    private lateinit var reference: DatabaseReference
    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
    private var listCheck = ArrayList<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_booking)
        initUi()
        booking = intent.getParcelableExtra("clickBooking")!!
        displayBooking()
        btnSubmit.setOnClickListener {
            clickSubmit()
        }
        itemRef = FirebaseDatabase.getInstance().reference.child("hotel")
    }

    private fun readData(firebaseCallback: FirebaseCallback) {
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var sum = 0
                for (child in snapshot.children) {
                    val room = child.getValue(Hotel::class.java)
                    if (room?.typeroom.equals(tvTypeRoom.text.toString())) {
                        sum++
                    }
                }
                itemList.add(sum.toString())
                firebaseCallback.onCallback(sum)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        itemRef?.addListenerForSingleValueEvent(valueEventListener)
    }

    private interface FirebaseCallback {
        fun onCallback(count: Int?)
    }

    private fun readCheckData(firebaseCallbackCheck: FirebaseCallbackCheck) {
        val strDateCome = tvDateCome.text.toString().trim()
        val strStayDate: Int = tvStayDate.text.toString().trim().toInt()
        val amout: Int = edtAmout.text.toString().trim().toInt()
        val day = strDateCome.substring(0, 2).toInt()
        val month = strDateCome.substring(3, 5).toInt()
        val year = strDateCome.substring(6).toInt()
        val calendar = Calendar.getInstance()
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                readData(object : FirebaseCallback {
                    override fun onCallback(count: Int?) {
                        if (snapshot.exists()) {
                            listCheck.clear()
                            for (child in snapshot.children) {
                                for (i in 0 until strStayDate) {
                                    Log.e("huy test thu", child.key.toString())
                                    calendar.set(year, month - 1, day + i)
                                    val date = simpleDateFormat.format(calendar.time).toString()
                                    totalRoomBooking = if (child.key?.equals(date)!!) {
                                        child.childrenCount.toInt()
                                    } else {
                                        0
                                    }
                                    if ((count!! - (totalRoomBooking + amout)) >= 0) {
                                        listCheck.add(true)
                                    } else {
                                        listCheck.add(false)
                                    }
                                }
                                Log.e("tien", "so la: $count-$totalRoomBooking-$amout")
                            }
                            for (i in 0 until listCheck.size) {
                                if (!listCheck[i]) {
                                    firebaseCallbackCheck.onCallback(false)
                                    break
                                }
                                firebaseCallbackCheck.onCallback(true)
                            }
                            Log.e("listcheck: ", listCheck.toString())
                        } else {
                            listCheck.clear()
                            if ((count!! - (totalRoomBooking + amout)) >= 0) {
                                Log.e("tien", "vao if: $count-$totalRoomBooking-$amout")
                                listCheck.add(true)
                            } else {
                                Log.e("tien", "vao else: " + (count - (totalRoomBooking + amout)))
                                listCheck.add(false)
                            }

                            for (i in 0 until listCheck.size) {
                                if (!listCheck[i]) {
                                    firebaseCallbackCheck.onCallback(false)
                                    break
                                }
                                firebaseCallbackCheck.onCallback(true)
                            }
                        }

                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        bookingRef?.addListenerForSingleValueEvent(valueEventListener)
    }

    private interface FirebaseCallbackCheck {
        fun onCallback(check: Boolean?)
    }

    private fun clickSubmit() {
        val strStayDate: String = tvStayDate.text.toString().trim()
        val ref = FirebaseDatabase.getInstance().reference
        readData(object : FirebaseCallback {
            override fun onCallback(count: Int?) {
                val strDateCome = tvDateCome.text.toString().trim()
                val strDateLeave = tvDateLeave.text.toString().trim()
                val strTyperoom = tvTypeRoom.text.toString().trim()
                val strEmail: String = tvEmail.text.toString().trim()
                val strName: String = edtName.text.toString().trim()
                val strPhone: String = edtPhone.text.toString().trim()
                val strAmount: String = edtAmout.text.toString().trim()
                val stayDate: Int = tvStayDate.text.toString().trim().toInt()
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                val dateTime = simpleDateFormat.format(calendar.time).toString()
                val amout: Int = edtAmout.text.toString().trim().toInt()
                val day = strDateCome.substring(0, 2).toInt()
                val dayCome = strDateLeave.substring(0, 2).toInt()
                val month = strDateCome.substring(3, 5).toInt()
                val year = strDateCome.substring(6).toInt()

                Log.e("tienne", count.toString())
                var temp = true
                bookingRef = FirebaseDatabase.getInstance().getReference("booking").child(strTyperoom)
                readCheckData(object : FirebaseCallbackCheck {
                    override fun onCallback(check: Boolean?) {
                        if (temp) {
                            temp = false
                            Log.e("check: ", check.toString())
                            if (check == false) {
                                tvThongbao.text = "Ngày chọn đã Hết phòng !!!"
                                tvThongbao.visibility = View.VISIBLE
                                return
                            } else {
                                val ticketRef = ref.child("ticket booking")
                                val booking: MutableMap<String, Any> = HashMap()
                                booking["datecome"] = strDateCome
                                booking["dateleave"] = strDateLeave
                                booking["typeroom"] = strTyperoom
                                booking["email"] = strEmail
                                booking["name"] = strName
                                booking["phone"] = strPhone
                                booking["amount"] = strAmount
                                booking["staydate"] = strStayDate
                                booking["currentdate"] = dateTime
                                ticketRef.push().setValue(booking)
                                for (i in 1..amout) {
                                    for (j in 0 until stayDate) {
                                        calendar.set(year, month - 1, day + j)

                                        val date = simpleDateFormat.format(calendar.time).toString()
                                        val dateBooking: MutableMap<String, Any> = HashMap()
                                        dateBooking["email"] = strEmail
                                        dateBooking["name"] = strName
                                        dateBooking["phone"] = strPhone
                                        reference = FirebaseDatabase.getInstance().reference
                                        val bookingRef1 = reference.child("booking").child(strTyperoom)
                                        bookingRef1.child(date).push().setValue(dateBooking)
                                    }
                                }
                                val intent = Intent(this@ConfirmBooking, MainScreenUser::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
            }
        })
    }

    private fun displayBooking() {
        tvDateCome.text = booking.datecome
        tvDateLeave.text = booking.dateleave
        tvTypeRoom.text = booking.typeroom
        tvEmail.text = booking.email
        edtName.setText(booking.name)
        edtPhone.setText(booking.phone)
        tvStayDate.text = booking.staydate.toString().trim()
    }

    private fun initUi() {
        tvDateCome = findViewById(R.id.ChkIn)
        tvDateLeave = findViewById(R.id.ChkOut)
        tvTypeRoom = findViewById(R.id.type_room_booking)
        tvEmail = findViewById(R.id.email_booking)
        tvThongbao = findViewById(R.id.tv_thongbao)
        tvStayDate = findViewById(R.id.staydate)
        edtName = findViewById(R.id.edt_name_booking)
        edtPhone = findViewById(R.id.edt_phone_booking)
        edtAmout = findViewById(R.id.edt_amout_booking)
        btnSubmit = findViewById(R.id.btnSubmit)
    }
}