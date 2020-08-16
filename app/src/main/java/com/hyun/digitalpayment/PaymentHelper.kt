package com.hyun.digitalpayment

import android.app.Activity
import android.util.Log
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONObject

interface PaymentHelper {

    fun createPaymentsClient(activity : Activity) : PaymentsClient {
        val walletOptions = Wallet
            .WalletOptions
            .Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    // To operate in the ENVIRONMENT_PRODUCTION environment, you need a Google Pay merchant identifier.
    // Obtain one by creating a new account on the Google Pay Business Console.
    // https://pay.google.com/business/console?utm_campaign=business_console_launch_031920&utm_source=gdev&utm_medium=codelab

    private fun getBaseCardPaymentMethod() : JSONObject {
        return JSONObject()
            .apply {
                put("type", "CARD")
                put("parameters", JSONObject().apply {
                    put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
                    put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
                })
            }
    }

    private fun getGooglePayBaseConfiguration() : JSONObject {
        return JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods",  JSONArray().put(getBaseCardPaymentMethod()))
        }
    }

    fun getReadyToPayRequest() : IsReadyToPayRequest {
        return IsReadyToPayRequest
            .fromJson(getGooglePayBaseConfiguration().toString())
    }

    private fun getTokenizationSpecification() : JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters",
                JSONObject(
                    mapOf(
                        "gateway" to "example",
                        "gatewayMerchantId" to "exampleGatewayMerchantId"
                    )
                )
            )
        }
    }

    private fun getCardPaymentMethod() : JSONObject {
        return JSONObject().apply {
            put("type", "CARD")
            put("tokenizationSpecification", getTokenizationSpecification())
            put("parameters", JSONObject().apply {
                put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
                put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject(mapOf("format" to "FULL")))
            })
        }
    }

    private fun getTransactionInfo(price : Float) : JSONObject {
        return JSONObject().apply {
            put("totalPrice", price.toString())
            put("totalPriceStatus", "FINAL")
            put("currencyCode", "USD")
        }
    }

    private fun getMerchantInfo() : JSONObject {
        return JSONObject().apply {
            put("merchantName", "Example Merchant")
            put("merchantId", "01234567890123456789")
        }
    }

    fun getPaymentDataRequestJson(price : Float) : JSONObject {
        return JSONObject(getGooglePayBaseConfiguration().toString()).apply {
            put("allowedPaymentMethods", JSONArray().put(getCardPaymentMethod()))
            put("transactionInfo", getTransactionInfo(price))
            put("merchantInfo", getMerchantInfo())
        }
    }
}