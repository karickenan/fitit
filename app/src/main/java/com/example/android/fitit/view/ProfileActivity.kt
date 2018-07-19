package com.example.android.fitit.view

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import com.example.android.fitit.FitnessDataAdapter
import com.example.android.fitit.misc.ACTIVITY
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_profile.*
import me.digi.sdk.core.DigiMeClient
import me.digi.sdk.core.SDKCallback
import me.digi.sdk.core.SDKException
import me.digi.sdk.core.SDKResponse
import org.jetbrains.anko.doAsync


class ProfileActivity : AppCompatActivity() {

    private var profileFitnessList = ArrayList<String>()
    private lateinit var context: Context
    private val jsonBodies = ArrayList<JsonObject>()
    private var dummyFitnessData: HashMap<String, ArrayList<String>>? = hashMapOf()
    private var anotherFitnessData = ArrayList<JsonObject>()
    private var stepsList = ArrayList<String>()
    private var caloriesList = ArrayList<String>()
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileFitnessList = intent.getStringArrayListExtra("fitnessDataProfile")

        context = this

        Log.d("Fitness Intent", profileFitnessList.toString())

        initSocialFiles()

        for (fileId in profileFitnessList) requestData().execute(fileId)
        Log.d("Fitness list: ", profileFitnessList.toString())

        Log.d("Dummy fitness data ", dummyFitnessData.toString())

        val snapHelper = GravitySnapHelper(Gravity.TOP)
        snapHelper.attachToRecyclerView(recyclerview_profile)

        recyclerview_profile.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = FitnessDataAdapter(anotherFitnessData)
            hasFixedSize()
        }
    }

    private fun initSocialFiles() {
        dummyFitnessData = hashMapOf(ACTIVITY to ArrayList())
    }

    internal inner class requestData : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg fileId: String?): Void? {
            DigiMeClient.getInstance().getFileJSON(fileId[0], object : SDKCallback<JsonElement>() {
                override fun succeeded(response: SDKResponse<JsonElement>?) {
                    count += 1

                    val jsonObject = response?.body?.asJsonObject

                    val jsonContent = jsonObject?.get("fileContent")?.asJsonArray

                    // Gets only first object in the array
                    val jsonBody = jsonContent?.get(0)?.asJsonObject
//
                    for (i in 0 until jsonContent?.size()!!) {
                        val jsonBoody = jsonContent.get(i)?.asJsonObject
//                        Log.d("JsonBody $i", jsonBoody.toString())
                        anotherFitnessData.add(jsonBoody!!)
                        val stepsValue = jsonBoody.get("steps")
//                        Log.d("Steps", stepsValue.asString)
                        stepsList.add(stepsValue.toString())
//                        Log.d("Steps list", stepsList.toString())

                        val stepping = stepsList.map { it.toInt() }.sum()
//                        Log.d("Counting", stepping.toString())
                        total_steps_txtView.text = "Total number of steps: $stepping"

                        val caloriesValue = jsonBoody.get("calories")
//                        Log.d("Calories", caloriesValue.asString)
                        caloriesList.add(caloriesValue.toString())
//                        Log.d("Calories list", caloriesList.toString())

                        val calorie = caloriesList.map { it.toInt() }.sum()
                        total_calories_txtView.text = "Total number of calories: $calorie"

                    }

//                    Log.d("Size: ", jsonContent.size().toString())
//                    Log.d("Size json content : ", anotherFitnessData.size.toString())

                    jsonBodies.add(jsonBody!!)

//                    Log.d("Json Object", "$jsonObject")
//                    Log.d("Json Bodies", "$jsonBodies")
//                    Log.d("Json Content", "${jsonContent}")
//                    jsonBodiesTxt.text = "$jsonBodies"
//                    jsonBodiesTxt.movementMethod = ScrollingMovementMethod()

//
//                    Log.d("Json Body", jsonBody.toString())
//                    jsonBodyTxt.text = jsonBody.toString()
//                    jsonBodyTxt.movementMethod = ScrollingMovementMethod()

//                    Log.d("Json Content", "$jsonContent")
//                    jsonContentTxt.text = "$jsonContent\n"
//                    jsonContentTxt.movementMethod = ScrollingMovementMethod()

                    checkProgress()

                    Log.d("Success: ", count.toString())
                }

                override fun failed(reason: SDKException?) {
                    Log.d("Error: ", reason?.message)
                    count += 1
                    checkProgress()
                    Log.d("Failed: ", count.toString())
                }
            })
            return null
        }
    }

    private fun checkProgress() {
        if (count == profileFitnessList.size) {
            Log.d("AAAAA", count.toString())
//
            progressBar?.visibility = View.GONE
            loadingTxt?.visibility = View.GONE

            val fitnessAdapter = FitnessDataAdapter(anotherFitnessData)

            doAsync {
                runOnUiThread {
                    recyclerview_profile?.adapter = fitnessAdapter
                }
            }
        } else {
            loadingTxt.text = "Currently loading data: $count of ${profileFitnessList.size} files"
//            toast("Currently loading data: $count of ${jsonBodies.size}")
        }
    }
}
