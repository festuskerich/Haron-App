package com.example.realestate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.example.realestate.databinding.ActivityMainBinding

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    lateinit var binding: ActivityMainBinding
    private var bp: BillingProcessor? = null
    var purchaseTransactionDetails: TransactionDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bp = BillingProcessor(this, getString(R.string.licence), this)
        bp?.initialize()
    }

    private fun hasSubscription(): Boolean {
        return if (purchaseTransactionDetails != null) {
            purchaseTransactionDetails?.purchaseInfo != null
        } else false
    }

    override fun onBillingInitialized() {
        val premium = resources.getString(R.string.premium)
        purchaseTransactionDetails = bp?.getSubscriptionTransactionDetails(premium)
        bp?.loadOwnedPurchasesFromGoogle()

        binding.apply {
            one.setOnClickListener {
                subscription_status(premium)
            }
            two.setOnClickListener {
                subscription_status(premium)
            }
            three.setOnClickListener {
                subscription_status(premium)
            }
            four.setOnClickListener {
                subscription_status(premium)
            }
            five.setOnClickListener {
                subscription_status(premium)
            }

        }

        if (hasSubscription()) {
            makeText(this, "Already Subscribed", LENGTH_SHORT).show()
        } else {
            makeText(this, "Not Subscribed", LENGTH_SHORT).show()
        }
    }

    private fun subscription_status(subscrition_id: String) {
        if (bp?.isSubscriptionUpdateSupported == true) {
            bp?.subscribe(this, subscrition_id)
        } else {
            Log.d("MainActivity", "onBillingInitialized: Subscription updated is not supported")
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        Log.d(TAG, "onProductPurchased: ")
    }

    override fun onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ")
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.d(TAG, "onBillingError: ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp?.handleActivityResult(requestCode, resultCode, data)!!) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        bp?.release()
        super.onDestroy()
    }

}