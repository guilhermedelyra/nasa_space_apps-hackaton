package com.sei.la.ue

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.os.Parcel
import android.os.Parcelable

class MainActivity() : AppCompatActivity(), SensorEventListener {

    private var senSensorManager: SensorManager? = null
    private var senAccelerometer: Sensor? = null
    private var senMagneticField: Sensor? = null

    private  var ipAddress: String = "192.168.0.27"
    private  var port:String = "5005"

    private lateinit var ipEditText: EditText
    private lateinit var portEditText: EditText
    private lateinit var startStopSwitch: Switch

    private lateinit var sensorNameTextView: TextView
    private lateinit var sensorManufacturerTextView: TextView
    private lateinit var sensorVersionTextView: TextView
    private lateinit var sensorPowerTextView: TextView
    private lateinit var sensorResolutionTextView: TextView
    private lateinit var sensorMaximumReachTextView: TextView


    private lateinit var xAxisTextView: TextView
    private lateinit var yAxisTextView: TextView
    private lateinit var zAxisTextView: TextView
    private lateinit var xAxisTextView1: TextView
    private lateinit var yAxisTextView1: TextView
    private lateinit var zAxisTextView1: TextView

    private var time = 0

    var handX = "0.5"
    var handY = "0.5"
    var handZ = "0.5"

    var rightHandX = "0.5"
    var rightHandY = "0.5"
    var rightHandZ = "0.5"

    var armX = "0.5"
    var armY = "0.5"
    var armZ = "0.5"

    var rightArmX = "0.5"
    var rightArmY = "0.5"
    var rightArmZ = "0.5"

    var sholderX = "0.5"
    var sholderY = "0.5"
    var sholderZ = "0.5"

    var rightSholderX = "0.5"
    var rightSholderY = "0.5"
    var rightSholderZ = "0.5"

    var elbowX = "0.5"
    var elbowY = "0.5"
    var elbowZ = "0.5"

    var rightElbowX = "0.5"
    var rightElbowY = "0.5"
    var rightElbowZ = "0.5"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendMessage("tchau")

        ipEditText = findViewById(R.id.ipEditText)
        portEditText = findViewById(R.id.portEditText)
        startStopSwitch = findViewById(R.id.startStopSwitch)

        sensorNameTextView =  findViewById(R.id.sensorNameTextView)
        sensorManufacturerTextView =  findViewById(R.id.sensorManufacturerTextView)
        sensorVersionTextView =  findViewById(R.id.sensorVersionTextView)
        sensorPowerTextView =  findViewById(R.id.sensorPowerTextView)
        sensorResolutionTextView =  findViewById(R.id.sensorResolutionTextView)
        sensorMaximumReachTextView =  findViewById(R.id.sensorMaximumReachTextView)

        xAxisTextView = findViewById(R.id.xAxisTextView)
        yAxisTextView = findViewById(R.id.yAxisTextView)
        zAxisTextView = findViewById(R.id.zAxisTextView)

        xAxisTextView1 = findViewById(R.id.xAxisTextView1)
        yAxisTextView1 = findViewById(R.id.yAxisTextView1)
        zAxisTextView1 = findViewById(R.id.zAxisTextView1)

        senSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        senAccelerometer = senSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        senMagneticField = senSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        senSensorManager!!.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        senSensorManager!!.registerListener(this, senMagneticField, SensorManager.SENSOR_DELAY_NORMAL)

        sensorNameTextView.text = senAccelerometer!!.name.toString()
        sensorManufacturerTextView.text = senAccelerometer!!.vendor
        sensorVersionTextView.text = senAccelerometer!!.version.toString()
        sensorPowerTextView.text = senAccelerometer!!.power.toString() + " mA"
        sensorResolutionTextView.text =  senAccelerometer!!.resolution.toString() + " m/s²"
        sensorMaximumReachTextView.text =  senAccelerometer!!.maximumRange.toString() + " m/s²"
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val mySensor = sensorEvent!!.sensor

        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            xAxisTextView.text = x.toString()
            yAxisTextView.text = y.toString()
            zAxisTextView.text = z.toString()

            if(startStopSwitch.isChecked) {
                Log.d("accelerometer", "send")
                createData(x.toString(), y.toString(), z.toString(), "accelerometer")
            }
        }

        if (mySensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            xAxisTextView.text = x.toString()
            yAxisTextView.text = y.toString()
            zAxisTextView.text = z.toString()

            if(startStopSwitch.isChecked) {
                Log.d("magneticfield", "send")
                createData(x.toString() ,y.toString(), z.toString(), "magneticfield")
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onPause() {
        super.onPause()
        senSensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        senSensorManager!!.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        senSensorManager!!.registerListener(this, senMagneticField, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun sendMessage(message: String) {

        val handler = Handler()
        val thread = Thread(object : Runnable {

            internal lateinit var stringData: String

            override fun run() {

                var ds: DatagramSocket? = null
                try {
                    ds = DatagramSocket()
                    // IP Address below is the IP address of that Device where server socket is opened.
                    val serverAddr = InetAddress.getByName(ipEditText.text.toString())
                    var dp: DatagramPacket
                    dp = DatagramPacket(message.toByteArray(), message.length, serverAddr, 5004)
                    ds.send(dp)

                    val lMsg = ByteArray(1024)
                    dp = DatagramPacket(lMsg, lMsg.size)
                    ds.receive(dp)
                    stringData = String(lMsg, 0, dp.length)

                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (ds != null) {
                        ds.close()
                    }
                }

                handler.post(Runnable {
//                    val s = mTextViewReplyFromServer.getText().toString()
//                    if (stringData.trim { it <= ' ' }.length != 0)
//                        mTextViewReplyFromServer.setText(s + "\nFrom Server : " + stringData)
                })
            }
        })

        thread.start()
    }

    private fun createData(x: String, y: String, z: String, sen: String) {

        time = time.plus(0.2).toInt()
        if (sen == "accelerometer") {
            formactData(x, y, z)
        }
        if (sen == "magneticfield") {
            sendMessage("$sen=$x $y $z")
        }
    }

    private fun formactData(x: String, y: String, z: String) {
        var message: String = time.toString() + " "

        message += "$handX $handY $handZ "
        message += "$rightHandX $rightHandY $rightHandZ "
        message += "$sholderX $sholderY $sholderZ "
        message += "$rightElbowX $rightElbowY $rightElbowZ "
        message += "$sholderX $sholderY $sholderZ "
        message += "$rightSholderX $rightSholderY $rightSholderZ "
        message += "$x $y $z "
        message += "$rightArmX $rightArmY $rightArmZ "

        sendMessage("accelerometer="+message)
    }
}
