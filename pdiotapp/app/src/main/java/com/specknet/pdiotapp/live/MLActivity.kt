package com.specknet.pdiotapp.live

//import org.tensorflow.lite.Interpreter
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.RespeckData
import com.amplifyframework.datastore.generated.model.ThingyData
import com.bumptech.glide.Glide
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.apache.commons.lang3.math.NumberUtils.toDouble
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MLActivity : AppCompatActivity() {

    lateinit var tflite: Interpreter
    var buffersize: Int = 250
    var timestep: Int = 100     //  4s
    var detection_interval: Int = 25
    var real_time: Int = 0

    lateinit var activeImage: ImageView
    lateinit var pieChart: PieChart

    lateinit var activity: TextView

    var acc_xs = arrayListOf<Float>()
    var acc_ys = arrayListOf<Float>()
    var acc_zs = arrayListOf<Float>()

    var gyro_xs = arrayListOf<Float>()
    var gyro_ys = arrayListOf<Float>()
    var gyro_zs = arrayListOf<Float>()

    var activity_buffersize: Int = 10
    var activity_buffer = arrayListOf<Int>()


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

    private lateinit var outputData: java.lang.StringBuilder

    val gif_list = arrayOf(
        R.drawable.sittinggif,
        R.drawable.sittingforwardgif,
        R.drawable.sittingbackwardgif,
        R.drawable.standinggif,
        R.drawable.lyingdownleftgif,
        R.drawable.lyingdownrightgif,
        R.drawable.lyingstomachgif,
        R.drawable.lyingbackgif,
        R.drawable.walkinggif,
        R.drawable.runninggif,
        R.drawable.upstairsgif,
        R.drawable.downstairs,
        R.drawable.deskgif,
        R.drawable.movementgif,
        R.drawable.fallingkneesgif,
        R.drawable.fallingbackgif,
        R.drawable.fallingleftrightgif,
        R.drawable.fallingleftrightgif
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



    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ml)

        //Create Amplyfy DataStore Begin

//        try {
//            Amplify.addPlugin(AWSApiPlugin())
//            Amplify.addPlugin(AWSDataStorePlugin())
//            Amplify.configure(applicationContext)
//            Log.i("Amplify", "Initialized Amplify")
//        } catch (failure: AmplifyException) {
//            Log.e("Amplify", "Could not initialize Amplify", failure)
//        }
//
//        Amplify.DataStore.observe(RespeckData::class.java,
//            { Log.i("Amplify", "Observation began.") },
//            { Log.i("Amplify", it.item().toString()) },
//            { Log.e("Amplify", "Observation failed.", it) },
//            { Log.i("Amplify", "Observation complete.") }
//        )
//
//        Amplify.DataStore.observe(ThingyData::class.java,
//            { Log.i("Amplify", "Observation began.") },
//            { Log.i("Amplify", it.item().toString()) },
//            { Log.e("Amplify", "Observation failed.", it) },
//            { Log.i("Amplify", "Observation complete.") }
//        )
//
//        val r = Random()




        //Create Amplify DataStore End

        outputData = StringBuilder()

        activity = findViewById(R.id.activity)
//        activity.text = "Movement"
        Log.d("myTag", activity.text as String)

        //load activity image
        activeImage = findViewById(R.id.activeimage)

        var simpleModelID: Int =    1
        var simpleModelName: String =   "4s"   // val items = arrayOf("2s", "4s", "8s", "custom")
        var baseModelName: String =  "ResNet"  // val items2 = arrayOf("CNN", "LSTM", "GRU", "biLSTM", "CNNbiLSTM", "ResNet", "ConvMix", "test")


        //read data from saved file:
        val arrayStats = IntArray(18)
        val currentUser = getIntent().getStringExtra("username");
//        var file_data = arrayListOf<String>()
        val filename = "${currentUser}.csv"
        val file = File(getExternalFilesDir(null), filename)
        val getDirectoryPath = file.parent
        val exists = file.exists()

        if (exists) {
            csvReader().open(getDirectoryPath +"/"+ filename) {
                readAllAsSequence().forEach { row ->
                    for (i in 0..17){
                        if(row[1] == motion_list[i]){
                            arrayStats[i] += 1
                        }
                    }
                }
            }
        }

        var previous_status:String = "Previous status:\n"
        Log.d("all_data", arrayStats.toList().toString())

        // adding a pie chart with animation for visualization of past status of this person
        pieChart = findViewById(R.id.pieChart)
        initPieChart()
        setDataToPieChart(arrayStats)

        // adding GPU delegate and the NNAPI (for CPU, for LSTM operations) optimization
        val compatList = CompatibilityList()
        Log.d("MyTag","Using GPU: "+ compatList.isDelegateSupportedOnThisDevice.toString())
        val options = Interpreter.Options().apply{
            if(compatList.isDelegateSupportedOnThisDevice){
                // if the device has a supported GPU, add the GPU delegate
                Log.d("MyTag", "Success3")
                val delegateOptions = compatList.bestOptionsForThisDevice
                this.addDelegate(GpuDelegate(delegateOptions))
                Log.d("MyTag", "Success2")
            } else {
                // if the GPU is not supported, run on 4 threads
                this.setNumThreads(4)
                val list = listOf("LSTM", "GRU", "biLSTM", "CNNbiLSTM")
                if(baseModelName in list) {
                    this.setUseNNAPI(true)    //LSTM model using NNAPI is fast but not Conv model
                    Log.d("MyTag", "Using NNAPI")
                }
                Log.d("MyTag", "Success1")
            }
        }

        try {
            tflite = Interpreter(loadModelFile(baseModelName,simpleModelName), options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("MyTag", "Load tfLite model succeed")

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
                    var acc_x = liveData.accelX
                    var acc_y = liveData.accelY
                    var acc_z = liveData.accelZ

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

                    var gyro_x = liveData.gyro.x
                    var gyro_y = liveData.gyro.y
                    var gyro_z = liveData.gyro.z

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


                    // adding the data to the cloud using AWS amplify
//                    val date = Date()
//                    val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
//                    val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
//                    val dataTime = Temporal.DateTime(date, offsetSeconds)
//                    val respeckItem = RespeckData.builder().resAccX(acc_x.toDouble()).resAccY(acc_y.toDouble()).resAccZ(acc_z.toDouble()).resGyroX(gyro_x.toDouble()).resGyroY(gyro_y.toDouble()).resGyroZ(gyro_z.toDouble()).resDataTime(dataTime).build()
//
//                    Amplify.DataStore.save(respeckItem,
//                        {Log.i("Saving respeck data", "Success")},
//                        {Log.e("Saving respeck data","Could not save it",it)}
//                        )

                    //Adding to DataStore end


                    //doing perdiction automatically here within every 1s (25 frame)
                    if(real_time>buffersize){
                        if(real_time.rem(detection_interval) ==0){

                            val results: Array<Any> = inference(baseModelName,simpleModelID, simpleModelName,acc_xs, acc_ys, acc_zs, gyro_xs, gyro_ys, gyro_zs)
                            val class_index = results[0] as Int
                            val confidence = results[1] as Float
                            Log.d(
                                "myTag",
                                Integer.toString(class_index) + " , " + java.lang.Float.toString(confidence)
                            )

                            val motion = motion_list[class_index]
                            val gif = gif_list[class_index]
//                            activity.setText(motion + ", confidence: " + "${confidence.format(3)}") // Integer.toString(prediction)\
                            // Error here: Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.

                            // try to touch View of UI thread
                            this@MLActivity.runOnUiThread(java.lang.Runnable {
                                activity.setText(motion + ", confidence: " + "${confidence.format(3)}") // Integer.toString(prediction)\
                                Glide.with(this@MLActivity).asGif().load(gif).into(activeImage)
                            })

                            var second:Int = (real_time - buffersize)/25
                            outputData.append( second.toString() + "," +motion+ "\n")
//                            outputData.append( time.toString() + "," +motion+ "\n")

                            if(activity_buffer.size>=activity_buffersize){
                                activity_buffer.removeFirst()
                            }
                            activity_buffer.add(class_index)
                        }
//                        //read data from saved file:
//                        if(real_time.rem(detection_interval) ==0) {
//                            var all_data = readFromCsv()
//                            Log.d("all_data", all_data[all_data.size - 1].toString())
//                        }
                    }

                    real_time += 1
                    time += 1
                    Log.d("time1", time.toString())
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

                    val date = Date()
                    val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
                    val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
                    val dataTime = Temporal.DateTime(date, offsetSeconds)
                    val thingyItem = RespeckData.builder().resAccX(x.toDouble()).resAccY(y.toDouble()).resAccZ(z.toDouble()).build()

                    Amplify.DataStore.save(thingyItem,
                        {Log.i("Saving thingy data", "Success")},
                        {Log.e("Saving thingy data","Could not save it",it)}
                    )

                    time += 1
                    Log.d("time2", time.toString())
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

//        Set changeable dynamical time window for diffrent activity detecytion
//        timestep = Math.pow(2.0, ((simpleModelID+1).toDouble())).toInt() * 25

        timestep = 100
        if(simpleModelName=="custom"){
            timestep = 250
        }
        Log.d("myTag", timestep.toString() + " timesteps (sliding window)")


        val inputValue = Array(1) {
            Array(timestep) {
                FloatArray(6)
            }
        }

        var starttimestep = buffersize-timestep
        if(acc_xs.size>=buffersize) {
            for (i in 0..(timestep-1)) {
                inputValue[0][i][0] = acc_xs.get(starttimestep + i);
                inputValue[0][i][1] = acc_ys.get(starttimestep + i);
                inputValue[0][i][2] = acc_zs.get(starttimestep + i);
                inputValue[0][i][3] = gyro_xs.get(starttimestep + i);
                inputValue[0][i][4] = gyro_ys.get(starttimestep + i);
                inputValue[0][i][5] = gyro_zs.get(starttimestep + i);
//                Log.d("myTag", i.toString())
            }
        }else {
            for (i in 0..(timestep-1)) {
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


    fun writeToHistory(){

        val currentUser = getIntent().getStringExtra("username");
        val currentTime = System.currentTimeMillis()
        var formattedDate = ""
        try {
            formattedDate = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.UK).format(Date())
            Log.i("TAG", "saveRecording: formattedDate = " + formattedDate)
        } catch (e: Exception) {
            Log.i("TAG", "saveRecording: error = ${e.toString()}")
            formattedDate = currentTime.toString()
        }
//        val filename = "${currentUser}_${formattedDate}.csv"
        val filename = "${currentUser}.csv"

        val file = File(getExternalFilesDir(null), filename)
        val dataWriter: BufferedWriter

        // Create file for current day and append header, if it doesn't exist yet
        try {
            val exists = file.exists()
            dataWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true)))

            if (!exists) {
                Log.d("TAG", "saveRecording: filename doesn't exist")
                // the header columns in here
//                dataWriter.append("# Motion Type").append("\n")

                dataWriter.write("timestamp,Motion Type")
                dataWriter.newLine()
                dataWriter.flush()
            }
            else {
                Log.d("TAG", "saveRecording: filename exists")
            }

            if (outputData.isNotEmpty()) {
                dataWriter.write(outputData.toString())   // outputData.append( time.toString() + "," +motion+ "\n")
                dataWriter.flush()
            }

            dataWriter.close()
            outputData = StringBuilder()
            Toast.makeText(this, "Successfully saved motion data!", Toast.LENGTH_SHORT).show()
        }
        catch (e: IOException) {
            Toast.makeText(this, "Error while saving saving!", Toast.LENGTH_SHORT).show()
        }
    }

    fun readFromCsv(): ArrayList<String> {
        val currentUser = getIntent().getStringExtra("username");
        var file_data = arrayListOf<String>()
        val filename = "${currentUser}.csv"
        val file = File(getExternalFilesDir(null), filename)
        val getDirectoryPath =
            file.parent // Only return path if physical file exist else return null

        csvReader().open(getDirectoryPath +"/"+ filename) {
            readAllAsSequence().forEach { row ->
                file_data.add(row[0])
            }
        }
        return file_data
    }


    private fun initPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.text = ""
        //hollow pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)
        //adding padding
        pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        pieChart.setUsePercentValues(true)
        pieChart.isRotationEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isWordWrapEnabled = true

    }

    private fun setDataToPieChart(arrayStats: IntArray) {
//
        var sorted_arr = arrayStats.sortedArray()
        var first = sorted_arr[sorted_arr.size - 1]
        var second = sorted_arr[sorted_arr.size - 2]
        var third = sorted_arr[sorted_arr.size - 3]
        var first_index = arrayStats.indexOf(first)
        var second_index = arrayStats.indexOf(second)
        var third_index = arrayStats.indexOf(third)
        var sum = arrayStats.sum()
//        Log.d("test", first.toString() + " "+ second.toString()+ " "+first_index.toString()+ " "+second_index.toString() )

        pieChart.setUsePercentValues(true)
        val dataEntries = ArrayList<PieEntry>()
        dataEntries.add(PieEntry((first.toFloat())/(sum.toFloat()), motion_list[first_index]))
        dataEntries.add(PieEntry((second.toFloat())/(sum.toFloat()), motion_list[second_index]))
        dataEntries.add(PieEntry((third.toFloat())/(sum.toFloat()), motion_list[third_index]))
        dataEntries.add(PieEntry(1 - (first.toFloat())/(sum.toFloat()) - second.toFloat()/sum.toFloat() - third.toFloat()/sum.toFloat(), "Others"))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#4DD0E1"))
        colors.add(Color.parseColor("#FFF176"))
        colors.add(Color.parseColor("#FF8A65"))
        colors.add(Color.parseColor("#519C3F"))

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter())
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        pieChart.data = data
        data.setValueTextSize(15f)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        //add text in center
        pieChart.setDrawCenterText(true);
        pieChart.centerText = "Past Activity Summary"

        pieChart.invalidate()
    }


    override fun onDestroy() {
        writeToHistory()
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        unregisterReceiver(thingyLiveUpdateReceiver)
        looperRespeck.quit()
        looperThingy.quit()

    }
}
