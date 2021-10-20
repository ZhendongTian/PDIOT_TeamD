package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class MLActivity : AppCompatActivity() {

    lateinit var tflite: Interpreter
    var buffersize: Int = 250
    var timestep: Int = 50

    lateinit var activity: TextView
    lateinit var modelname2: TextView
    lateinit var modelname1: TextView
    var acc_xs = arrayListOf<Float>()
    var acc_ys = arrayListOf<Float>()
    var acc_zs = arrayListOf<Float>()

    var gyro_xs = arrayListOf<Float>()
    var gyro_ys = arrayListOf<Float>()
    var gyro_zs = arrayListOf<Float>()


    // global graph variables
    lateinit var dataSet_res_accel_x: LineDataSet
    lateinit var dataSet_res_accel_y: LineDataSet
    lateinit var dataSet_res_accel_z: LineDataSet

    lateinit var dataSet_thingy_accel_x: LineDataSet
    lateinit var dataSet_thingy_accel_y: LineDataSet
    lateinit var dataSet_thingy_accel_z: LineDataSet

    var time = 0f
    lateinit var allRespeckData: LineData

    lateinit var allThingyData: LineData

    lateinit var respeckChart: LineChart
    lateinit var thingyChart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var thingyLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper
    lateinit var looperThingy: Looper

    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
    val filterTestThingy = IntentFilter(Constants.ACTION_THINGY_BROADCAST)


    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ml)

        activity = findViewById(R.id.activity)
//        activity.text = "Movement"
        Log.d("myTag", activity.text as String)

        modelname2 = findViewById(R.id.model2)
        modelname1 = findViewById(R.id.model1)

        //get the spinner from the xml.
        val dropdown = findViewById<Spinner>(R.id.spinner1)
//create a list of items for the spinner.
        val items = arrayOf("2s", "4s", "8s")
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
//set the spinners adapter to the previously created one.
        dropdown.adapter = adapter
        var simpleModelName = dropdown.selectedItem.toString()  //default is the first one in the dropbox
        var simpleModelID: Int = dropdown.selectedItemId.toInt()

        dropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                simpleModelName = dropdown.selectedItem.toString()
                simpleModelID = dropdown.selectedItemId.toInt()
                Log.d("myTag", simpleModelID.toString())
                modelname2.setText(simpleModelName)
                modelname2.setTextColor(Color.BLUE)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val dropdown2 = findViewById<Spinner>(R.id.spinner2)
        val items2 = arrayOf("CNN", "LSTM", "GRU", "biLSTM")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items2)
//set the spinners adapter to the previously created one.
        dropdown2.adapter = adapter2
        var baseModelName = dropdown2.selectedItem.toString()
        dropdown2.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                baseModelName = dropdown2.selectedItem.toString()
                modelname1.setText(baseModelName)
                modelname1.setTextColor(Color.MAGENTA);
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }



        try {
            tflite = Interpreter(loadModelFile(baseModelName,simpleModelName))
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val detect = findViewById<View>(R.id.detect) as Button
        detect.setOnClickListener(View.OnClickListener {
            //                float prediction = inference(input.getText().toString());
            val results: Array<Any> = inference(baseModelName,simpleModelID, simpleModelName,acc_xs, acc_ys, acc_zs, gyro_xs, gyro_ys, gyro_zs)
            val class_index = results[0] as Int
            val confidence = results[1] as Float
            Log.d(
                "myTag",
                Integer.toString(class_index) + " , " + java.lang.Float.toString(confidence)
            )
            val motion_list = arrayOf(
                "Sitting",
                "Sitting bent forward",
                "Sitting bent backward",
                "Standing",
                "Lying down left",
                "Lying down right",
                "Lying down on stomach",
                "Lying down on back",
                "Walking at normal speed",
                "Running",
                "Climbing stairs",
                "Descending stairs",
                "Desk work",
                "Movement",
                "Falling on knees",
                "Falling on the back",
                "Falling on the left",
                "Falling on the right"
            )
            val motion = motion_list[class_index]
            activity.setText(motion + ", confidence: " + "${confidence.format(3)}") // Integer.toString(prediction)
        })


        setupCharts()


        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
                    val acc_x = liveData.accelX
                    val acc_y = liveData.accelY
                    val acc_z = liveData.accelZ

                    if(acc_xs.size>=buffersize){          // Make all buffer arraylist have same size [buffersize]
                        acc_xs.removeFirst()
                    }
                    if(acc_ys.size>=buffersize){
                        acc_ys.removeFirst()
                    }
                    if(acc_zs.size>=buffersize){
                        acc_zs.removeFirst()
                    }

                    acc_xs.add(acc_x)
                    acc_ys.add(acc_y)
                    acc_zs.add(acc_z)

                    val gyro_x = liveData.gyro.x
                    val gyro_y = liveData.gyro.y
                    val gyro_z = liveData.gyro.z


                    if(gyro_xs.size>=buffersize){
                        gyro_xs.removeFirst()
                    }
                    if(gyro_ys.size>=buffersize){
                        gyro_ys.removeFirst()
                    }
                    if(gyro_zs.size>=buffersize){
                        gyro_zs.removeFirst()
                    }

                    gyro_xs.add(gyro_x)
                    gyro_ys.add(gyro_y)
                    gyro_zs.add(gyro_z)

//                    Log.d("myTag", gyro_zs.size.toString())

                    time += 1
                    updateGraph("respeck", acc_x, acc_y, acc_z)
                }
            }
        }

        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)

        // set up the broadcast receiver
        thingyLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_THINGY_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData
//                    val liveData =
//                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
//                    val gyro_x = liveData.gyro.x
//                    val gyro_y = liveData.gyro.y
//                    val gyro_z = liveData.gyro.z

                    val x = liveData.accelX
                    val y = liveData.accelY
                    val z = liveData.accelZ

                    time += 1
                    updateGraph("thingy", x, y, z)

                }
            }
        }

        // register receiver on another thread
        val handlerThreadThingy = HandlerThread("bgThreadThingyLive")
        handlerThreadThingy.start()
        looperThingy = handlerThreadThingy.looper
        val handlerThingy = Handler(looperThingy)
        this.registerReceiver(thingyLiveUpdateReceiver, filterTestThingy, null, handlerThingy)

    }


    @Throws(IOException::class)
    private fun loadModelFile(baseModelName: String, simpleModelName: String): MappedByteBuffer {
        val fileDescriptor = this.assets.openFd(baseModelName+simpleModelName+".tflite")
        val fileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startoffSets = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffSets, declaredLength)
    }

    fun Float.format(digits: Int) = "%.${digits}f".format(this)


    fun inference(
        baseModelName: String,
        simpleModelID: Int,
        simpleModelName: String,
        acc_xs: ArrayList<Float>,
        acc_ys: ArrayList<Float>,
        acc_zs: ArrayList<Float>,
        gyro_xs: ArrayList<Float>,
        gyro_ys: ArrayList<Float>,
        gyro_zs: ArrayList<Float>
    ): Array<Any> {

        try {
            tflite = Interpreter(loadModelFile(baseModelName,simpleModelName))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        timestep = Math.pow(2.0, ((simpleModelID+1).toDouble())).toInt() * 25
        Log.d("myTag", timestep.toString())



        // A 6x5 array of Int, all set to 0.
//        var inputValue = Array(1) { Array(50) { Array(6) { 0f } } }
//        var m = Array(6, {i -> Array(5, {j -> 0})})
        //        inputValue[0] = Float.valueOf(s);
//        var outputValue =  Array(1) { Array(18) { 0f } }

        val inputValue = Array(1) {
            Array(timestep) {
                FloatArray(6)
            }
        }

        var starttimestep = buffersize-timestep
        if(acc_xs.size>=buffersize) {
            for (i in 0..49) {
                inputValue[0][i][0] = acc_xs.get(starttimestep + i);
                inputValue[0][i][1] = acc_ys.get(starttimestep + i);
                inputValue[0][i][2] = acc_zs.get(starttimestep + i);
                inputValue[0][i][3] = gyro_xs.get(starttimestep + i);
                inputValue[0][i][4] = gyro_ys.get(starttimestep + i);
                inputValue[0][i][5] = gyro_zs.get(starttimestep + i);
            }
        }else {
            for (i in 0..49) {
                inputValue[0][i][0] = 0f;
                inputValue[0][i][1] = 0f;
                inputValue[0][i][2] = 0f;
                inputValue[0][i][3] = 0f;
                inputValue[0][i][4] = 0f;
                inputValue[0][i][5] = 0f;
            }
        }


        val outputValue = Array(1) {
            FloatArray(
                18
            )
        }

        tflite.run(inputValue, outputValue)


//        int index = argmax(outputValue[0]);
        //Obtained highest prediction
        //        int index = argmax(outputValue[0]);
        //Obtained highest prediction

        return argmax(outputValue.get(0))

    }

    fun argmax(array: FloatArray): Array<Any> {
        var best = -1
        var best_confidence = 0.0f
        for (i in array.indices) {
            val value = array.get(i)
            if (value > best_confidence) {
                best_confidence = value
                best = i
            }
        }

        return arrayOf(best, best_confidence)
    }


    fun setupCharts() {
        respeckChart = findViewById(R.id.respeck_chart)
        thingyChart = findViewById(R.id.thingy_chart)

        // Respeck

        time = 0f
        val entries_res_accel_x = ArrayList<Entry>()
        val entries_res_accel_y = ArrayList<Entry>()
        val entries_res_accel_z = ArrayList<Entry>()

        dataSet_res_accel_x = LineDataSet(entries_res_accel_x, "Accel X")
        dataSet_res_accel_y = LineDataSet(entries_res_accel_y, "Accel Y")
        dataSet_res_accel_z = LineDataSet(entries_res_accel_z, "Accel Z")

        dataSet_res_accel_x.setDrawCircles(false)
        dataSet_res_accel_y.setDrawCircles(false)
        dataSet_res_accel_z.setDrawCircles(false)

        dataSet_res_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_res_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_res_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsRes = ArrayList<ILineDataSet>()
        dataSetsRes.add(dataSet_res_accel_x)
        dataSetsRes.add(dataSet_res_accel_y)
        dataSetsRes.add(dataSet_res_accel_z)

        allRespeckData = LineData(dataSetsRes)
        respeckChart.data = allRespeckData
        respeckChart.invalidate()

        // Thingy

        time = 0f
        val entries_thingy_accel_x = ArrayList<Entry>()
        val entries_thingy_accel_y = ArrayList<Entry>()
        val entries_thingy_accel_z = ArrayList<Entry>()

        dataSet_thingy_accel_x = LineDataSet(entries_thingy_accel_x, "Gyro X")
        dataSet_thingy_accel_y = LineDataSet(entries_thingy_accel_y, "Gyro Y")
        dataSet_thingy_accel_z = LineDataSet(entries_thingy_accel_z, "Gyro Z")

        dataSet_thingy_accel_x.setDrawCircles(false)
        dataSet_thingy_accel_y.setDrawCircles(false)
        dataSet_thingy_accel_z.setDrawCircles(false)

        dataSet_thingy_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_thingy_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_thingy_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsThingy = ArrayList<ILineDataSet>()
        dataSetsThingy.add(dataSet_thingy_accel_x)
        dataSetsThingy.add(dataSet_thingy_accel_y)
        dataSetsThingy.add(dataSet_thingy_accel_z)

        allThingyData = LineData(dataSetsThingy)
        thingyChart.data = allThingyData
        thingyChart.invalidate()
    }

    fun updateGraph(graph: String, x: Float, y: Float, z: Float) {
        // take the first element from the queue
        // and update the graph with it
        if (graph == "respeck") {
            dataSet_res_accel_x.addEntry(Entry(time, x))
            dataSet_res_accel_y.addEntry(Entry(time, y))
            dataSet_res_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allRespeckData.notifyDataChanged()
                respeckChart.notifyDataSetChanged()
                respeckChart.invalidate()
                respeckChart.setVisibleXRangeMaximum(150f)
                respeckChart.moveViewToX(respeckChart.lowestVisibleX + 40)
            }
        } else if (graph == "thingy") {
            dataSet_thingy_accel_x.addEntry(Entry(time, x))
            dataSet_thingy_accel_y.addEntry(Entry(time, y))
            dataSet_thingy_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allThingyData.notifyDataChanged()
                thingyChart.notifyDataSetChanged()
                thingyChart.invalidate()
                thingyChart.setVisibleXRangeMaximum(150f)
                thingyChart.moveViewToX(thingyChart.lowestVisibleX + 40)
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        unregisterReceiver(thingyLiveUpdateReceiver)
        looperRespeck.quit()
        looperThingy.quit()
    }
}
