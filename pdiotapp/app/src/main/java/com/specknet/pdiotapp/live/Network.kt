package com.specknet.pdiotapp.live

import android.util.Log
import dev.azn9.nnt.util.debugPrintln
import dev.azn9.nnt.util.sigmoid
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow



data class Network(
    val neuronOutput: ArrayList<DoubleArray>,
    val neuronRawInput: ArrayList<DoubleArray>,
    val neuronBiases: ArrayList<DoubleArray>,
    val synapsesWeights: ArrayList<DoubleArray>,
    val synapsesOldWeights: ArrayList<DoubleArray>,
    val synapsesSpcd: ArrayList<DoubleArray>,
    var lastOutput: DoubleArray
) {

    val debug = false
    val learningRate = 0.01

    private lateinit var input: DoubleArray
    private lateinit var expected: DoubleArray

    constructor() : this(
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        doubleArrayOf(0.0)
    )

    fun initialize(hiddenLayerCount: Int, inputNeuronCount: Int, hiddenLayerNeuronCount: Int, outputNeuronCount: Int) {
        assert(input.size == inputNeuronCount)

        neuronRawInput.add(DoubleArray(inputNeuronCount) { i -> input[i] })
        neuronOutput.add(DoubleArray(inputNeuronCount) { i -> input[i] })
        neuronBiases.add(DoubleArray(inputNeuronCount) { 0.0 })

        for (i in 0 until inputNeuronCount) {
            neuronBiases[0][i] = random.nextDouble()
        }

        for (i in 0 until hiddenLayerCount) {
            neuronRawInput.add(DoubleArray(hiddenLayerNeuronCount) { 0.0 })
            neuronOutput.add(DoubleArray(hiddenLayerNeuronCount) { 0.0 })
            neuronBiases.add(DoubleArray(hiddenLayerNeuronCount) { 0.0 })

            for (j in 0 until hiddenLayerNeuronCount) {
                neuronBiases[i + 1][i] = random.nextDouble()
            }
        }

        neuronRawInput.add(DoubleArray(hiddenLayerNeuronCount) { 0.0 })
        neuronOutput.add(DoubleArray(outputNeuronCount) { 0.0 })
        neuronBiases.add(DoubleArray(outputNeuronCount) { 0.0 })

        for (i in 0 until outputNeuronCount) {
            neuronBiases[neuronBiases.size - 1][i] = random.nextDouble()
        }

        synapsesWeights.add(DoubleArray(inputNeuronCount * hiddenLayerNeuronCount) { 0.0 })
        synapsesOldWeights.add(DoubleArray(inputNeuronCount * hiddenLayerNeuronCount) { 0.0 })
        synapsesSpcd.add(DoubleArray(inputNeuronCount * hiddenLayerNeuronCount) { 0.0 })
        for (i in 1 until hiddenLayerCount) {
            synapsesWeights.add(DoubleArray(hiddenLayerNeuronCount * hiddenLayerNeuronCount) { 0.0 })
            synapsesOldWeights.add(DoubleArray(hiddenLayerNeuronCount * hiddenLayerNeuronCount) { 0.0 })
            synapsesSpcd.add(DoubleArray(hiddenLayerNeuronCount * hiddenLayerNeuronCount) { 0.0 })
        }
        synapsesWeights.add(DoubleArray(hiddenLayerNeuronCount * outputNeuronCount) { 0.0 })
        synapsesOldWeights.add(DoubleArray(hiddenLayerNeuronCount * outputNeuronCount) { 0.0 })
        synapsesSpcd.add(DoubleArray(hiddenLayerNeuronCount * outputNeuronCount) { 0.0 })
    }

    fun output(): DoubleArray {
        for (i in 1 until neuronOutput.size) {
            for (k in neuronOutput[i].indices) {
                neuronOutput[i][k] = 0.0

                for (j in neuronOutput[i - 1].indices) {
                    neuronOutput[i][k] += neuronOutput[i - 1][j] * synapsesWeights[i - 1][j * neuronOutput[i].size + k]
                }

                neuronRawInput[i][k] = neuronOutput[i][k]
                neuronOutput[i][k] = sigmoid(neuronOutput[i][k] + neuronBiases[i][k])
            }
        }

        return neuronOutput.last().also { lastOutput = it }
    }

//    fun cost(): Double {
//
//        assert(expected.size == lastOutput.size)
//
//        var cost = 0.0
//
//        for (i in expected.indices) {
//            cost += (expected[i] - lastOutput[i]).pow(2.0)
//
//        }
//
//        return cost
//    }


//    CrossEntropycost
    fun cost(): Double {

        assert(expected.size == lastOutput.size)

        var exp_sum = 0.0
        for (i in expected.indices) {
//            cost += (expected[i] - lastOutput[i]).pow(2.0)
            exp_sum += Math.exp(lastOutput[i])
        }

        var p:DoubleArray = DoubleArray(lastOutput.size){0.0}
        for (i in expected.indices) {
            p[i] = Math.exp(lastOutput[i]) /exp_sum
        }
        Log.d("testNet", p[0].toString())

        var cost = 0.0

        for (i in expected.indices) {
//            cost += (expected[i] - lastOutput[i]).pow(2.0)
            cost -= expected[i] * Math.log(p[i])
        }

        return cost
    }


    fun backPropagation() {
        ////////////////
        //OUTPUT LAYER//
        ////////////////
        var synapseLayer = synapsesWeights.size - 1
        val layerId = neuronOutput.size - 1
        var neuronCount = neuronOutput[layerId].size

        debugPrintln("SL : $synapseLayer")

        for (j in synapsesWeights[synapseLayer].indices) {
            val neuronId = j % neuronCount
            val previousNeuronId = (j - neuronId) / neuronCount
            val currentWeight = synapsesWeights[synapseLayer][j]

            debugPrintln("Previous weight : $currentWeight")

            val oh = neuronOutput[layerId - 1][previousNeuronId]
//            val si = sigmoid(neuronRawInput[layerId][neuronId])
//            val sp = (si * (1 - si))
//            val cd = 2 * (lastOutput[neuronId] - expected[neuronId])
//            val spcd = sp * cd
            val spcd = lastOutput[neuronId] - expected[neuronId]

            synapsesSpcd[synapseLayer][j] = spcd

            val dw = oh * spcd
            val newWeight = currentWeight - learningRate * dw

            synapsesOldWeights[synapseLayer][j] = synapsesWeights[synapseLayer][j]
            synapsesWeights[synapseLayer][j] = newWeight

//            debugPrintln("OH: $oh | SI: $si | SP: $sp | CD: $cd")
            debugPrintln("New weight : " + synapsesWeights[synapseLayer][j])
        }

        for (i in neuronBiases[layerId].indices) {
            val currentBias = neuronBiases[layerId][i]

//            val si = sigmoid(neuronRawInput[layerId][i])
//            val sp = (si * (1 - si))
//            val cd = 2 * (lastOutput[i] - expected[i])
//            val dw = sp * cd
            val dw = lastOutput[i] - expected[i]
            val newBias = currentBias - learningRate * dw

            neuronBiases[layerId][i] = newBias

            debugPrintln("CurrentB: $currentBias | NewB: $newBias")
        }

        for (i in (neuronOutput.size - 2)..1) {
            synapseLayer = i - 1
            debugPrintln("SL: $synapseLayer")

            neuronCount = neuronOutput[i].size
            val nextNeuronCount = neuronOutput[i + 1].size

            debugPrintln(synapsesWeights[synapseLayer].indices)

            for (j in synapsesWeights[synapseLayer].indices) {
                val neuronId = j % neuronCount
                val previousNeuronId = (j - neuronId) / neuronCount
                val currentWeight = synapsesWeights[synapseLayer][j]

                debugPrintln("NID: $neuronId | PNID: $previousNeuronId")

                debugPrintln("Previous weight : $currentWeight")

                val oh = neuronRawInput[i - 1][previousNeuronId]
                val si = sigmoid(neuronOutput[i][neuronId])
//                val sp = (si * (1 - si))

//                debugPrintln("SP : $sp")

                var cd = 0.0

                debugPrintln("NNC: $nextNeuronCount")

                for (k in (0 until nextNeuronCount)) {
                    val sid = k + (nextNeuronCount * neuronId)
                    debugPrintln("$k : $sid")
                    debugPrintln("SOW : ${synapsesOldWeights[synapseLayer + 1][sid]}, SSPCD: ${synapsesSpcd[synapseLayer + 1][sid]}")

                    cd += synapsesOldWeights[synapseLayer + 1][sid] * synapsesSpcd[synapseLayer + 1][sid]
                    debugPrintln("CD : $cd")
                }

                debugPrintln("CD : $cd")

//                val dw = oh * sp * cd
                val dw = oh * cd

                debugPrintln("DW : $dw")

                val newWeight = currentWeight - learningRate * dw

                synapsesWeights[synapseLayer][j] = newWeight

//                debugPrintln("OH: $oh | SI: $si | SP: $sp | CD: $cd")
                debugPrintln("New weight : " + synapsesWeights[synapseLayer][j])
            }

            for (j in neuronBiases[i].indices) {
                val currentBias = neuronBiases[i][j]

//                val si = sigmoid(neuronRawInput[i][j])
//                val sp = (si * (1 - si))

                var cd = 0.0

                debugPrintln("NNC: $nextNeuronCount")

                for (k in (0 until nextNeuronCount)) {
                    val sid = k + (nextNeuronCount * j)
                    debugPrintln("$k : $sid")
                    debugPrintln("SOW : ${synapsesOldWeights[synapseLayer + 1][sid]}, SSPCD: ${synapsesSpcd[synapseLayer + 1][sid]}")

                    cd += synapsesOldWeights[synapseLayer + 1][sid] * synapsesSpcd[synapseLayer + 1][sid]
                    debugPrintln("CD : $cd")
                }

//                val dw = sp * cd
                val dw = cd
                val newBias = currentBias - learningRate * dw

                neuronBiases[i][j] = newBias

                debugPrintln("CurrentB: $currentBias | NewB: $newBias")
            }
        }
    }

    fun print() {
        debugPrintln("Input : ")
        debugPrintln(neuronOutput[0].joinToString(", "))
        debugPrintln()

        for (i in 1 until neuronOutput.size) {
            debugPrintln("Layer $i : ")

            debugPrintln(neuronOutput[i].joinToString(", "))
            debugPrintln(neuronBiases[i].joinToString(", "))

            debugPrintln()
            if (i != neuronOutput.size - 1) {
                debugPrintln("Synapses : ")

                debugPrintln(synapsesWeights[i - 1].joinToString(", "))

                debugPrintln()
            }
        }
    }

    fun setData(input: DoubleArray, expected: DoubleArray) {
        this.input = input
        this.expected = expected
    }

    companion object {
        val random = Random(0)
    }

}