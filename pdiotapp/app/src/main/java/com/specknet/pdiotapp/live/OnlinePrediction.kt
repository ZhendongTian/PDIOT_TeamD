package com.specknet.pdiotapp.live

//import org.tensorflow.lite.Interpreter
import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.slider.Slider
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData

import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


//adding neural network for kotlin here
//
//const val debug = false
//const val learningRate = 0.01


class OnlinePrediction : AppCompatActivity() {

    var activity_index:Int = 0

    var slider_index:Int = -1
    var neuronBiases1:DoubleArray = doubleArrayOf(
        0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
    )
    var neuronBiases2:DoubleArray = doubleArrayOf(
        0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
    )
    var synapsesWeights0:DoubleArray = doubleArrayOf(
        1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0

    )
    var synapsesWeights1:DoubleArray = doubleArrayOf(
        1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0
    )

    lateinit var tflite: Interpreter
    var buffersize: Int = 250
    var timestep: Int = 100     //  4s
    var detection_interval: Int = 25
    var real_time: Int = 0

    lateinit var activeImage: ImageView
//    lateinit var pieChart: PieChart
//    lateinit var slider:Slider

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

    var time = 0f
    lateinit var allRespeckData: LineData
    lateinit var respeckChart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper

    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)

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
        setContentView(R.layout.activity_online_prediction)

//        val currentUser = getIntent().getStringExtra("username");

        outputData = StringBuilder()

        activity = findViewById(R.id.activity)
//        activity.text = "Movement"

        //load activity image
//        activeImage = findViewById(R.id.activeimage)

        // We also provide other options to choose from:
        var simpleModelID: Int =    1
        var simpleModelName: String =   "4s"   // val items = arrayOf("2s", "4s", "8s", "custom")
        var baseModelName: String =  "ResNet"  // val items2 = arrayOf("CNN", "LSTM", "GRU", "biLSTM", "CNNbiLSTM", "ResNet", "ConvMix", "test")

        // slider to change for different matrix customization (NN learnt on the phone previously)
        //    android:valueFrom="0.0"
        //    android:valueTo="17.0"
        //    android:stepSize="1.0"
//        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
//            @SuppressLint("RestrictedApi")
//            override fun onStartTrackingTouch(slider: Slider) {
//                // Responds to when slider's touch event is being started
//                slider_index = slider.value.toInt()
//                Log.d("Slider", slider_index.toString())
//                // read from previous trained on phone NN
//                readMatrix(slider_index)
//            }
//
//            @SuppressLint("RestrictedApi")
//            override fun onStopTrackingTouch(slider: Slider) {
//                // Responds to when slider's touch event is being stopped
//            }
//        })
//
//        slider.addOnChangeListener { slider, value, fromUser ->
//            // Responds to when slider's value is changed
//            slider_index = slider.value.toInt()
//            Log.d("Slider", slider_index.toString())
//            // read from previous trained on phone NN
//            readMatrix(slider_index)
//
//        }



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

                    //doing perdiction automatically here within every 1s (25 frame)
                    var weightString1:String = "Confusion Matrix (for online update): \n"
                    for(ii in 0..17){
                        for(jj in 0..17){
                            var matrixIndex:Int = ii*18 + jj
                            var matrixValue:Double = synapsesWeights0[matrixIndex]
                            weightString1 += "${matrixValue.toFloat().format(3)}| "
                        }
                        weightString1 += "\n"
                    }

                    var activityString = motion_list[activity_index]
                    var confusionResults1:String = "This behaviour $activityString is often misclassfied as: \n"
                    var confusionResults:String ="\n"
                    var wrong_list = arrayListOf<Int>()
                    for(ii in 0..17){
                        var matrixIndex:Int = ii*18 + activity_index
                        var matrixValue:Double = synapsesWeights0[matrixIndex]
                        if(matrixValue>0.05 && ii != activity_index) {
                            wrong_list.add(ii)
                            var wrongActivity = motion_list[ii]
                            confusionResults += "$wrongActivity with off-set ${matrixValue.toFloat().format(3)}; "
                            confusionResults += "\n"
                        }
                    }
                    if(wrong_list.size!=0){
                        confusionResults = confusionResults1+confusionResults
                        confusionResults += "For this person, the model is adapted to adjust this off-set"
                    }else{
                        confusionResults += "This activity can be well-predicted with general data model, no need to fine-tune further"
                    }


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
                            this@OnlinePrediction.runOnUiThread(java.lang.Runnable {
                                activity.setText(motion + ", confidence: " + "${confidence.format(3)}") // Integer.toString(prediction)\
//                                Glide.with(this@OnlineActivity).asGif().load(gif).into(activeImage)
                            })

                            var second:Int = (real_time - buffersize)/25


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

    }

    private fun readMatrix(sliderIndex: Int) {
        // read data from saved file, weights and bias:
        // read the fine-tuned model that is just trained on the phone NN with customized calibration
        val arrayStats = IntArray(18)
        val currentUser = getIntent().getStringExtra("username");
//        var file_data = arrayListOf<String>()
        val filename = "${currentUser}_activity${sliderIndex}.csv"
        val file = File(getExternalFilesDir(null), filename)
        val getDirectoryPath = file.parent
        val exists = file.exists()

        if (exists) {
            csvReader().open(getDirectoryPath +"/"+ filename) {
                var allSequence = readAllAsSequence()
                var sequenceList = allSequence.toList()
                var lengthList = sequenceList.size
                var rowList = sequenceList.get(lengthList-1) //as String
                if(rowList[0].toInt() == activity_index){
                    for(weightIndex in 1..(synapsesWeights0.size)){
                        synapsesWeights0[weightIndex] = rowList[weightIndex].toDouble()
                    }
                }
            }
        }

        Log.d("all_data", arrayStats.toList().toString())
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

        val network1 = Network()

        var new_inputs = outputValue.get(0)
        val new_input = DoubleArray(new_inputs.size)
        for (i in 0 until new_inputs.size) {
            new_input[i] = new_inputs.get(i).toDouble()
        }

        val null_label = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        network1.setData(new_input, null_label)
        network1.initialize(1, 18, 18, 18)



        //read data from saved file:
        val currentUser = getIntent().getStringExtra("username");
//        var file_data = arrayListOf<String>()
        var aI:Int = 0
        val filename = "${currentUser}_activity${aI}.csv"
        val file = File(getExternalFilesDir(null), filename)
        val getDirectoryPath = file.parent
        val exists = file.exists()

        if (exists) {
            csvReader().open(getDirectoryPath +"/"+ filename) {
                readAllAsSequence().forEach { row ->
                    for (i in 0..(row.size)){
                        if(row[0].toInt() == aI){
                            synapsesWeights0[i] = row[i].toDouble()
                        }
                    }
                }
            }
        }



        network1.neuronBiases[1] = neuronBiases1
        network1.neuronBiases[2] = neuronBiases2

        network1.synapsesWeights[0] = synapsesWeights0
        network1.synapsesWeights[1] = synapsesWeights1

        var online_result = network1.output()

        val online_results = FloatArray(online_result.size)
        for (i in 0 until online_result.size) {
            online_results[i] = online_result.get(i).toFloat()
        }

        var class_prob = argmax(online_results)
//        if(!((class_prob[0] as Int == activity_index) && (class_prob[1] as Float > 0.8) ))
        //Obtained highest prediction
        //        int index = argmax(outputValue.get(0))
        // here we use the fine-tuned model which is used previously on the online training part every 0.5s, and we choose what off0-set (confusion matrix we want to use /learn from)!

        return argmax(online_results)
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
        }
    }


    fun writeToHistory(){
        val currentUser = getIntent().getStringExtra("username");
        val currentActivityIndexplus = getIntent().getIntExtra("activity_index", 0)
        val currentActivityIndex = currentActivityIndexplus -1
        val filename = "${currentUser}_activity${currentActivityIndex}.csv"
        val file = File(getExternalFilesDir(null), filename)
        val dataWriter: BufferedWriter
        try {
            val exists = file.exists()
            dataWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file, true)))
            if (!exists) {
                var dataWriterString = "activity index,"
                var lengthMinusOne = synapsesWeights0.size -1
                for(iii in 0..lengthMinusOne){
                    dataWriterString += "C[$iii],"
                }
                dataWriter.write(dataWriterString)
                dataWriter.newLine()
                dataWriter.flush()
            }
            else {Log.d("TAG", "saveRecording: filename exists") }
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
        val getDirectoryPath = file.parent // Only return path if physical file exist else return null
        csvReader().open(getDirectoryPath +"/"+ filename) {
            readAllAsSequence().forEach { row ->
                file_data.add(row[0])
            }
        }
        return file_data
    }


    override fun onDestroy() {
        writeToHistory()
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        looperRespeck.quit()
    }


}
