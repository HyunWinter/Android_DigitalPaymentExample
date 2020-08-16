package com.hyun.digitalpayment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ProductAdapter.GoogleClickListener, PaymentHelper {

    // Variables
    private lateinit var mProductAdapter : ProductAdapter
    private lateinit var mProductList : ArrayList<ProductModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setRVItems()
        this.setContents()
        this.setPayment()
    }

    private fun setRVItems() {

        val item1 = ProductModel(
            R.drawable.image_item1,
           "Chocolate Cookies",
            2,
            "Ounce",
            36,
            4.5,
            18.99
        )

        val item2 = ProductModel(
            R.drawable.image_item2,
            "Macarons",
            12,
            "Count",
            1,
            4.0,
            24.00
        )

        this.mProductList = arrayListOf(item1, item2)
        this.mProductAdapter = ProductAdapter(this.mProductList, this)
    }

    private fun setContents() {

        // Toolbar
        setSupportActionBar(this.Main_Toolbar)

        // Recycler View
        this.Main_RecyclerView.setHasFixedSize(true)
        this.Main_RecyclerView.layoutManager = LinearLayoutManager(this)
        this.Main_RecyclerView.adapter =this.mProductAdapter


    }

    // Payment Variables
    companion object {
        private const val TAG = "Payment Activity"
        private const val LOAD_PAYMENT_DATA_REQUEST_CODE : Int = 991
    }

    private lateinit var mPaymentsClient : PaymentsClient
    private lateinit var mReadyToPayRequest : IsReadyToPayRequest
    private lateinit var mReadyToPayTask : Task<Boolean>
    private lateinit var mPaymentDataRequest : PaymentDataRequest

    private fun setPayment() {

        this.mPaymentsClient = createPaymentsClient(this)
        this.mReadyToPayRequest = getReadyToPayRequest()
    }

    override fun onButtonSelected(position : Int) {

        this.mReadyToPayTask = this.mPaymentsClient
            .isReadyToPay(this.mReadyToPayRequest)
            .addOnSuccessListener {
                requestPayment(this.mProductList[position].price.toFloat())
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Not Ready To Pay: $exception")
            }
    }

    private fun requestPayment(price : Float) {
        this.mPaymentDataRequest = PaymentDataRequest
            .fromJson(getPaymentDataRequestJson(price).toString())
        val request = PaymentDataRequest
            .fromJson(this.mPaymentDataRequest.toString())

        if (request != null) {
            AutoResolveHelper.resolveTask(
                this.mPaymentsClient.loadPaymentData(this.mPaymentDataRequest),
                this,
                LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data : Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        PaymentData.getFromIntent(data!!)?.let(::handlePaymentSuccess)
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user cancelled without selecting a payment method.
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
            }
        }
    }

    private fun handlePaymentSuccess(paymentData : PaymentData) {
        Log.e(TAG,"Handle Payment Success")
    }

    private fun handleError(statusCode: Int) {
        Log.w(TAG, String.format("loadPaymentData failed: %d", statusCode))
    }
}