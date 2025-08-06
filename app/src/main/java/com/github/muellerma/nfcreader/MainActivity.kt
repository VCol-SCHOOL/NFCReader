package com.github.muellerma.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import java.util.*

import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var tagList: LinearLayout? = null
    var nfcAdapter: NfcAdapter? = null

    //Views
    lateinit var successView: View //for api success go to success layout
    lateinit var submitButton: Button
    lateinit var returnButton: Button
    lateinit var returnButtonEr: Button
    lateinit var backButton: Button
    lateinit var emailInput: EditText
    lateinit var exView: View
    lateinit var howTo: View
    lateinit var errorPg: View
    lateinit var footerMenu: LinearLayout
    lateinit var helpButton: ImageButton

    //Fields for DB and to display
    private lateinit var type_text: String
    private lateinit var tech_text: String
    private lateinit var serial_text: String
    private lateinit var sig_text: String
    private lateinit var record_text: String

    //Reference to Api to submit said data
    private lateinit var api: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tagList = layoutInflater.inflate(R.layout.activity_main, null) as LinearLayout
        tagList!!.setId(View.generateViewId())
        setContentView(tagList)

        footerMenu = tagList!!.findViewById(R.id.tapyoca_footer_wpg) as LinearLayout
        helpButton =  footerMenu.findViewById(R.id.menu_btn)

        exView = layoutInflater.inflate(R.layout.submit_forum, null) as View
        tagList!!.addView(exView)
        exView.setId(View.generateViewId())
        emailInput = exView.findViewById(R.id.emailInput)
        submitButton = exView.findViewById(R.id.submitButton)
        exView.visibility = View.GONE

        howTo = layoutInflater.inflate(R.layout.how_to_tap, null) as LinearLayout
        tagList!!.addView(howTo)
        howTo.setId(View.generateViewId())
        returnButton = howTo.findViewById(R.id.returnButtonHT)
        howTo.visibility = View.GONE


        errorPg = layoutInflater.inflate(R.layout.error_page, null) as View
        tagList!!.addView(errorPg)
        errorPg.setId(View.generateViewId())
        returnButtonEr = errorPg.findViewById(R.id.returnButton)
        errorPg.visibility = View.GONE

        successView = layoutInflater.inflate(R.layout.success_page, null) as View
        tagList!!.addView(successView)
        successView.setId(View.generateViewId())
        backButton = successView.findViewById(R.id.returnButton)
        successView.visibility = View.GONE


        //https://api.tapyoca.app/tech-support/index.php
        api = Retrofit.Builder()
            .baseUrl("https://api.tapyoca.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        resolveIntent(intent)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            showNoNfcDialog()
            return
        }

        submitButton.setOnClickListener {
            if (emailInput.text.isNullOrEmpty()) {
                Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "No, not yet", Toast.LENGTH_SHORT).show()
                sendDataToApi(emailInput.text.toString())
                sendDataToApi(emailInput.text.toString())
            }
        }

        helpButton.setOnClickListener {
            howTo.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            successView.visibility = View.GONE
        }

        returnButton.setOnClickListener {
            howTo.visibility = View.GONE

        }

        returnButtonEr.setOnClickListener {
            errorPg.visibility = View.GONE
        }
    }

    fun sendDataToApi(email: String) {
        val request = TechSupportRequest(
            email = email,
            uID = serial_text,
            //tech = tech_text,
            //sig = sig_text,
            record = record_text,
            type = type_text,
            liveID = 0
        )

        try{
            api.sendTechSupport(request).enqueue(object : Callback<TechSupportRequest> {
                override fun onResponse(call: Call<TechSupportRequest>, response: Response<TechSupportRequest>) {
                    if (response.isSuccessful /*&& response.body()?.success == true*/) {
                        exView.visibility = View.GONE
                        successView.visibility = View.VISIBLE

                    } else {
                        Toast.makeText(this@MainActivity, "Failed to send data.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<TechSupportRequest>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
        catch(e: Exception){
            Log.e("AppCrash", "UI update failed", e)
        }
    }

    override fun onResume() {
        super.onResume()
        if (nfcAdapter?.isEnabled == false) {
            openNfcSettings()
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent_Mutable
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resolveIntent(intent)
    }

    private fun showNoNfcDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.no_nfc)
            .setNeutralButton(R.string.close_app) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun openNfcSettings() {
        val intent = Intent(Settings.Panel.ACTION_NFC)
        startActivity(intent)
    }

    private fun resolveIntent(intent: Intent) {
        val validActions = listOf(
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED
        )
        if (intent.action in validActions) {
            // TODO
            val rawMsgs = IntentCompat.getParcelableArrayExtra(intent, NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
            val messages = mutableListOf<NdefMessage>()
            if (rawMsgs != null) {
                rawMsgs.forEach {
                    messages.add(it as NdefMessage)
                }
                // Setup the views
                buildTagViews(messages)
            } else {
                errorPg.visibility = View.VISIBLE
            }
        }
    }

    private fun buildTagViews(msgs: List<NdefMessage>) {
        if (msgs.isEmpty()) {
            return
        }

        val tag = intent.parcelable<Tag>(NfcAdapter.EXTRA_TAG) ?: return
        //var specStr = StringBuilder()
        val conn =  NfcA.get(tag)

        //Tag Type
        conn.connect()
        var cmd = byteArrayOf(0x60)
        var result = conn.transceive(cmd)
        conn.close()
        //var rawTagInfo = ""
        if(result != null){
            val verString =
                result.joinToString(separator = ":") { eachByte -> "%02x".format(eachByte) }
            //rawTagInfo = "Raw tag info: "+verString+"\n"
            when(verString.substring(18,20)){
                "0F" -> type_text = "NTAG213"
                "11" -> type_text = "NTAG215"
                "13" -> type_text = "NTAG216"
            }
        }
        else {
            type_text = "NA"
        }

        //Tech available
        //val techFound = TextView(this)
        //built similar to dumpTagData
        val sb = StringBuilder()
        val prefix = "android.nfc.tech."
        sb.append("[")
        for (tech in tag.techList) { //uses tag read from line 237
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        sb.append("]")
        tech_text = sb.toString()

        //serial number
        //val serialNo = TextView(this)
        val sN = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
        serial_text = "NA" //place holder if not found
        if(sN != null){
            serial_text = sN.joinToString(separator = ""){ eachByte -> "%02x".format(eachByte) }
        }

        //Signature
        conn.connect()
        cmd = byteArrayOf(0x3C, 0x00)
        result = conn.transceive(cmd)
        conn.close()
        if(result != null){
            sig_text = result.joinToString(separator = ""){ eachByte -> "%02x".format(eachByte)}
        }
        else {
            sig_text = "NA"
        }

        //First Record
        val ndef = Ndef.get(tag)
        ndef?.connect() //It got it, just without the https
        ndef?.cachedNdefMessage?.records?.firstOrNull()?.let { record ->
            record_text = URI_PREFIX_MAP[record.payload[0]]!!+String(record.payload, Charsets.UTF_8)
                .removePrefix("\u0002en")
        }
        ndef?.close()
        exView.visibility = View.VISIBLE // submition forum

        //parse function leads to NdefMessageParser, and will sort out if a tag record is a
        //url, text, or smart text
    }

}