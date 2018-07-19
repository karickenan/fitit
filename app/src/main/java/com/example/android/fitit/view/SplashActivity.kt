package com.example.android.fitit.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.android.fitit.misc.ACTIVITY
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_splash.*
import me.digi.sdk.core.DigiMeClient
import me.digi.sdk.core.SDKException
import me.digi.sdk.core.SDKListener
import me.digi.sdk.core.entities.CAAccounts
import me.digi.sdk.core.entities.CAFileResponse
import me.digi.sdk.core.entities.CAFiles
import me.digi.sdk.core.internal.AuthorizationException
import me.digi.sdk.core.session.CASession
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class SplashActivity : AppCompatActivity(), SDKListener {

    private var dgmClient: DigiMeClient? = null
    private var accountInfo: TextView? = null
    private val failedCount = AtomicInteger(0)
    private var allFiles = 0
    private val fileTypeMap = hashMapOf<String, ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        DigiMeClient.init(this@SplashActivity)

        dgmClient = DigiMeClient.getInstance()
        initTypeMap()
        dgmClient?.addListener(this@SplashActivity)
        dgmClient?.authorize(this, null)

        fitTitleImageView.isClickable = false

        fitTitleImageView.onClick {
            startActivity<NavigationActivity>(
                    "fitnessType" to fileTypeMap[ACTIVITY])
        }
    }

    private fun initTypeMap() {
        fileTypeMap[ACTIVITY] = ArrayList()
    }

    private fun loadFitnessData(view: View) {
        startActivity<NavigationActivity>(
                "fitnessType" to fileTypeMap[ACTIVITY])
        toast("Please wait while data loads")
//        fitTitleImageView.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dgmClient?.authManager?.onActivityResult(requestCode, resultCode, data)
    }

    /*
    SDK LISTENER METHODS
     */

    override fun authorizeSucceeded(session: CASession?) {
        Log.d(com.example.android.fitit.misc.TAG, "Session created with token: ${session?.sessionKey}")
//        statusText.text = getString(R.string.session_created)
//        toast("${R.string.session_created}")

        DigiMeClient.getInstance().getFileList(null)
    }

    override fun authorizeDenied(reason: AuthorizationException?) {
        Log.d(com.example.android.fitit.misc.TAG, "Failed to authorize session; Reason: ${reason?.throwReason?.name}")
//        statusText.text = getString(R.string.auth_declined)
        toast("${R.string.auth_declined}. Click on the title to start again")

//        mainBtn.visibility = View.VISIBLE
//        mainBtn.text = "Retry"
        fitTitleImageView.setOnClickListener {
            dgmClient?.addListener(this@SplashActivity)
            dgmClient?.authorize(this, null)
        }
    }

    override fun authorizeFailedWithWrongRequestCode() {
        Log.d(com.example.android.fitit.misc.TAG, "We received a wrong request code while authorization was in progress!")
    }

    override fun clientRetrievedFileList(files: CAFiles?) {
        DigiMeClient.getInstance().getAccounts(null)

//        statusText.text = String.format(Locale.getDefault(), "Downloaded : %d/%d", 0, files?.fileIds?.size)

        allFiles = files?.fileIds?.size!!

        customList(files.fileIds)

//        val progress = "File list retrieved: ${files.fileIds.size} file"
//        statusText.text = progress
//        mainBtn.visibility = View.VISIBLE
//        mainBtn.text = "Open test data"

//        statusText.text = "File list retrieved: ${fileTypeMap[ACTIVITY]?.size} files"
        toast("File list retrieved: ${fileTypeMap[ACTIVITY]?.size} files")
        fitTitleImageView.isClickable = true

        fitTitleImageView.setOnClickListener {
            loadFitnessData(it)
        }

        /*
         accountInfo.setText(R.string.fetch_accounts);
        //Fetch account metadata
        DigiMeClient.getInstance().getAccounts(null);

        downloadedCount.setText(String.format(Locale.getDefault(), "Downloaded : %d/%d", 0, files.fileIds.size()));
        allFiles = files.fileIds.size();
        for (final String fileId :
                files.fileIds) {
            counter.incrementAndGet();
            //Fetch content for returned file IDs
            DigiMeClient.getInstance().getFileJSON(fileId, null);
        }
        String progress = getResources().getQuantityString(R.plurals.files_retrieved, files.fileIds.size(), files.fileIds.size());
        statusText.setText(progress);
        gotoCallback.setVisibility(View.VISIBLE);
         */

    }

    override fun contentRetrievedForFile(fileId: String?, caFileResponse: CAFileResponse?) {
        Log.v(com.example.android.fitit.misc.TAG, caFileResponse?.fileContent.toString())
    }

    override fun contentRetrieveFailed(fileId: String?, reason: SDKException?) {
        Log.d(com.example.android.fitit.misc.TAG, "Failed to retrieve file content for file: $fileId; Reason: $reason")
        failedCount.incrementAndGet()
//        updateCounters()
    }

    override fun accountsRetrieved(accounts: CAAccounts?) {
        // NOTHING
    }

    override fun accountsRetrieveFailed(reason: SDKException?) {
        Log.d(com.example.android.fitit.misc.TAG, "Failed to retrieve account details for session. Reason: $reason")
    }

    override fun sessionCreated(session: CASession?) {
        Log.d(com.example.android.fitit.misc.TAG, "Session created with token: ${session?.sessionKey}")

//        statusText.text = getString(R.string.session_created)
//        toast("${R.string.session_created}")
    }

    override fun sessionCreateFailed(reason: SDKException?) {
        Log.d(com.example.android.fitit.misc.TAG, reason?.message)
    }

    override fun clientFailedOnFileList(reason: SDKException?) {
        Log.d(com.example.android.fitit.misc.TAG, "Failed to retrieve file list: ${reason?.message}")
        accountInfo?.text = getString(R.string.account_fail)
    }

    override fun jsonRetrievedForFile(fileId: String?, content: JsonElement?) {
        Log.d(com.example.android.fitit.misc.TAG, content.toString())
//        updateCounters()
    }

    private fun customList(files: List<String>) {
        var stringParts: List<String>
        var groupType: String

        for (fieldId in files) {
            stringParts = fieldId.split("_")
            groupType = stringParts[4]
            fileTypeMap[groupType]?.add(fieldId)
        }

        Log.d(com.example.android.fitit.misc.TAG, fileTypeMap[ACTIVITY].toString())
    }
}
